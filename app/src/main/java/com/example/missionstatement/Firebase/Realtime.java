package com.example.missionstatement.Firebase;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.missionstatement.CallBackType.Callback_register;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class Realtime  {
    private DatabaseReference mDatabase;
    private  static  HashMap<String,String>accept;
    private final Context context;
    public  static  boolean newHuman=false;
    DatabaseReference usersReference;
    // public static AtomicBoolean checked=new AtomicBoolean(false);

    public Realtime(Context c) {
        FirebaseApp.initializeApp(c);
        context = c;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }
    public void updateFieldatHuman(String root,HashMap updates, String field) {
        mDatabase.child(root).child(field).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(null, "succUpdate");
                })
                .addOnFailureListener(e -> {
                    Log.d(null, "FailUpdate"+e.getMessage());

                });
    }

    public CompletableFuture<HashMap<String,HashMap<String,String>>> checkDataSnapshot(String child )  {
        final boolean[] arr=new boolean[1];
        final CompletableFuture<HashMap<String,HashMap<String,String>>> resultFuture = new CompletableFuture<>();
        DatabaseReference ref=mDatabase;
        if(child != null &&! child.isEmpty())
        {
            ref=mDatabase.child(child);
        }
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,HashMap<String,String>> hashMap=(HashMap<String,HashMap<String,String>>) dataSnapshot.getValue();
                resultFuture.complete((hashMap));//check deatils before

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                resultFuture.completeExceptionally(databaseError.toException());
            }


        });
        return  resultFuture;


    }




    public Context getContext() {
        return context;
    }

    public HashMap<String, String> getAccept() {
        return accept;
    }

    public void setAccept(HashMap<String, String> accept) {
        this.accept = accept;
    }


    public CompletableFuture<HashMap<String,HashMap<String,Object>>> checkDataSnapshotnew(String child )  {
        final boolean[] arr=new boolean[1];
        final CompletableFuture<HashMap<String,HashMap<String,Object>>> resultFuture = new CompletableFuture<>();
        DatabaseReference ref=mDatabase;
        if(child != null &&! child.isEmpty())
        {
            ref=mDatabase.child(child);
        }
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,HashMap<String,Object>> hashMap=(HashMap<String,HashMap<String,Object>>) dataSnapshot.getValue();
                resultFuture.complete((hashMap));//check deatils before

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                resultFuture.completeExceptionally(databaseError.toException());
            }


        });
        return  resultFuture;


    }


}