package com.example.missionstatement.Fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.missionstatement.CallBackType.Callback_test;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class FragmentQuest extends Fragment {
    private RadioGroup radioGroup;
    private MaterialTextView textView;
    private RadioButton []options;
private MaterialButton submit;
private Callback_test callback_test;
   @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quest, container, false);
initViews(view);

        return view;
    }
    private void  initViews(View view)
    {
        options=new RadioButton[4];
        submit=view.findViewById(R.id.BTN_test_ok);
        radioGroup=view.findViewById(R.id.test_radio_group);
        options[0]=view.findViewById(R.id.BTN_test_opt1);
        options[1]=view.findViewById(R.id.BTN_test_opt2);
        options[2]=view.findViewById(R.id.BTN_test_opt3);
        options[3]=view.findViewById(R.id.BTN_test_opt4);

    }
    private void setRadioButtonsExclusive(RadioGroup radioGroup) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Get the selected radio button
                RadioButton selectedButton = group.findViewById(checkedId);

                // Iterate through all radio buttons in the group
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton button = (RadioButton) group.getChildAt(i);
                    // Set all other radio buttons to false except the selected one
                    if (button != selectedButton) {
                        button.setChecked(false);
                    } else {
                        callback_test.uploadIndex(i + 1);
                    }
                }
            }

        });
   }

            public void onAction(String[] arr) {
                if (arr.length != options.length) {
                    return;
                }
                for (int i = 0; i < options.length; i++) {
                    options[i].setText(arr[i]);
                }
                setRadioButtonsExclusive(radioGroup);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }  

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }



}

