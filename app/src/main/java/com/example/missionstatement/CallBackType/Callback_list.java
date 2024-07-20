package com.example.missionstatement.CallBackType;

import com.example.missionstatement.Fragment.FragmentProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Callback_list {
    void addToList(FragmentProfile f);
    void start();
    void  addHashList(HashMap<String,String>map, List<Map<String,String>>lst);

}
