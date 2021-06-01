# Predict Rotation Angle of an Barcode or Image

## Overview
This repository contains the code necessary to train and test convolutional neural networks (CNNs) for predicting the rotation angle of an barcode or image to correct its orientation. There are notebook to Train, Test and convert to .pb or .tflite format. You can install all the required packages using pip: `pip install -r requirements.txt`

## Model
1. You can download model at this link : [Model_Barcode](https://drive.google.com/drive/folders/1aOCx-ue_KW6DiP3RhvN4sY7JddFQhJS-?usp=sharing)
2. You can download pb format model at this link : [Model.Pb_Barcode](https://drive.google.com/drive/folders/1QyvLv1E6k6xUORvP4CdDCFQkL6xITxef?usp=sharing) (Use Bangkit Account)

## Dataset
Because we lacked the dataset, therefore we combined the streetview and barcode datasets.
1. [Barcode dataset](https://uni-muenster.sciebo.de/s/8KC6sVAuL2BzCXS/download)
2. [Google Street View dataset](http://www.cs.ucf.edu/~aroshan/index_files/Dataset_PitOrlManh/zipped%20images/)

Then for the barcode dataset we have to select the barcode again with an angle of 0 degrees [Barcode 0 degree dataset](https://drive.google.com/drive/folders/1dn9sehLYJ2d4NqDbsyOMW0rL6oPE6B9k?usp=sharing)

## Train
Run notebook `Train_barcode.ipynb` to train on the Barcode+Google Street View dataset. you will need a decent GPU to train the *ResNet50* model that is used in `Train_barcode.ipynb`, otherwise it will take quite long to finish.

## Test
Run notebook `test_barcode.ipynb` to evaluate the models and display examples using the provided Jupyter or colab notebooks. Angle error this model ranged 1-6 degree.

## Convert to pb or tflite
Run notebook `convert_keras_models_to_frozen_graph_n_pb.ipynb` to convert pb format and `convert_model_to_TFLite.ipynb` to convert tflite format.
