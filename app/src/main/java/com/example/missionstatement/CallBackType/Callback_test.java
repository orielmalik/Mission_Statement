package com.example.missionstatement.CallBackType;
import android.widget.RadioGroup;

import com.example.missionstatement.Objects.Test;

import java.util.List;

public interface Callback_test {

     void uploadAnswers(RadioGroup radioGroup);
    void getResult(int index);

    boolean getCounterQuestion(int counter);

    void setAnswer(String string, int i);

    boolean canStartBuildTest();

    int state();

boolean isFirst();
void ToastFrom(String s);

}
