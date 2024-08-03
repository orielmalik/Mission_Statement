package com.example.missionstatement.CallBackType;

import com.example.missionstatement.Fragment.FragmentProfile;
import com.example.missionstatement.Objects.Operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Callback_list {
    void addToList(FragmentProfile f);
    void start() ;
    void initOperatorr(HashMap<String,Object>map,boolean b);
    void FilterBy(String criteria,String value);
    void  setFilter(String filter);
}
