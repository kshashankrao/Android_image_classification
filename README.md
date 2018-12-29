Android Image Classfication application

Aim: To build an android application to classify images.

Tools used: Tensorflow Java API, Firebase, listview, webview API, Camera API, texttospeech API

Description:

Trained a model using mobilenet, tensorflow and converted to android compatible version. 
Used Android Camera API to caputre image and convert to BITmap and pass to the imageclassifier class, that contains tflite interpreter.
Search results of the predicted output.
Voice translation of the predicted output.
The predictions are stored on Firebase realtime database to view the results later.
Image is stored on tehe local storage.
The history can be viewed as a listview.


Reference:

https://www.tensorflow.org/lite/demo_android
https://firebase.google.com/

