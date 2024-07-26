package com.example.missionstatement.Menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.missionstatement.CallBackType.Callback_test;
import com.example.missionstatement.Category;
import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentQuest;
import com.example.missionstatement.HeatmapView;
import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.R;
import com.example.missionstatement.Tools.Functions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Personality_Test extends AppCompatActivity {
    private FrameLayout frameLayout;
    private HeatmapView heatmapView;
    private ProgressBar progressBar;
    private EditText questEditText;

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
    private Realtime server;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality_test);
        initViews();
        test=new Test();
        server=new Realtime(this);
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
        public void uploadAnswers(RadioGroup radioGroup) {

            if (test.getAnswers() == null) {
                return;
            }
            String[] answers;
            if (!user.isManager()) {
                answers = (String[]) test.getAnswers().get(mycounter).toArray();
            } else {
                answers = new String[4];

            }

            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View view = radioGroup.getChildAt(i);
                if (view instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) view;
                    if (answers[i] != null && !user.isManager()) {
                        radioButton.setText(answers[i]);
                    } else if (answers[i] != null && user.isManager()) {
                        answers[i] = radioButton.getText().toString();
                    } else if (!user.isManager()) {
                        radioButton.setVisibility(View.INVISIBLE);

                    }

                }
            }
            if (user.isManager()) {
                if (test.getAnswers() == null) {
                    test.setAnswers(new ArrayList<>());
                }
                if (test.getQuestions() == null) {
                    test.setQuestions(new ArrayList<>());
                }
                if (!subject.getText().toString().isEmpty()) {
                    test.getQuestions().add(test.getQuestions().size() - 1,subject.getText().toString());
                }
                if (!Arrays.asList(answers).isEmpty() && Arrays.asList(answers).size() == 4) {
                    test.getAnswers().get(test.getAnswers().size() - 1).addAll(Arrays.asList(answers));

                    mycounter++;
                    uploadGenerateTest(test);
                }
            }
        }


        @Override
        public void getResult(int index) {
            if (!user.isManager()) {
                test.getResults().add(mycounter - 1, Integer.valueOf(index));

            }
        }


        @Override
        public boolean getCounterQuestion(int counter) {
            if(!user.isManager()) {
                if (counter <= 8 && counter >= 1&&mycounter!=counter) {
                    mycounter = counter;
                    if (test.getQuestions().get(counter) != null) {
                        subject.setText(test.getQuestions().get(counter));
                        Toast.makeText(Personality_Test.this, test.getQuestions().get(counter), Toast.LENGTH_SHORT).show();
                    }
                    //fragmentQuest.onAnswer();
                    return true;
                }
                else if(counter==9&&user.isManager())
                {
                    if(Personality_Test.this.path==null){
                        Personality_Test.this.path="e"+UUID.randomUUID();
                    }
                    test.setDone(true);
                    if(user.getTests()==null){
                        user.setTests(new ArrayList<>());
                    }
                    HashMap<String,Object>m=new HashMap<>();
                    m.put(Functions.sanitizeKey(Personality_Test.this.path),Functions.fromMap(test.toMap()));
                    user.getTests().add(m);
                    server.getmDatabase().child("USER").child(user.getPhoneNumber()).setValue(user.ResultMap());
                    Intent intent=new Intent(Personality_Test.this, ResultsGraph.class);
                    intent.putExtra("user",user.getPhoneNumber());
                    Personality_Test.this.startActivity(intent);
                    return  false;
                }else{return  false;}
            }else {

                if (counter > 0 || counter < 9) {
                    mycounter = counter;
                    boolean b= subject.getText().equals("Give points \n to every question Before")&&mycounter==1;
                    if(!b)
                    {
                        test.getQuestions().add(subject.getText().toString());
                        NewQuestion();
                    }
                }


            }
            return true;
        }


        @Override
        public void setAnswer(String string, int i) {
            if (user.isManager()) {
                String[] arr = new String[4];
                int[]pointsPerAnswer= Functions.convertListToArray(test.pointsPerAnswer);
                if(subject.getText().equals("Give points \n to every question Before"))
                {
                    try {
                        pointsPerAnswer[i] = Integer.parseInt(string);
                        Toast.makeText(Personality_Test.this, "Question is  "+mycounter, Toast.LENGTH_LONG).show();
                    }catch (NumberFormatException numberFormatException)
                    {
                        numberFormatException.printStackTrace();
                        Toast.makeText(Personality_Test.this, numberFormatException.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }//start
                else {

                    arr[i] = string;
                    boolean k=false;
                    for (int j = 0; j <arr.length ; j++) {
                        if(arr[j]==null)
                        {
                            k=true;
                        }
                    }
                    if(!k) {
                        test.getAnswers().add(Arrays.asList(arr));

                    }
                }//middle

            }
        }

        @Override
        public boolean canStartBuildTest() {
            test.setDone(true);
            // user.getTests().add(test);
            return  true;
        }

        @Override
        public int state() {
            return !user.isManager()? 0:1 ;
        }
    };

    private void uploadGenerateTest(Test test) {
        if(test.getQuestions().size()>7&&test.getAnswers().size()>7)
test.setFileLocation(storage.getStorageReference().child("TEST").child(Functions.sanitizeKey(getRandomCategory().name()+"Test"+((int)(Math.random()*88+22)))));
            File f=test.buildEnd(Functions.sanitizeKey(getRandomCategory().name()+"Test"+((int)(Math.random()*88+22))+".txt"),this);
storage.uploadTextFile(test.getFileLocation(),this,f);
    }
    public Category getRandomCategory() {
        Category[] categories = Category.values();
        Random random = new Random();
        int index = random.nextInt(categories.length);
        return categories[index];
    }
    private void showAlertDialogAnswer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(frameLayout.getContext());
        builder.setTitle("Save test?")
                .setMessage("finished to fill test?")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StorageReference fileref=  storage.getStorageReference().child("Test")
                                .child("EDUCATION");
                        File f = test.buildEnd("EducationTest"+(Math.random()*100+2), Personality_Test.this);
                        if (f != null) {
                            storage.uploadTextFile(fileref, Personality_Test.this, f);
                            test.setDone(true);
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(Personality_Test.this, "ERROR UPLOAD TEXT", Toast.LENGTH_LONG).show();

                        }
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
        questEditText.setText("");
        subject.setEnabled(true);
        subject.setInputType(InputType.TYPE_CLASS_TEXT);
        questEditText.setVisibility(View.VISIBLE);
        questEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                subject.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



       /* subject.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check for touch event (e.g., only handle DOWN event)
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    fragmentQuest.getRadioGroup().setEnabled(false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(subject, 0);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // שחרר את `RadioGroup` כאשר האצבע מורמת
                    fragmentQuest.getRadioGroup().setEnabled(true);
                }
                return true;

            }
        });

        */



    }
    private void initViews() {
        fragmentQuest = new FragmentQuest();
        frameLayout = findViewById(R.id.frame_quest);
        subject = findViewById(R.id.TXT_personal);
        //    heatmapView = findViewById(R.id.heatmapView);
        progressBar=findViewById(R.id.progressBar);
        questEditText=findViewById(R.id.EditAdminSubject);

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
                                                    subject.setText(test.getQuestions().get(mycounter));
                                                    callbackTest.uploadAnswers(fragmentQuest.getRadioGroup());
                                                }
                                            }

                                            @Override
                                            public void onFileReadFailure(Exception exception) {
                                                Toast.makeText(Personality_Test.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        waiter.complete(fileRef);


                                    }
                                }
                            }

                        }
                    }
                });
        waiter.thenAccept(f->
        {
            this.path=f.getPath();
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
        }else {
            mycounter=0;
            subject.setText("Give points \n to every question Before");
            test=new Test();
            test.buildStart();
            fragmentQuest.setCallback_test(callbackTest);
            //fragmentQuest.onBuildAdmin();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }



}


