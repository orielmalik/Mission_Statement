package com.example.missionstatement.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.missionstatement.CallBackType.Callback_profile;
import com.example.missionstatement.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;
import java.util.Map;

public class FragmentProfile extends Fragment {
    private MaterialTextView username,phoneNumber,email;
    private static  int PICK_IMAGE_REQUEST=1;
    private HashMap<String,String> Deatils;
    private LinearLayoutCompat linearLayoutCompat;
    private AppCompatRatingBar ratingBar;
    private AppCompatImageView icon,profiler;
    Drawable drawable;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Callback_profile callback_profile;
    private AppCompatSpinner spinner;
    private boolean isEditing=false;
    Uri selectedImageUri;
    private View rootView;
    private int currentIcon;

    //init to only watch version without editing
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findviews(view);
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageUri = data.getData();
                            profiler.setImageURI(selectedImageUri);
                        }
                    }
                });
        rootView=view;
        return view;
    }


    private   void  findviews(View view)
    {
        username=view.findViewById(R.id.profile_LBL_username);
        ratingBar=view.findViewById(R.id.operator_RTG_rating);
        phoneNumber=view.findViewById(R.id.profile_LBL_phoneNumber);
        email=view.findViewById(R.id.profile_LBL_email);
        icon=view.findViewById(R.id.profile_IMG_position);
        profiler=view.findViewById(R.id.profile_IMG);
    }


    public  void setListenerClick()
    {
        if(profiler.getDrawable()!=null)
        {
            callback_profile.showImage(profiler);
        }
        drawable=profiler.getDrawable();

        profiler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

    }

    public   void  showImageProfile()
    {
        openGallery();

    }

    public void  putDeatilsWithHash(HashMap<String,String>deatils)
    {
        findviews(rootView);
        if(deatils==null||deatils.containsValue(null)){return;}
        email.setText(deatils.get("email"));

        username.setText(deatils.get("Username"));
        phoneNumber.setText(deatils.get("PhoneNumber"));
        if(deatils.get("position").equals("USER"))
        {
            ratingBar.setVisibility(View.INVISIBLE);
            icon.setImageResource(R.drawable.ic_logouser);
        }
        else {
            ratingBar.setRating(0.5F);//this data will be by ratings of people

        }

    }
    public void  putOperatorWithHash(HashMap<String,String> deatils)
    {
        findviews(rootView);
        if(deatils==null){return;}


        phoneNumber.setText(deatils.get("PhoneNumber"));
        email.setText(deatils.get("area"));
        deatils.put("icon", String.valueOf(this.currentIcon));
        try {
            ratingBar.setRating(Float.parseFloat(deatils.get("rating")));
            icon.setImageResource(Integer.parseInt(deatils.get("icon")));

        }catch (NumberFormatException numberFormatException)
        {
            Log.d("NUMBEREX", ": "+numberFormatException.getMessage());
        }

    }
    public HashMap<String, String> getDeatils() {
        return Deatils;
    }

    public void setDeatils(HashMap<String, String> deatils) {
        Deatils = deatils;
    }

    public void showPopupMenu(View v) {
        PopupMenu popupMenu =new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.popu_menu); // Create a menu resource file
        int[]id={R.id.menu_item1,R.id.menu_item2,R.id.menu_item3,R.id.menu_item4,R.id.menu_item5};

        int[]Operatoricon={R.drawable.ic_operatorone,R.drawable.ic_operatortwo,R.drawable.ic_operatorthree,R.drawable.ic_operatorfour,R.drawable.ic_operatorsix,R.drawable.ic_operatorseven};
        int[]Usericon={R.drawable.ic_userone,R.drawable.ic_usertwo,R.drawable.ic_userthree,R.drawable.ic_userfour,R.drawable.ic_usersix,R.drawable.ic_operatorseven};

        for (int i = 0; i <id.length ; i++) {
            popupMenu.getMenu().findItem(id[i]).setTitle("Option"+(i+1) );
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                putIcon(menuItem,Operatoricon,Usericon,callback_profile.returnPosition().equals("OPERATOR"),id);
                return  true;
            }
        });

        popupMenu.show();
    }
    private void putIcon(MenuItem menuItem,int[] sourceOperator,int[] sourceUser,boolean b,int[] id)
    {
        int []source;
        if(b)//operator
        {
            source=sourceOperator;
        }
        else {
            source=sourceUser;
        }
        int pid=0;
        for (int i = 0; i <id.length ; i++) {
            if(menuItem.getItemId()==id[i])
            {
                pid= i;
            }
        }
        menuItem.setIcon(source[pid]);
        icon.setImageResource(source[pid]);
        this.currentIcon=source[pid];
    }



    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(galleryIntent);

    }


    public Callback_profile getCallback_profile() {
        return callback_profile;
    }

    public void setCallback_profile(Callback_profile callback_profile) {
        this.callback_profile = callback_profile;
    }

    public AppCompatImageView getProfiler() {
        return profiler;
    }
}






