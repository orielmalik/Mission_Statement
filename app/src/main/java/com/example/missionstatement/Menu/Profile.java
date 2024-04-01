package com.example.missionstatement.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.missionstatement.CallBackType.Callback_profile;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Fragment.FragmentProfile;
import com.example.missionstatement.Fragment.FragmentRegister;
import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Objects.Human;
import com.example.missionstatement.Objects.Operator;
import com.example.missionstatement.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

import android.widget.Toast;

public class Profile extends AppCompatActivity {
    private HashMap<String,String> deatils;
    private MaterialButton ChangeButton,DescriptionButton;
    private FloatingActionButton delete;
    String child;
    private FrameLayout myprofile;
    private static boolean clicked=false;
    private Context context;
    private boolean conditionFragment;
    private FragmentRegister fragmentRegister;
    private Realtime server;
    private Human human;
    private FragmentProfile fragmentProfile;
    private  static  int backPressedCount=0, clickCount=0,DesClick=0;

    AppCompatImageView target_image;
    private Storage storage;
private AppCompatEditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Bundle b=getIntent().getBundleExtra("bundle");
        deatils=(HashMap<String, String>) b.getSerializable("deatils");
        server=new Realtime(this);
        fragmentProfile=new FragmentProfile();
        fragmentRegister=new FragmentRegister();
        ChangeButton=findViewById(R.id.profile_changeDetails);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.myProfile, fragmentProfile)
                .add(R.id.myProfile, fragmentRegister)
                .hide(fragmentRegister)
                .commit();
        myprofile=findViewById(R.id.myProfile);
        description=findViewById(R.id.profile_editText);
        context=this;
        storage=Storage.getInstance();
        child=(deatils.get("email")+"jpeg.jpg");
        // fragmentProfile.editProfile();
        delete=findViewById(R.id.BTN_removeProfiler);
        DescriptionButton=findViewById(R.id.profile_AddDescription);
        if(!deatils.get("position").equals("OPERATOR"))
        {
            DescriptionButton.setVisibility(View.INVISIBLE);
        }
    }
private void  deleteImg()
{
delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        storage.removeImg(deatils.get("")+"jpeg",child);
    }
});
}

    Callback_profile callback_profile=new Callback_profile() {
        @Override
        public void showImage(AppCompatImageView image) {
            storage.uploadImageToFirebase(image,child,deatils.get("position"));
            storage.showImage(image,child,deatils.get("position"));
        }


        @Override
        public String returnPosition() {
            return deatils.get("position");
        }
    };



    private void makeBtn()
    {
        if(clickCount==0){
            storage.showImage(fragmentProfile.getProfiler(),child,deatils.get("position"));

        }
        ChangeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clickCount++;
                chooseShowFragment(false);
                setLayoutParms();
                myprofile.setBackgroundColor(Color.rgb(102,133,34));
                storage.uploadImageToFirebase(fragmentProfile.getProfiler(),child,deatils.get("position"));
               if (clickCount>1)
               {
                   showYesNoDialog("Upload Deatils","Save the Changes",context,0);
               }
               else
               {//avoid dischange
                   fragmentRegister.putDeatilsWithHash(deatils);

               }

            }});

        DescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeButton.setVisibility(View.INVISIBLE);
                myprofile.setVisibility(View.INVISIBLE);
                description.setVisibility(View.VISIBLE);
                description.setClickable(true);

                DesClick++;
                if(DesClick>1)
                {
                    showYesNoDialog("Add Description","Save the Changes",context,1);

                }
            }
        });
    }
    private void  setLayoutParms()
    {
        ViewGroup.LayoutParams ProfileParams = myprofile.getLayoutParams();
        ProfileParams.height= 1500;
        RelativeLayout.LayoutParams btn_parm=(  RelativeLayout.LayoutParams)ChangeButton.getLayoutParams();
        btn_parm.setMargins(btn_parm.leftMargin, 43, btn_parm.rightMargin, btn_parm.bottomMargin);
        myprofile.setLayoutParams(ProfileParams);
        ChangeButton.setLayoutParams(btn_parm);
    }



    public  void showYesNoDialog(String title, String message,Context context,int condition) {
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
                 /*  chooseShowFragment(false);
                    myprofile.setBackgroundColor(android.R.color.transparent);*/

            }

        });



        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @SuppressLint("ResourceAsColor")
    private  void positiveBtnAction(int condition)
    {
        switch (condition) {
            case (0):
                String field = deatils.get("PhoneNumber");
                if (fragmentRegister.createHuman() == null) {
                    Toast.makeText(context, "One Or MoreFields are Null", Toast.LENGTH_SHORT).show();
                } else {
                    deatils = (HashMap<String, String>) fragmentRegister.createHuman().toMap();
                    server.updateFieldatHuman(deatils, field);
                    chooseShowFragment(true);
                    myprofile.setBackgroundColor(android.R.color.transparent);
                    clickCount = 0;
                    break;
                }
            case (1) :
                Operator operator=new Operator();
                operator.setEmail(deatils.get("email"));
                operator.writeAbout(description.getText().toString());
                server.getmDatabase().child("operator").setValue(operator.OperatorMap());
                DesClick=0;
                break;
        }
    }
    private void chooseShowFragment(boolean b)
    {//true-profile false-register
        conditionFragment=b;
        if(b)
        {
            getSupportFragmentManager().beginTransaction()
                    .hide(fragmentRegister)
                    .show(fragmentProfile)
                    .commit();

        }
        else {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(clickCount>0) {
            clickCount--;
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        fragmentProfile.setCallback_profile(callback_profile);
        fragmentProfile.setListenerClick();
        fragmentProfile.putDeatilsWithHash(deatils);
        makeBtn();
        deleteImg();
    }

}


