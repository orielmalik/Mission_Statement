package com.example.missionstatement.CallBackType;

import com.example.missionstatement.Objects.Human;

public interface Callback_register {
  Human createHuman();//get human check if we can create new human
  boolean clickedNull( );


    void onDataRecived(boolean b);
}
