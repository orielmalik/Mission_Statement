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
import java.util.Date;
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
            String[]answers= (String[]) test.getAnswers().get(mycounter).toArray();
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View view = radioGroup.getChildAt(i);
                if (view instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) view;

                    if (answers[i] != null) {
                        radioButton.setText(answers[i]);
                    } else {
                        radioButton.setVisibility(View.INVISIBLE);

                    }
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
                if (counter <= 8 && counter >= 1) {
                    if(mycounter!=counter){
                        mycounter = counter;}

                    if (test.getQuestions().get(counter) != null) {
                        subject.setText(test.getQuestions().get(counter));
                        Toast.makeText(Personality_Test.this, test.getQuestions().get(counter), Toast.LENGTH_SHORT).show();
                    }
                    //fragmentQuest.onAnswer();
                    return true;
                }
                else if(counter==9&&!user.isManager())
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
                if(counter>1)
                {
                    subject.setText("");
                    questEditText.setVisibility(View.VISIBLE);
                    Functions.RemovePrestentedText(questEditText);
                    subject.setText(questEditText.getText().toString());

                }
                if(counter==8)
                {
                  uploadGenerateTest(test);
                }
              return  true;}
        }


        @Override
        public void setAnswer(String string, int i) {
            if (user.isManager()) {
                String[] arr = new String[4];
                int[]pointsPerAnswer=new int[4];
                if(test.pointsPerAnswer==null)
                {
                    test.setPointsPerAnswer(new ArrayList<>());
                }
               // pointsPerAnswer= Functions.convertListToArray(test.getPointsPerAnswer());
                if(subject.getText().equals("Give points \n to every question Before"))
                {
                    try {
                        pointsPerAnswer[i] = Integer.parseInt(string);
                        this.ToastFrom(""+i);
                    }catch (NumberFormatException numberFormatException)
                    {
                        numberFormatException.printStackTrace();
                        //Toast.makeText(Personality_Test.this, numberFormatException.getMessage(), Toast.LENGTH_SHORT).show();
                        this.ToastFrom(numberFormatException.getMessage());
                        return;
                    }
                    this.getCounterQuestion(test.getQuestions().size());

                }//start
                else {
                    if(test.getAnswers()==null)
                    {
                        test.setAnswers(new ArrayList<>());
                    }
                    if(test.getQuestions()==null)
                    {
                        test.setQuestions(new ArrayList<>());
                    }
                    arr[i] = string;
                    boolean k=false;

                    if(!k) {
                        String qu=subject.getText().toString();

                            if(!qu.endsWith("?"))
                            {
                                qu+='?';
                            }
                            test.getAnswers().add(Arrays.asList(arr));
                            test.getQuestions().add(subject.getText().toString());
                            mycounter++;
                            if(Arrays.asList(test.getAnswers()).contains(null)||test.getAnswers()==null) {
                                this.ToastFrom("CANT BE EMPTY TEXTFIELD");
                            }else {
                                this.getCounterQuestion(test.getQuestions().size());
                            }
                        }
                    }
                }//middle


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

        @Override
        public boolean isFirst() {
            boolean b= subject.getText().toString().equals("Give points \n to every question Before");
            return  b;
        }

        @Override
        public void ToastFrom(String s) {
            Toast.makeText(Personality_Test.this, s, Toast.LENGTH_SHORT).show();
        }
    };

    private void uploadGenerateTest(Test test) {

        if(user.isManager()&&test.getQuestions().size()>7&&test.getAnswers().size()>7){
        File f=test.buildEnd(getRandomCategory()+"test"+UUID.randomUUID()+".txt",this);
        Log.d("fb",Storage.getInstance().getStorageReference().child("Test").getPath());
       Storage.getInstance().uploadTextFile(Storage.getInstance().getStorageReference().child("Test").child("EDUCATION"), this,f);}
        else {
            Toast.makeText(this, "PROBLEM AT UPLOAD", Toast.LENGTH_SHORT).show();
        }
    }
    public Category getRandomCategory() {
        Category[] categories = Category.values();
        Random random = new Random();
        int index = random.nextInt(categories.length);


        return categories[index];
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
                                                    //test.setFileLocation(fileRef);
                                                    Personality_Test.this.path=fileRef.getPath();
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
           // this.path=f.getPath();
            Toast.makeText(this, "START", Toast.LENGTH_SHORT).show();
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


