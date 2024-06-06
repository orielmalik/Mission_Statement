package com.example.missionstatement.Fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.missionstatement.CallBackType.Callback_test;
import com.example.missionstatement.Firebase.Storage;
import com.example.missionstatement.Menu.Personality_Test;
import com.example.missionstatement.Objects.Test;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class FragmentQuest extends Fragment {
    private RadioGroup radioGroup;
    private MaterialTextView textView;
    private RadioButton []options;
    private MaterialButton submit;
    private static int counter=1;
    private Callback_test callback_test;
    private EditText editText;
    private View vieww;
    private boolean acceptToBuild=false;

    public FragmentQuest() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quest, container, false);
        initViews(view);
        Log.e("t",view.toString());
        setRadioButtonsExclusive(radioGroup);
        onAction();
        this.vieww=view;
        return view;
    }

    private void setRadioButtonsExclusive(RadioGroup radioGroup) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedButton = group.findViewById(checkedId);
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton button = (RadioButton) group.getChildAt(i);
                    if (button != selectedButton) {
                        button.setChecked(false);
                    }
                }
            }
        });
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




    public RadioGroup getRadioGroup() {
        return radioGroup;
    }


    public Callback_test getCallback_test() {
        return callback_test;
    }

    public void setCallback_test(Callback_test callback_test) {
        this.callback_test = callback_test;
    }



    public void onAction() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback_test != null) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId != -1) {
                        // Find the index of the selected radio button
                        for (int i = 0; i < radioGroup.getChildCount(); i++) {
                            RadioButton button = (RadioButton) radioGroup.getChildAt(i);
                            if (button.getId() == selectedId) {
                                callback_test.getResult(selectedId + 1);
                                break;
                            }
                        }
                        if (callback_test.getCounterQuestion(counter)) {
                            counter++;
                            getRadioGroup().clearCheck();
                            onAnswer();

                        }
                        Log.d("counter", "onClickCounter: " + counter);
                    } else {
                        // Handle the case when no radio button is selected
                        Log.d("submit", "No option selected");
                    }
                }
            }
        });
    }

    public  void onAnswer()
    {
        if(callback_test!=null) {
            callback_test.uploadAnswers(radioGroup,counter-1);
        }
    }



    public void onBuildAdmin()
    {
        for (int i = 0; i < getRadioGroup().getChildCount(); i++) {
            View view = getRadioGroup().getChildAt(i);
            int[]index=new int[]{i};
            if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleRadioButtonClick((RadioButton) v,index[0]);
                    }
                });
            }
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(isClear()|| !callback_test.canStartBuildTest())
              {
                  Toast.makeText(view.getContext(), "one or more buttons without text", Toast.LENGTH_SHORT).show();
              }
              else
              {
                  counter++;
              }
            }
        });


    }

    private void handleRadioButtonClick(RadioButton radioButton,int i) {
        // Enable the RadioButton for text input
        radioButton.setInputType(InputType.TYPE_CLASS_TEXT);
        radioButton.setFocusable(true);
        radioButton.setFocusableInTouchMode(true);
        radioButton.requestFocus();
        radioButton.setCursorVisible(true);
        if(callback_test!=null)
        {
            callback_test.setAnswer(radioButton.getText().toString(),i);
        }
    }

    public boolean isClear()
    {
        for (int i = 0; i < getRadioGroup().getChildCount(); i++) {
            RadioButton button = (RadioButton) radioGroup.getChildAt(i);
            if (button.getText().toString().isEmpty()) {

                return false;

            }
        }
        return true;

    }


    public void clear() {
        radioGroup.clearCheck();
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View view = radioGroup.getChildAt(i);
            if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                radioButton.setText("");

            }
        }
    }
}

