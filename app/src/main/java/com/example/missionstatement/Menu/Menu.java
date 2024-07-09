package com.example.missionstatement.Menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.missionstatement.Objects.Human;
import com.example.missionstatement.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;

public class Menu extends AppCompatActivity {
    private Human Human_Deatils;
    private String name, pos;
    private MaterialTextView mtxt;
    Bundle bundle;
    private HashMap<String, String> deatils;
    private MaterialButton[] options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            deatils = (HashMap) bundle.getSerializable("deatils");
        }
        ;
        name = deatils.get("Username");
        pos = deatils.get("position");

        mtxt = findViewById(R.id.Txt_Menu);
        if (!pos.equals("USER")) {
            mtxt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        options = new MaterialButton[3];
        options[0] = findViewById(R.id.btn_updateDeatils);
        options[1] = findViewById(R.id.btn_option2);
        options[2] = findViewById(R.id.btn_option3);
        makeText();

        Log.d(null, "onClickAfter: " + deatils);

    }

    private void makebtn_updateDeatils() {
        Intent in = new Intent(this, Profile.class);
        options[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in.putExtra("bundle", bundle);
                startActivity(in);
            }
        });
    }

    private void makebtn_option2() {
        final Intent[] i = new Intent[1];
        if (pos.equals("USER")) {
            options[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i[0] = new Intent(Menu.this, DeatilsTest.class);

                    i[0].putExtra("ph", deatils.get("PhoneNumber"));
                    startActivity(i[0]);
                }
            });
        } else {


        }


    }

    private void makebtn_option3() {
        Intent i3 = new Intent(this, ListOperator.class);
        View.OnClickListener operatorClick;
        View.OnClickListener UserClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i3);
            }
        };
        options[2].setOnClickListener(UserClick);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (name != null && pos != null) {
            mtxt.setText(" Nice to meet\n" + name);
            if (pos.equals("USER")) {
                options[1].setText("Declare\n Intentions");
                options[2].setText("Show \nOperators");
            } else {
                options[1].setText("Provide \nProffession");
                options[2].setText(" Communication");//with operators and more
            }
        }

        makebtn_option2();
        makebtn_option3();
        makebtn_updateDeatils();
        makeText();
    }


    private void makeText() {

        this.mtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, ResultsGraph.class).putExtra("user", deatils.get("PhoneNumber"));
                startActivity(intent);
            }
        });


    }
}