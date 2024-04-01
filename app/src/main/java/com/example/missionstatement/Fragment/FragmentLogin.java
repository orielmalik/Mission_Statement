package com.example.missionstatement.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.ViewGroup;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.missionstatement.CallBackType.Callback_login;
import com.example.missionstatement.R;
import com.example.missionstatement.Tools.Functions;
import com.google.android.material.textview.MaterialTextView;

public class FragmentLogin extends Fragment {
    private EditText email, password;
    private MaterialTextView login;
    private Callback_login callback_login;
    private AppCompatButton btn_start;
    private String username_str, password_str, position;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_login, container, false);
        email=view.findViewById(R.id.lbl_email);
        password=view.findViewById(R.id.lbl_password);
        Functions.RemovePrestentedText(email);
        Functions.RemovePrestentedText(password);
        login=view.findViewById(R.id.txtlogin);

        return view;
    }

    public String getPassword() {
        return password.getText().toString();
    }

    public String getEmail() {
        return email.getText().toString();
    }
public  void getData()
{
    callback_login.transportEmailPassword(getEmail(),getPassword());
}

    public void setCallback_login(Callback_login callback_login) {
        this.callback_login = callback_login;
    }

    public  void Showpassword()
    {
        password.setVisibility(View.INVISIBLE);
        login.setText("Enter your Email to Show your Password");

    }
}