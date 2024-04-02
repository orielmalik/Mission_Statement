package com.example.missionstatement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.missionstatement.CallBackType.Callback_login;
import com.example.missionstatement.CallBackType.Callback_register;
import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Fragment.FragmentRegister;
import com.example.missionstatement.Menu.Menu;
import com.example.missionstatement.Objects.Human;
import com.example.missionstatement.Tools.CryptoUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Register extends AppCompatActivity {
    private FragmentRegister fragmentRegister;

    private MaterialButton btn_Start;
    private FloatingActionButton fb, fh;
    private ProgressBar progressBar;
    private Context context;
    private Realtime server;
    private  static int clicked = 0;
    private static boolean check=false;
    private Human human;
    private Callback_login callback_login;
    private HashMap<String,String>accept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fragmentRegister = new FragmentRegister();
        context=this;
        server = new Realtime(this);
        // fragmentRegister.setCallback_register(callback_register);
        fh=findViewById(R.id.info_center);
        btn_Start = findViewById(R.id.btnxStartRegister);
        fragmentRegister.removePresentedText();
        progressBar=findViewById(R.id.progressBar);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frmae_Register, fragmentRegister)
                //.hide(fragmentMap)
                .commit();
        accept=new HashMap<>();
    }


    private void makeButton() {
        Intent i = new Intent(this, Menu.class);
        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start to use App
                clicked++;
                if (fragmentRegister.createHuman() == null) {
                    Toast.makeText(context, "Fill Deatils Again", Toast.LENGTH_SHORT).show();
                    return;

                }
                Toast.makeText(context, "System load", Toast.LENGTH_SHORT).show();

                CompletableFuture<HashMap<String, HashMap<String, String>>> dataFuture = server.checkDataSnapshot("human");
                dataFuture.thenAccept(data -> {
                 /*   if(data==null){
                        server.getmDatabase().child("human").child(String.valueOf(fragmentRegister.createHuman().getPhoneNumber())).setValue(fragmentRegister.createHuman().toMap());
                        makeIntent(i);
                    };*/

if(data!=null)
                        for (HashMap<String, String> innerMap : data.values()) {
                            try {
                                if (CryptoUtils.decrypt((innerMap).get("email")).equals(fragmentRegister.createHuman().getEmail())) {
                                    Toast.makeText(context, "Email is already in use" + fragmentRegister.createHuman().getPhoneNumber(), Toast.LENGTH_SHORT).show();

                                    return;
                                } else if (CryptoUtils.decrypt((innerMap).get("PhoneNumber")).equals(fragmentRegister.createHuman().getEmail())) {
                                    Toast.makeText(context, "PhoneNumber is already in use" + fragmentRegister.createHuman().getPhoneNumber(), Toast.LENGTH_SHORT).show();

                                    return;
                                }
                            }catch (Exception e)
                            {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            }

                        server.getmDatabase().child("human").child(String.valueOf(fragmentRegister.createHuman().getPhoneNumber())).
                                setValue(encryptHuman((HashMap<String, String>) fragmentRegister.createHuman().toMap()));
                        makeIntent(i);

                    dataFuture.cancel(true);
                }).exceptionally(e -> {
                    // Handle exception if there was an error
                    Log.e("YourActivity", "Error: " + e.getMessage());
                    return null;
                });

            }

        } );




    };
    private HashMap<String,String> encryptHuman(HashMap<String,String>mdeatils){
        HashMap<String,String>deatils=mdeatils;
   deatils.forEach((key, value) -> {
            // בדיקה אם הערך הוא String
            if (value instanceof String) {
                // שינוי הערך
                try {
                    String strValue=CryptoUtils.encrypt((String) value.trim());
                    deatils.put(key, strValue);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
return  deatils;
    }
    private  void makeIntent(Intent i) {
      HashMap<String,String>deatils=  ((HashMap<String, String>) fragmentRegister.createHuman().toMap());

        Bundle b = new Bundle();


                b.putSerializable("deatils", (HashMap) deatils);
                i.putExtra("bundle", b);
                startActivity(i);
            }



    private  void  makeInfoCenter()
    {

    }
    Callback_register callback_register=new Callback_register() {
        @Override
        public Human createHuman() {
            return fragmentRegister.createHuman();
        }

        @Override
        public boolean clickedNull( )
        {
            boolean b=clicked>0;
            clicked=0;
            return b;

        }

        @Override
        public void onDataRecived(boolean b) {
            check=b;
        }


    };
    @Override
    protected void onResume() {
        super.onResume();
        makeButton();
        fragmentRegister.removePresentedText();
   }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        // Do nothing here to prevent going back to the previous activity
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Handle landscape orientation
            setContentView(R.layout.activity_register_land);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Handle portrait orientation
            setContentView(R.layout.activity_register);

        }
    }


}

