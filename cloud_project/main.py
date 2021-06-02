import csv
import os
import re
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from keras.preprocessing import image
import cv2
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import PIL.Image
import zbar
import zbar.misc
import pathlib
from skimage.io import imread as read_image

## Global variable
model = None  #model
db = None
image_path = ""
id = ""
fn = ""
value = ""

def run_tflite_model(tflite_file, test_image):

    interpreter = tf.lite.Interpreter(model_path=str(tflite_file))
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()[0]
    output_details = interpreter.get_output_details()[0]

    interpreter.set_tensor(input_details["index"], test_image)
    interpreter.invoke()
    output = interpreter.get_tensor(output_details["index"])[0]

    prediction = output.argmax()

    return prediction

def rotate(image, angle):
    """
    Rotates an OpenCV 2 / NumPy image about it's centre by the given angle
    (in degrees). The returned image will be large enough to hold the entire
    new image, with a black background

    Source: http://stackoverflow.com/questions/16702966/rotate-image-and-crop-out-black-borders
    """
    # Get the image size
    # No that's not an error - NumPy stores image matricies backwards
    image_size = (image.shape[1], image.shape[0])
    image_center = tuple(np.array(image_size) / 2)

    # Convert the OpenCV 3x2 rotation matrix to 3x3
    rot_mat = np.vstack(
        [cv2.getRotationMatrix2D(image_center, angle, 1.0), [0, 0, 1]]
    )

    rot_mat_notranslate = np.matrix(rot_mat[0:2, 0:2])

    # Shorthand for below calcs
    image_w2 = image_size[0] * 0.5
    image_h2 = image_size[1] * 0.5

    # Obtain the rotated coordinates of the image corners
    rotated_coords = [
        (np.array([-image_w2,  image_h2]) * rot_mat_notranslate).A[0],
        (np.array([ image_w2,  image_h2]) * rot_mat_notranslate).A[0],
        (np.array([-image_w2, -image_h2]) * rot_mat_notranslate).A[0],
        (np.array([ image_w2, -image_h2]) * rot_mat_notranslate).A[0]
    ]

    # Find the size of the new image
    x_coords = [pt[0] for pt in rotated_coords]
    x_pos = [x for x in x_coords if x > 0]
    x_neg = [x for x in x_coords if x < 0]

    y_coords = [pt[1] for pt in rotated_coords]
    y_pos = [y for y in y_coords if y > 0]
    y_neg = [y for y in y_coords if y < 0]

    right_bound = max(x_pos)
    left_bound = min(x_neg)
    top_bound = max(y_pos)
    bot_bound = min(y_neg)

    new_w = int(abs(right_bound - left_bound))
    new_h = int(abs(top_bound - bot_bound))

    # We require a translation matrix to keep the image centred
    trans_mat = np.matrix([
        [1, 0, int(new_w * 0.5 - image_w2)],
        [0, 1, int(new_h * 0.5 - image_h2)],
        [0, 0, 1]
    ])

    # Compute the tranform for the combined rotation and translation
    affine_mat = (np.matrix(trans_mat) * np.matrix(rot_mat))[0:2, :]

    # Apply the transform
    result = cv2.warpAffine(
        image,
        affine_mat,
        (new_w, new_h),
        flags=cv2.INTER_LINEAR
    )

    return result


def angle_error(y_true, y_pred):
    """
    Calculate the mean diference between the true angles
    and the predicted angles. Each angle is represented
    as a binary vector.
    """
    diff = angle_difference(K.argmax(y_true), K.argmax(y_pred))
    return K.mean(K.cast(K.abs(diff), K.floatx()))

# Download model file from cloud storage bucket
def download_model_file():

    from google.cloud import storage

    # Model Bucket details
    BUCKET_NAME        = "model-predict"
    PROJECT_ID         = "careful-muse-313003"
    GCS_MODEL_FILE     = "barcode.tflite"

    # Initialise a client
    client   = storage.Client(PROJECT_ID)
    
    # Create a bucket object for our bucket
    bucket   = client.get_bucket(BUCKET_NAME)
    
    # Create a blob object from the filepath
    blob     = bucket.blob(GCS_MODEL_FILE)
    
    folder = '/tmp/'
    if not os.path.exists(folder):
        os.makedirs(folder)
    # Download the file to a destination
    blob.download_to_filename(folder + "model.tflite")

def download_image(event, context):

    from google.cloud import storage

    file = event
    # Model Bucket details
    BUCKET_NAME        = "careful-muse-313003.appspot.com"
    PROJECT_ID         = "careful-muse-313003"
    GCS_IMAGE_FILE     = file['name']

    # Initialise a client
    client   = storage.Client(PROJECT_ID)
    
    # Create a bucket object for our bucket
    bucket   = client.get_bucket(BUCKET_NAME)
    
    # Create a blob object from the filepath
    blob     = bucket.blob(GCS_IMAGE_FILE)
    
    folder = '/tmp/'
    if not os.path.exists(folder):
        os.makedirs(folder)
    # Download the file to a destination
    global image_path
    image_path = folder + GCS_IMAGE_FILE
    blob.download_to_filename(image_path)

    #adding function split name
    global fn
    global id
    fn= file['name']
    id = fn.split('-') #use id[-2]


def farmacode(event, context):
    
    #download image to predict
    download_image(event, context)
    
    #initialize firestore
    global db
    if not db:
        # Use the application default credentials
        cred = credentials.ApplicationDefault()
        firebase_admin.initialize_app(cred, {
            'projectId': 'careful-muse-313003',
        })
    
    db = firestore.client()

    barcode_image = read_image(image_path)
    image_as_numpy_array = zbar.misc.rgb2gray(barcode_image)

    result1 = []
    global value
    global model
    scanner = zbar.Scanner()
    results = scanner.scan(image_as_numpy_array)
    if results == []:
        if not model:
            download_model_file()
            model = tf.lite.Interpreter('/tmp/model.tflite')
    
        img = image.load_img(image_path, target_size=(224, 224))
        x = image.img_to_array(img)
        x = np.expand_dims(x, axis=0).astype(np.float32)

        model.allocate_tensors()
        input_details = model.get_input_details()[0]
        output_details = model.get_output_details()[0]
        model.set_tensor(input_details["index"], x)
        model.invoke()
        output = model.get_tensor(output_details["index"])[0]
        predection = output.argmax()
        rot_image = rotate(image_as_numpy_array,-int(predection))
        results1 = scanner.scan(rot_image)

        if results1 == []:
            value = ""
        else:
            value = results1[0].data.decode("utf-8")
    else:
        value = results[0].data.decode("utf-8")

    #update firestore
    doc_ref = db.collection(u'farmacode-classification').document(id[-2])
    doc_ref.set({
        fn.strip('.jpg').strip('.jpeg').strip('.png'): value
    }, merge=True)
    print(fn)
    print(value)
