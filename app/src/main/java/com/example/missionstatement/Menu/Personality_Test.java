package com.example.missionstatement.Menu;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.missionstatement.CallBackType.Callback_test;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentQuest;
import com.example.missionstatement.HeatmapView;
import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class Personality_Test extends AppCompatActivity {
    private FrameLayout frameLayout;
private HeatmapView heatmapView;

    private MaterialTextView subject;

    private Storage storage = Storage.getInstance();
    private Test test;
    private FragmentQuest fragmentQuest;
    private List<Point> touchPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality_test);
initViews();
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
        heatmapView = findViewById(R.id.heatmapView);

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


    /*   public void buildTest(Test test) {
           //final CompletableFuture<Test> t = storage.downloadTxtFile("sam.txt");
           try {
               test = t.get();
               Log.d("ddd", test.toString());
               subject.setText(test.getQuestions().get(0));
           } catch (ExecutionException | InterruptedException e) {

           }
       }*/
    @Override
    protected void onResume() {
        super.onResume();
        showFragment();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}

