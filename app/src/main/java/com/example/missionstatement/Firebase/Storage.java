package com.example.missionstatement.Firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.Tools.CryptoUtils;
import com.example.missionstatement.Tools.ImageUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import javax.crypto.SecretKey;

public class Storage {
    private static Storage instance;//singelton design pattern
    private StorageReference storageReference;
private Test test;
private static int counter=0;//know when the first time user upload to states
    private Storage() {
        storageReference = FirebaseStorage.getInstance().getReference(/*"gs://mission-statement.appspot.com"*/);
    }
    public StorageReference getStorageReference() {
        return storageReference;
    }

    // Public method to get the singleton instance
    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public void uploadImageToFirebase(AppCompatImageView imageView, String name, String pos) {
        if (imageView.getDrawable() == null) return;
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        try {
            // Encrypt the image data
            String encryptedDataString = CryptoUtils.encrypt(Base64.encodeToString(data, Base64.DEFAULT));
            byte[] encryptedData = Base64.decode(encryptedDataString, Base64.DEFAULT);

            UploadTask uploadTask = getStorageReference().child(pos).child(name).putBytes(encryptedData);
            // Continue with your upload listener as before
        } catch (Exception e) {
            e.printStackTrace();
            // Handle encryption error
        }
    }






    public  void removeImg(String pos,String child)//pos=position or file location
    {
        getStorageReference().child(pos).child(child).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        getStorageReference().child(pos).child(child).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(null, "deleteFail: "+e.getMessage());
            }}
        );
    }
    public void showImage(AppCompatImageView imageView, String child, String pos) {
        if (imageView == null) return;
        long max = 550 * 550; // Define the max download size
        storageReference.child(pos).child(child).getBytes(max).addOnSuccessListener(bytes -> {
            try {
                // Decrypt the image data
                String decryptedDataString = CryptoUtils.decrypt(Base64.encodeToString(bytes, Base64.DEFAULT));
                byte[] decryptedData = Base64.decode(decryptedDataString, Base64.DEFAULT);

                Bitmap bitmap = BitmapFactory.decodeByteArray(decryptedData, 0, decryptedData.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle decryption error
            }
        }).addOnFailureListener(e -> Log.d(null, "Download Failed: " + e.getMessage()));
    }




    public Test downloadTxtFile(String txtChoice)
    {
        // final Test[] t=new Test[1];
       final   CompletableFuture<Test>t=new CompletableFuture<>();
        getStorageReference().child("Test").child(txtChoice).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

             Test r= readContent(bytes);
                //t.complete(test);
                t.complete(r);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                exception.printStackTrace();
                Log.e("STE",exception.getMessage());
            }
        });
        return null;
    }



    public Test readContent(byte[] bytes)
    {test =new Test();
        try {
            // Convert bytes to string
            String content = new String(bytes);

            // Use BufferedReader to read the content line by line
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
            String line;

            // Read lines until the end of the file
            while ((line = reader.readLine()) != null) {
                test.fillContent(line,reader);
                Log.d("FileContent", line);
            }

            // Close the BufferedReader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("FileContentERR", e.getMessage());

        }
        return test;
    }
    private String imgenc(AppCompatImageView imageView) throws Exception {//image encrypt
        Bitmap bitmap = ImageUtil.getBitmapFromImageView(imageView);

        String base64 = ImageUtil.convertBitmapToBase64(bitmap);

     return  CryptoUtils.encrypt(base64);

    }

//image decrypt
    public void imagedec(StorageReference imageRef, AppCompatImageView imageView) {

        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {

            /*    String encryptedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
                String decryptedImage = CryptoUtils.decrypt(encryptedImage);
                Bitmap bitmap = ImageUtil.convertBase64ToBitmap(decryptedImage);*/
                Bitmap bitmap = BitmapFactory.decodeByteArray((bytes), 0, bytes.length);
                imageView.setImageBitmap(bitmap);

        }).addOnFailureListener(exception -> {
            Log.d(null, "DownFailed: "+exception.getMessage());
            Log.d(null, "DownFailedp: "+imageRef.getPath());
        });
    }

    public Test getTest() {
        return test;
    }


}




