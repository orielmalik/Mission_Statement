package com.example.missionstatement.Menu;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.missionstatement.CallBackType.Callback_test;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentQuest;
import com.example.missionstatement.HeatmapView;
import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Personality_Test extends AppCompatActivity {
    private FrameLayout frameLayout;
    private HeatmapView heatmapView;
    private ProgressBar progressBar;
    private MaterialTextView subject;
    private static int mycounter=1;
    String content;
    List<String>questions;
    private Storage storage ;
    private Test test;
    private FragmentQuest fragmentQuest;
    User user;
    private FloatingActionButton next,previous;
    private StorageReference myStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality_test);
        initViews();
        test=new Test();
        Storage storage = Storage.getInstance();
        try {
            user =(User) getIntent().getSerializableExtra("user");
        }catch (IllegalArgumentException illegalArgumentException)
        {
            Toast.makeText(this, illegalArgumentException.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("puserTest",illegalArgumentException.getMessage());
        }

    }

    Callback_test callbackTest = new Callback_test() {
        @Override
        public void uploadAnswers(RadioGroup radioGroup,int counter) {

            if (test.getAnswers() == null) {
                return;
            }
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View view = radioGroup.getChildAt(i);
                if (view instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) view;
                    if (test.getAnswers().get(counter )[i] != null) {
                        radioButton.setText(test.getAnswers().get(counter )[i]);
                    } else if (test.getAnswers().get(mycounter - 1)[i] == null && user.isManager()) {
                        radioButton.setText("");
                    } else {
                        radioButton.setVisibility(View.INVISIBLE);

                    }
                }
            }
        }


        private setAnswersOnRadioGroup(RadioGroup radioGroup)
        {
            
        }
        @Override
        public void getResult(int index) {
            if (!user.isManager()) {
                test.getResults().add(mycounter - 1, Integer.valueOf(index));
            }
        }


        @Override
        public boolean getCounterQuestion(int counter) {

            if (counter <= 8 && counter >= 1) {
                mycounter = counter;
                if(test.getQuestions().get(mycounter)!=null) {//because of manager
                    subject.setText(test.getQuestions().get(counter));
                }
                fragmentQuest.onAnswer();
                return true;
            }
            return false;

        }

        @Override
        public void setAnswer(String string, int i) {
            if (user.isManager()) {
                String[] arr = new String[4];
                arr[i] = string;



                test.getAnswers().add(mycounter - 1, arr);
                if (subject.getText().toString().isEmpty()) {
                    return;
                }
                for (String s : arr) {
                    if (s.isEmpty()) {
                        return;
                    }
                }
                showAlertDialogAnswer();

            }
        }

        @Override
        public boolean canStartBuildTest() {
            if(user.isManager()) {
                return subject.getText().equals("Give points \n to every question Before")&&mycounter==0;
        }
            return  false;}
    };
    private void showAlertDialogAnswer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentQuest.getContext());
        builder.setTitle("Save Answer?")
                .setMessage("finished to fill answer text and options?")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewQuestion();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void NewQuestion() {
        fragmentQuest.clear();
        subject.setText("");
    }

    private void initViews() {
        fragmentQuest = new FragmentQuest();
        frameLayout = findViewById(R.id.frame_quest);
        subject = findViewById(R.id.TXT_personal);
        //    heatmapView = findViewById(R.id.heatmapView);
        progressBar=findViewById(R.id.progressBar);
        next=findViewById(R.id.BTN_RightAnswer);
        previous=findViewById(R.id.BTN_leftAnswer);
    }

    private void buildTest() {
        questions = new ArrayList<>();
        storage = Storage.getInstance();
        progressBar.setVisibility(View.VISIBLE);

        CompletableFuture<StorageReference> waiter = new CompletableFuture<>();

        storage.getStorageReference().child("Test").child(user.getDescription())
                .listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {
                        if (task.isSuccessful()) {
                            ListResult result = task.getResult();
                            for (StorageReference fileRef : result.getItems()) {
                                if (fileRef != null) {
                                    if (!user.isTestName(fileRef.getPath())) {
                                        readFileOnline(fileRef, new FileReadCallback() {
                                            @Override
                                            public void onFileReadSuccess(String c) {
                                                Personality_Test.this.content=c;
                                                if (c != null)
                                                {
                                                    test=new Test();
                                                    progressBar.setVisibility(View.GONE);
                                                    fragmentQuest.setCallback_test(callbackTest);
                                                    test.fillContent(c);
                                                    subject.setText(test.getQuestions().get(mycounter-1));

                                                }
                                            }

                                            @Override
                                            public void onFileReadFailure(Exception exception) {

                                            }
                                        });
                                        waiter.complete(fileRef);
                                        //waiter.complete(fileRef);


                                    }
                                }
                            }

                        }
                    }
                });
        waiter.thenAccept(f->
        {

        });

    }






    public  interface FileReadCallback
    {
        void onFileReadSuccess(String content);
        void onFileReadFailure(Exception exception);
    }
    private void readFileOnline(StorageReference fileRef, FileReadCallback callback) {
        final long ONE_MEGABYTE = 3 * 1024;

        fileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] data) {
                String contt = new String(data);
                Toast.makeText(Personality_Test.this, "click", Toast.LENGTH_SHORT).show();
                String c = contt.substring(contt.indexOf(String.valueOf(1)), contt.indexOf("?"));
                //Personality_Test.this.content = contt;
                callback.onFileReadSuccess(contt);
                Log.d(null, "onSuccess: " + content);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressBar.setVisibility(View.GONE);
                // Handle the error
                Toast.makeText(Personality_Test.this, "Failed to read file", Toast.LENGTH_SHORT).show();
                callback.onFileReadFailure(exception);
            }
        });
    }





    private void showFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.frame_quest, fragmentQuest).commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Handle landscape orientation


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Handle portrait orientation
            fragmentQuest.getRadioGroup().clearCheck();

        }

    }







    @Override
    protected void onResume() {
        super.onResume();
        showFragment();

        if(!user.isManager()) {
            buildTest();
            next.setVisibility(View.INVISIBLE);
            previous.setVisibility(View.VISIBLE);
        }else {
            subject.setText("Give points \n to every question Before");
            test=new Test();
            test.buildStart();
            fragmentQuest.setCallback_test(callbackTest);
            //fragmentQuest.onBuildAdmin();
            Nevigate();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private  void Nevigate()
    {

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mycounter>0)
                    mycounter--;
                if(test.getAnswers().size()>mycounter)
                    fragmentQuest.onAnswer();
                subject.setText(test.getQuestions().get(mycounter-1));
            }
        }); next.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mycounter<8)
                mycounter++;
            if(test.getAnswers().size()-1>mycounter)
                fragmentQuest.onAnswer();
            subject.setText(test.getQuestions().get(mycounter-1));
        }
    });
        if(mycounter-1==0)
        {
            previous.setVisibility(View.INVISIBLE);
        }
        else if (mycounter+1==8||subject.getText().equals("Give points \n to every question Before"))
        {
            next.setVisibility(View.INVISIBLE);
        }


    }
}


