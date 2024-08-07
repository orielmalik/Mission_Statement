package com.example.missionstatement.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.missionstatement.CallBackType.Callback_profile;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentProfile;
import com.example.missionstatement.Fragment.FragmentRegister;
import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Objects.Human;
import com.example.missionstatement.Objects.Operator;
import com.example.missionstatement.R;
import com.example.missionstatement.Tools.CryptoUtils;
import com.example.missionstatement.Tools.ImageUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import android.widget.Toast;
import android.provider.MediaStore;
import android.provider.Settings;
public class Profile extends AppCompatActivity {
    private HashMap<String, String> deatils;
    private MaterialButton ChangeButton, DescriptionButton;
    private FloatingActionButton delete;
    String child;
    private FrameLayout myprofile;
    private static boolean clicked = false,finishEdit=false;
    private Context context;
    private boolean conditionFragment;
    private FragmentRegister fragmentRegister;
    private Realtime server;
    private Human human;
    private FragmentProfile fragmentProfile;
    private  int backPressedCount = 0, clickCount = 0, DesClick = 0;

    AppCompatImageView target_image;
    private Storage storage;
    private AppCompatEditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Bundle b = getIntent().getBundleExtra("bundle");
        deatils = (HashMap<String, String>) b.getSerializable("deatils");
        server = new Realtime(this);
        fragmentProfile = new FragmentProfile();
        fragmentRegister = new FragmentRegister();
      
        ChangeButton = findViewById(R.id.profile_changeDetails);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.myProfile, fragmentProfile)
                .add(R.id.myProfile, fragmentRegister)
                .hide(fragmentRegister)
                .commit();
        myprofile = findViewById(R.id.myProfile);
        description = findViewById(R.id.profile_editText);
        context = this;
        storage = Storage.getInstance();
        child = (deatils.get("email") + "jpeg.jpg");
        // fragmentProfile.editProfile();
        delete = findViewById(R.id.BTN_removeProfiler);
        DescriptionButton = findViewById(R.id.profile_AddDescription);

    }

    private void deleteImg() {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storage.removeImg(deatils.get("") + "jpeg", child);
            }
        });
    }

    Callback_profile callback_profile = new Callback_profile() {
        @Override
        public void showImage(AppCompatImageView image) {
            storage.uploadImageToFirebase(image, child, deatils.get("position"));
            storage.showImage(image, child, deatils.get("position"));
        }


        @Override
        public String returnPosition() {
            return deatils.get("position");
        }
    };

    private  void  findHuman()
    {
        server.checkDataSnapshot("human").thenAccept(big ->
        {
            if(big.containsKey(deatils.get("PhoneNumber")))
            {
              setDeatils(CryptoUtils.decryptHuman( big.get(deatils.get("PhoneNumber"))));
            }
        });
    }
    private void makeBtn() {
        if (clickCount == 0) {
            storage.showImage(fragmentProfile.getProfiler(), child, deatils.get("position"));

        }
        ChangeButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                clickCount++;
                chooseShowFragment(false);
                fragmentProfile.putDeatilsWithHash(deatils);
                if(clickCount%2==0)
                {
                    finishEdit=true;
                }
                setLayoutParms();
                myprofile.setBackgroundColor(Color.rgb(102, 133, 34));
                storage.uploadImageToFirebase(fragmentProfile.getProfiler(), child, deatils.get("position"));
                if (finishEdit ) {
                    showYesNoDialog("Upload Deatils", "Save the Changes", context, 0);
                    myprofile.setBackgroundColor(android.R.color.transparent);
                    clickCount=0;
                } else {//avoid dischange
                }

            }
        });

        DescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeButton.setVisibility(View.INVISIBLE);
                myprofile.setVisibility(View.INVISIBLE);
                description.setVisibility(View.VISIBLE);
                description.setClickable(true);

                DesClick++;
                if (DesClick > 1) {
                    showYesNoDialog("Add Description", "Save the Changes", context, 1);

                }
            }
        });
    }

    private void setLayoutParms() {
        ViewGroup.LayoutParams ProfileParams = myprofile.getLayoutParams();
        ProfileParams.height = 1500;
        LinearLayout.LayoutParams btn_parm = (LinearLayout.LayoutParams) ChangeButton.getLayoutParams();
        btn_parm.setMargins(btn_parm.leftMargin, 43, btn_parm.rightMargin, btn_parm.bottomMargin);
        myprofile.setLayoutParams(ProfileParams);
        // ChangeButton.setLayoutParams(btn_parm);
    }


    public void showYesNoDialog(String title, String message, Context context, int condition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the dialog title and message
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                positiveBtnAction(condition);
                dialog.dismiss();
            }

        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseShowFragment(true);

            }

        });


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @SuppressLint("ResourceAsColor")
    private void positiveBtnAction(int condition) {
        switch (condition) {
            case (0):
                String field = deatils.get("PhoneNumber");
                fragmentRegister.getTgl_position().setVisibility(View.INVISIBLE);
                fragmentRegister.getTgl_gender().setVisibility(View.INVISIBLE);
                if (fragmentRegister.createHuman() == null||deatils.get("Username").replace(" ","").equals("Name")) {
                    Toast.makeText(context, "One Or MoreFields are Null", Toast.LENGTH_SHORT).show();
                }
                else {
                    String pos=deatils.get("position");
                    String gender=deatils.get("Gender");
                    deatils = CryptoUtils.encryptHuman((HashMap<String, String>)
                            fragmentRegister.createHuman().toMap());
                    try {
                        deatils.put("position", CryptoUtils.encrypt(pos));
                        deatils.put("Gender", CryptoUtils.encrypt(gender));
                    } catch (Exception e) {
                        Toast.makeText(context, "GENDER OR POSITION BAD", Toast.LENGTH_SHORT).show();

                        throw new RuntimeException(e);
                    }
                    if(!field.equals(deatils.get("PhoneNumber").replace(" ","")))
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            deatils.put("phoneChangedAt"+ LocalDate.now().toString(),field);
                        }
                    }
                    chooseShowFragment(true);
                    myprofile.setBackgroundColor(android.R.color.transparent);
                    server.updateFieldatHuman("human",deatils, field);

                    break;
                }

        }
    }

    private void chooseShowFragment(boolean b) {//true-profile false-register
        conditionFragment = b;
        if (b) {
            getSupportFragmentManager().beginTransaction()
                    .hide(fragmentRegister)
                    .show(fragmentProfile)
                    .commit();
            myprofile.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ActivityCompat.requestPermissions(
                            Profile.this,
                            new String[]{Manifest.permission.CAMERA},
                            100);

                    return false;
                }
            });

        } else {
            getSupportFragmentManager().beginTransaction()
                    .hide(fragmentProfile)
                    .show(fragmentRegister)
                    .commit();

        }
    }

    Drawable drawable;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Handle landscape orientation
            if (clickCount > 1)//0 or 1 is fragment profile mode
            {
                chooseShowFragment(false);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Handle portrait orientation

        }

    }

    public void setDeatils(HashMap<String, String> deatils) {
        this.deatils = deatils;
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentProfile.setCallback_profile(callback_profile);
        fragmentProfile.setListenerClick();
        fragmentProfile.putDeatilsWithHash(deatils);
        storage.showImage(fragmentProfile.getProfiler(), child, deatils.get("position"));
        Toast.makeText(context, child, Toast.LENGTH_SHORT).show();

        makeBtn();
        deleteImg();
    }





    private void openPermissionSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case (100):
                openPermissionSettings();
        }

    }
}


