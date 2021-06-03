# Deploying Machine Learning Model on Google Cloud Function

## Overview
The machine learning model with file extension (.tflite) is uploaded to the bucket on cloud storage, then run the cloud function with the script provided by the machine learning team. Then deploy serverless in the cloud function. then the output value will be directly integrated with Cloud Firestore on firebase and used by the mobile app when scanning.

