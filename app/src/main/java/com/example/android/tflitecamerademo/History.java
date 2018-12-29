package com.example.android.tflitecamerademo;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class History extends Activity {
    ListView listview;
    ImageView imageView2;
    ArrayList<String> list=new ArrayList<>();
    final ArrayList<String> keyList = new ArrayList<>();
    DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        listview=(ListView)findViewById(R.id.listView);
        imageView2=(ImageView)findViewById(R.id.imageView2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        dbRef = database.getReference("/test/data/label");
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,list);
        listview.setAdapter(adapter);
        imageView2.setVisibility(View.INVISIBLE);
        String path = getApplicationContext().getFilesDir().getAbsolutePath();


        dbRef.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            list.add(dataSnapshot.getValue(String.class));
            keyList.add(dataSnapshot.getKey());
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            list.remove(dataSnapshot.getValue(String.class));
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
         }
         });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {

            String imageDelete = path +'/'+ list.get(position);
            list.remove(position);
            File fileDelete = new File(imageDelete);
            if (fileDelete.exists())
            {
                fileDelete.delete();
            }
            adapter.notifyDataSetChanged();
            dbRef.child(keyList.get(position)).removeValue();
            keyList.remove(position);
            return true;
        }
    });
    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String imagePath = path +'/'+ list.get(position);
            File file = new File(imagePath);
            if (file.exists())
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView2.setImageBitmap(myBitmap);
                imageView2.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageView2.setVisibility(View.INVISIBLE);
                }
            },5 * 1000);

            }
        }

    });
    }

}

