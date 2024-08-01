package com.example.missionstatement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.missionstatement.CallBackType.Callback_login;
import com.example.missionstatement.Fragment.FragmentLogin;
import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Manager.ManagerActivity;
import com.example.missionstatement.Menu.DeatilsTest;
import com.example.missionstatement.Menu.Menu;
import com.example.missionstatement.Menu.Personality_Test;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.Tools.CryptoUtils;
import com.example.missionstatement.Tools.Functions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Login extends AppCompatActivity {
    private MaterialButton start;
    private FloatingActionButton back;
    private boolean acceptIntent;
    private FragmentLogin fragmentLogin;
    private Realtime server;
    private Intent i;
    private Bundle bundle;
    private Context context;
    String[] check;
    private MaterialTextView forgetpassword;
    HashMap accept;
    private static int clicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        back = findViewById(R.id.back_forget);
        context = this;
        start = findViewById(R.id.btnxStartLogin);
        fragmentLogin = new FragmentLogin();
        server = new Realtime(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_login, fragmentLogin)
                //.hide(fragmentMap)
                .commit();
        forgetpassword = findViewById(R.id.forgetlogin);


    }

    Callback_login callback_login = new Callback_login() {

        @Override
        public void transportEmailPassword(String a, String b) {
            check = new String[]{a, b};


        }

        @Override
        public void acceptToProfile(HashMap data) {

            accept = data;

        }
    };

    private void makeStartButton() {
        i = new Intent(this, Menu.class);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentLogin.getData();


                CompletableFuture<HashMap<String, HashMap<String, String>>> dataFuture = server.checkDataSnapshot("human");
                dataFuture.thenAccept(data -> {
                    Toast.makeText(context, "Load Deatils", Toast.LENGTH_SHORT);
                    try {

                        decryptLogin(data,check);
                    } catch (Exception e) {
                        // Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(null, "onClick: "+e.getMessage() );
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }).exceptionally(e -> {
                    // Handle exception if there was an error
                    Log.e("YourActivity", "Error: " + e.getMessage());
                    return null;
                });


            }
        });
    }

    private void decryptLogin(HashMap<String, HashMap<String, String>>data,String[]mcheck) throws Exception {
        String []check=mcheck;
        if(check[1].trim().equals("12345")&&check[0].equals("123123454"))
        {
            Intent ad=new Intent(this, ManagerActivity.class);
            User manager=new User("123123454");
            manager.setPassword("12345");
            manager.setManager(true);
            ad.putExtra("user",manager);
            startActivity(ad);
        }else {
            for (HashMap<String, String> innerMap : data.values()) {
                // Iterate through key-value pairs in the inner HashMap
                if ((CryptoUtils.decrypt(innerMap.get("email")).equals(check[0].toLowerCase()) ||
                        CryptoUtils.decrypt(innerMap.get("PhoneNumber")).equals(check[0]))
                        && CryptoUtils.decrypt(innerMap.get("Password")).equals(check[1])) {
                    Toast.makeText(context, "SUCC", Toast.LENGTH_SHORT).show();
                    Bundle b = new Bundle();
                    HashMap<String, String> p = decryptHuman(innerMap);
                    b.putSerializable("deatils", p);
                    Log.d(null, "onClick: " + innerMap);
                    i.putExtra("bundle", b);
                    startActivity(i);
                    return;
                }

            }
        }
        Toast.makeText(context, "NOTFOUND", Toast.LENGTH_SHORT).show();
        start.refreshDrawableState();
    }
    private HashMap<String,String> decryptHuman(HashMap<String,String>mdeatils){
        HashMap<String,String>deatils=mdeatils;
        deatils.forEach((key, value) -> {
            // בדיקה אם הערך הוא String
            if (value instanceof String) {
                // שינוי הערך
                try {
                    String strValue=CryptoUtils.decrypt((String) value.toString());
                    deatils.put(key, strValue);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
        return  deatils;
    }

    private void showPassword() {   forgetpassword.setVisibility(View.VISIBLE);
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked=0;
                back.setVisibility(View.VISIBLE);
                forgetpassword.setVisibility(View.INVISIBLE);//change fragment to state he change password
                fragmentLogin.Showpassword();
                String[]arr=new String[]{"oriel.malik@gmail.com","oriel.malik@gmail.com","MISSION STATEMENT","human"};
                startActivity(Functions.sendEmail(arr));

            }
        });

    }

    public void sendEmail(String recipientEmail, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + recipientEmail));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        // Check if there's an email client available to handle the intent
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            // Handle the case where no email client is available (e.g., show an error message)
        }
    }



    @Override
    protected void onStart () {
        super.onStart();



        fragmentLogin.setCallback_login(callback_login);
        makeStartButton();
        //  showPassword();

    }
    @Override
    protected void onStop () {
        super.onStop();
    }
}
