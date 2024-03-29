# Deploying Machine Learning Model on Google Cloud Function

## Overview
The machine learning model with file extension (.tflite) is uploaded to the bucket on cloud storage, then run the cloud function with the script provided by the machine learning team. Then deploy serverless in the cloud function. then the output value will be directly integrated with Cloud Firestore on firebase and used by the mobile app when scanning.

![tag alt](https://github.com/MrMG23/Farmacode/blob/main/cloud_project/image/flow.jpg)
## Step 0 : Prepare saved model of Machine Learning
Preparing machine learning model with file extension (.tflite) from Machine Learning team.
## Step 1 : Upload model to Google Cloud Storage Bucket
Move to navigation menu, then search Cloud Storage and then create new bucket. For this storage, the name of bucket is `model-prediction`. After create bucket, then upload file machine learning (.tflite) and wait until uploaded.

## Step 2 : Creating and Configuration Cloud Function
There are configuration of Cloud Function:
- Function name = `farmacode-function`
- region = `asiasoutheast2` [Region](https://cloud.google.com/functions/docs/locations)
- Memory allocated= `1 GB`
- timeout= `60 seconds` (The function execution timeout in seconds)
- After this, for next step is code. For code use python 3.7, for details code in `main.py` and `requirements.txt`
After this, [deploy cloud function](https://cloud.google.com/functions/docs/deploying) 
Machine Learning model has deployed and integrate to firebase so can use in mobile app.
