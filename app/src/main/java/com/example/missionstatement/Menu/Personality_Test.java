package com.example.missionstatement.Menu;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.missionstatement.CallBackType.Callback_test;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentQuest;
import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Personality_Test extends AppCompatActivity {
    private FrameLayout frameLayout;
    private MaterialTextView subject;
    private Storage storage = Storage.getInstance();
    private Test test;
    private FragmentQuest fragmentQuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality_test);

        Storage storage = Storage.getInstance();
    }

    Callback_test callbackTest = new Callback_test() {
        @Override
        public void uploadIndex(int index) {
            //calc
        }

        @Override
        public void fillTest(Test test) {

        }
    };

    private void initViews() {
        fragmentQuest = new FragmentQuest();
        frameLayout = findViewById(R.id.frame_quest);
        subject = findViewById(R.id.TXT_personal);
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


    public void buildTest(Test test) {
      /*  //final CompletableFuture<Test> t = storage.downloadTxtFile("sam.txt");
        try {
            test = t.get();
            Log.d("ddd", test.toString());
            subject.setText(test.getQuestions().get(0));
        } catch (ExecutionException | InterruptedException e) {

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        buildTest(test);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }*/
    }
}