package com.example.missionstatement.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.missionstatement.CallBackType.Callback_register;
import com.example.missionstatement.Objects.Human;
import com.example.missionstatement.R;
import com.example.missionstatement.Tools.Functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.widget.Toast;

public class FragmentRegister extends Fragment {
    private String gender, position;
    private View rootView;
    private Context context;
    private Human target;
    private EditText username;
    private EditText password;
    private EditText phoneNumber;
    private EditText Email;
    private AppCompatToggleButton tgl_position;
    private AppCompatToggleButton tgl_gender;
    private Callback_register callback_register;
    private static  int tglClick=0,genderClick=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_register, container, false);
        context = view.getContext();
        rootView=view;
        findViews(view);
        errorByRegex();

        // makeTgl(tgl_gender, "MALE", "FEMELE", Color.YELLOW, 0,genderClick);
        // makeTgl(tgl_position, "USER", "OPERATOR", Color.GRAY, 1,tglClick);
        return view;
    }

    private void findViews(View view)
    {
        setTgl_gender(view.findViewById(R.id.tgl_gender));
        setTgl_position(view.findViewById(R.id.tgl_position));
        setUsername(view.findViewById(R.id.register_username));
        password = view.findViewById(R.id.register_password);
        setEmail(view.findViewById(R.id.register_email));
        phoneNumber = view.findViewById(R.id.register_phoneNumber);
        BirthDateSpinner(view.getContext());


    }
    public Human createHuman() {
//check before and after if the deatils in numeric pattern-i did before and after for make sure without exception
        if (!toArr(getUsername().getText().toString(), phoneNumber.getText().toString(), getEmail().getText().toString(), password.getText().toString())) {
            return null;
        }


        target = new Human(phoneNumber.getText().toString(), getUsername().getText().toString(),
                password.getText().toString(), getEmail().getText().toString(), gender);

        if (!toArr(target.getUsername(), target.getPhoneNumber(), target.getEmail(), target.getPassword())) {
            return null;

        }
        if(getTgl_gender().getVisibility()==View.VISIBLE)
        {
            gender= getTgl_gender().getText().toString();

        }
        if(getTgl_position().getVisibility()==View.VISIBLE) {
            position = getTgl_position().getText().toString();
        }
        // upadateAgeFromSpinner();
        target.setPosition(position);
        target.setGender(gender);

        return target;

    }


    private void makeTgl(AppCompatToggleButton tgl, String option1, String option2, int opt1, int what,int clickCount)//what-kind of setting
    {
        //what-to provide dup code
        tgl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                position=compoundButton.getText().toString();

            }

        });
    }


    private void errorByRegex()//enable to act fragmentRegister
    {

        Functions.erroronEditText(getEmail(),"Only  @,numbers,letters ", "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Functions.erroronEditText(getUsername(),"Human Name (could contains family-Name) ",
                "/^[a-z ,.'-]+$/i*$");
        Functions.erroronEditText(phoneNumber,"Israel valid Phone Number" , "^[01]{1}[3-5]{1}[0-9]{5,9}$");
        //Functions.erroronEditText(password,"At least 6 digits 0-9 ", "^[0-9]{4,}$");
    }

    public  void removePresentedText()
    {
        Functions.RemovePrestentedText(getUsername());
        Functions.RemovePrestentedText(password);
        Functions.RemovePrestentedText(getEmail());
        Functions.RemovePrestentedText(phoneNumber);
    }
    private boolean toArr(String username,String phoneNumber,String Email,String password) //check if Fields in Runtime text==null
    {

        List<String> arr = new ArrayList<>();
        arr.add(username);
        arr.add(phoneNumber);
        arr.add(Email);
        arr.add(password);
        if(arr.contains(null))
        {

            Toast.makeText(getContext(),"NULL FIELD IS"+arr.indexOf(null)+1,Toast.LENGTH_SHORT);
            return  false;
        }
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        boolean numericPattern =(Functions.isValidPattern(Email.trim(),regex)&&
                // Functions.isValidPattern(password,"^[0-9]+$")&&
                /* Functions.isValidPattern(username,"^[A-Za-z]+([- ][A-Za-z]+)*$")*/
                Functions.isValidPattern(phoneNumber,   "^\\+?\\d{1,3}? ?\\d{1,4}? ?\\d{1,4}? ?\\d{1,4}? ?\\d{1,9}$"));

        ;

        return  numericPattern;

    }
    private void BirthDateSpinner(Context c)
    {
        // Create ArrayAdapter age 1-120
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_item);
        for (int i = 1; i <= 120; i++) {
            dayAdapter.add(String.valueOf(i));
        }

    }

    public void  putDeatilsWithHash(HashMap<String,String> deatils)  {
        if(deatils==null){return;}


        getEmail().setText(deatils.get("email"));
        getUsername().setText(deatils.get("Username"));
        password.setText(deatils.get("Password"));
        phoneNumber.setText(deatils.get("PhoneNumber"));
        position= getTgl_position().getText().toString();
        gender= getTgl_gender().getText().toString();

    }


    public Callback_register getCallback_register() {
        return callback_register;
    }

    public void setCallback_register(Callback_register callback_register) {
        this.callback_register = callback_register;
    }

    public EditText getEmail() {
        return Email;
    }

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public EditText getUsername() {
        return username;
    }

    public void setUsername(EditText username) {
        this.username = username;
    }

    public void setEmail(EditText email) {
        Email = email;
    }

    public AppCompatToggleButton getTgl_position() {
        return tgl_position;
    }

    public void setTgl_position(AppCompatToggleButton tgl_position) {
        this.tgl_position = tgl_position;
    }

    public AppCompatToggleButton getTgl_gender() {
        return tgl_gender;
    }

    public void setTgl_gender(AppCompatToggleButton tgl_gender) {
        this.tgl_gender = tgl_gender;
    }
}









