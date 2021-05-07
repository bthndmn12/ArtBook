 package com.bthndmn12.artbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

 public class ArtActivity extends AppCompatActivity {
        EditText artName;
        EditText artistName;
        EditText year;
        ImageView imageView;
        Button button;
        Bitmap selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art);

        artName = findViewById(R.id.artName);
        artistName = findViewById(R.id.artistName);
        year = findViewById(R.id.year);
        button = findViewById(R.id.save);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if(info.matches("new")){
            artName.setText("");
            artistName.setText("");
            year.setText("");
            button.setVisibility(View.VISIBLE);
            Bitmap selectImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.selected);
            imageView.setImageBitmap(selectImage);
        }else{
            int artId =  intent.getIntExtra("artId",1);
            button.setVisibility(View.INVISIBLE);
        }
    }

    public void selectImage(View view){
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
    }else{
        Intent toGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(toGallery,2);
    }
    }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         if(requestCode == 1){
             if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                 Intent toGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 startActivityForResult(toGallery,2 );

             }
         }
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

         if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri imageData = data.getData();
            if(Build.VERSION.SDK_INT >=28){
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageData);
                try {
                    selectedImage =ImageDecoder.decodeBitmap(source);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(selectedImage );
            }else{
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                    imageView.setImageBitmap(selectedImage );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
         }

         super.onActivityResult(requestCode, resultCode, data);
     }

     public void save(View view){
         String artN = artName.getText().toString();
         String painterN = artistName.getText().toString();
         String yearN = year.getText().toString();

         Bitmap smallImage = makeSmaller(selectedImage,300);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         selectedImage.compress(Bitmap.CompressFormat.PNG, 50,outputStream);
            byte[] byteArray = outputStream.toByteArray();
    try{
        SQLiteDatabase database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY,artname VARCHAR,artistname VARCHAR,year VARCHAR,image BLOB)");
        String sqlStr = "INSERT INTO arts (artname,artistname,year,image) VALUES(?,?,?,?)";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlStr);
        sqLiteStatement.bindString(1,artN);
        sqLiteStatement.bindString(2,painterN);
        sqLiteStatement.bindString(3,yearN);
        sqLiteStatement.bindBlob(4,byteArray);
        sqLiteStatement.execute();
    }catch (Exception e){

    }
    finish();
    }public Bitmap makeSmaller(Bitmap image,int size){
        int height = image.getHeight();
        int width = image.getWidth();
        float bitmapRatio = (float)width/(float)height;
        if(bitmapRatio>1){
            width = size;
            height = (int)(width/bitmapRatio);
        }else{
            height = size;
            width = (int)(height*bitmapRatio);
         }
        return  Bitmap.createScaledBitmap(image,width,height,true);
     }
}