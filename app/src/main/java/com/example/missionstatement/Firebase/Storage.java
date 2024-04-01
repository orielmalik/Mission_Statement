package com.example.missionstatement.Firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.missionstatement.Objects.Test;
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
import java.util.concurrent.CompletableFuture;

public class Storage {
    private static Storage instance;//singelton design pattern
    private StorageReference storageReference;
private Test test;
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

    public void uploadImageToFirebase(AppCompatImageView imageView,String name,String pos) {
        // Get the data from an ImageView as bytes
        if(imageView.getDrawable()==null){return;}
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = getStorageReference().child(pos).child(name).putBytes(data);
        Log.d(null, "uploadImageToFirebase: "+getStorageReference().child(name).getPath());

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(null, "onFailure: " + exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                // ...

            }
        });
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

    public  void showImage( AppCompatImageView imageView,String child,String pos) {
        if (imageView == null) {
            return;
        }
        long max = 550*550;
        storageReference.child(pos).child(child).getBytes(max).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });
        storageReference.child(pos).child(child).getBytes(max).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(null, "DownFailed: "+e.getMessage());
            }
        });
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

    public Test getTest() {
        return test;
    }
}



