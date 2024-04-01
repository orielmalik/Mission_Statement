package com.example.missionstatement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.MotionButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    private MotionButton btn_main_register,btn_main_login;
    private TextView textView;
   private  Intent intent_Reg;
    private  Intent intent_Log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.myTextView);
        btn_main_register=findViewById(R.id.btn_main_register);
        btn_main_login=findViewById(R.id.btn_main_login);

        intent_Reg = new Intent(this,Register.class);
        intent_Log = new Intent(this,Login.class);

     LottieAnimationView lottie = findViewById(R.id.lottie_tar);
        lottie.setSpeed(17f);
        lottie.setRepeatCount(10000);
        lottie.resumeAnimation();

    }


    @Override
    protected void onResume() {
        super.onResume();

        // Inside your activity or fragment
btn_main_register.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(intent_Reg);
    }

});
        btn_main_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent_Log);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}

