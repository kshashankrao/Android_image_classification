package com.example.android.tflitecamerademo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import static android.content.ContentValues.TAG;
public class MainActivity extends Activity {
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 8675309;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static String result;
    public static String[] words;
    public static String prob ="";
    public static String text=" ";
    public ImageClassifier classifier;
    static final String url = "https://www.google.com/search?q=";
    ImageView imageView;
    EditText editText;
    TextView textView7;
    WebView webView;
    Button button3;
    ImageButton button;
    ImageButton imageButton;
    ImageButton imageButton5;
    TextToSpeech textToSpeech;
    DatabaseReference dbRef;
    ArrayAdapter<String> adapter;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (ImageButton) findViewById(R.id.button);
        button3 = (Button) findViewById(R.id.button3);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton5 = (ImageButton) findViewById(R.id.imageButton5);
        imageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.editText);
        textView7 =(TextView) findViewById(R.id.textView7);
        textView7.setText("Usage: \n1. Click on the camera button to classify the input image \n2. Click on the microphone button to hear the the inference result " +
                "\n3. Click on the search button to view more information about the infered output \n4. Click on history to see previous outputs  \n \n \n \n"+
                "Features: \n1. Classify images captured by the camera \n2. Upload the result on Firebase and save the image on  phone storage \n3. Voice output of the infered output " +
                "\n4. Search Results of the infered output label \n5. History of the results can be viewed  \n \n \n \nAuthor: Kadapanatham Shashank Rao");
        editText.setVisibility(View.INVISIBLE);
        imageButton.setVisibility(View.INVISIBLE);
        imageButton5.setVisibility(View.INVISIBLE);
        button3.setVisibility(View.INVISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        dbRef = database.getReference("/test/data/label");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View view) {
                text = "";
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                else {
                    String[] permissionRequest = {Manifest.permission.CAMERA};
                    requestPermissions(permissionRequest,CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });
        textToSpeech =new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
          @Override
          @TargetApi(Build.VERSION_CODES.M)
          public void onClick(View view) {

              textToSpeech.speak(String.format("The label is %s and Probabiltiy is %s",text,prob), TextToSpeech.QUEUE_FLUSH, null, null);
             }
        });
        button3.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
           Intent intent_History = new Intent(MainActivity.this,History.class);
           startActivity(intent_History);
              }
           });
        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchResult.class);
                intent.putExtra("result",text);
                startActivity(intent);startActivity(intent);
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            else{
                Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            classifier = new ImageClassifier(this);
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialize an image classifier.");
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitMap =  Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(),
                    matrix, true);
            imageView.setImageBitmap(rotatedBitMap);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(rotatedBitMap, 224, 224, false);
            result = classifier.classifyFrame(resizedBitmap);
            words = result.split(" ");
            for(int i =0;i<words.length;i++)
            {
                if(words[i].matches(".*\\d.*"))
                {
                    prob = words[i];
                }
                else
                {
                    text = text+" "+words[i] ;
                }
            }
            String results = "Label: "+text+"\nProb: "+prob;
            textView7.setVisibility(View.INVISIBLE);
            editText.setVisibility(View.VISIBLE);
            editText.setFocusable(false);
            editText.setText(results);
            imageButton.setVisibility(View.INVISIBLE);
            imageButton.setVisibility(View.VISIBLE);
            imageButton5.setVisibility(View.VISIBLE);
            button3.setVisibility(View.VISIBLE);
            dbRef.child(text).setValue(text+" "+prob);
            saveToInternal(text+" "+prob,rotatedBitMap,getApplicationContext());
        }
    }
        private boolean saveToInternal(String filename, Bitmap bitmapImage, Context context)
        {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            try {
                FileOutputStream file = context.openFileOutput(filename, Context.MODE_PRIVATE);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, file);
                String path = context.getFilesDir().getAbsolutePath();
                file.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    @Override
    protected void onPause() {
        imageView.setImageURI(null);
        super.onPause();
    }
}



