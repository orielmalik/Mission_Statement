package com.example.missionstatement.Firebase;

import android.content.Context;
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
import java.io.File;
import java.io.FileReader;
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


    public StringBuilder downloadTextFile(String fileName, Context context, String root,String child) {
        // Reference to the file to be downloaded
        StorageReference fileRef = getStorageReference().child(root).child(child).child(fileName);
        StringBuilder text = new StringBuilder();

        // Create a local file to store the downloaded file
        File localFile = new File(context.getFilesDir(), fileName);

        fileRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            // File has been downloaded successfully
            try {
                // Read the file content
                BufferedReader br = new BufferedReader(new FileReader(localFile));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                // Handle the text from the file here (e.g., display it in a TextView)
                // textView.setText(text.toString());
            } catch (IOException e) {
                e.printStackTrace();

                Log.e(null, "downloadTextFile:INSIDE "+e.getMessage() );

            }
        }).addOnFailureListener(exception -> {
            // Handle any errors
            exception.printStackTrace();
            Log.e(null, "downloadTextFile:OUTSIDE    "+exception.getMessage() );

        });
        return  text;
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


    public Test getTest() {
        return test;
    }


}




