package com.example.missionstatement.Objects;

import com.example.missionstatement.Objects.Human;

import java.util.HashMap;
import java.util.Map;

public class Operator extends Human {
private enum proffesion{EDUCATION,FITNESS,CULUTURE,FINANCIAL};
 private  int rating,iconOption;
private String description;
    public Operator( String phonenumber, String Username, String password, String email, String gender)
    {
        super(phonenumber, Username, password, email, gender);
setPosition("OPERATOR");// usually Automaticially come before
    }

    public Operator() {

    }



    public Map<String, String> OperatorMap() {
        HashMap<String, String>result= new HashMap<>();
        result.put("email",getEmail());
        result.put("description",getDescription());
        return  result;
    }

    public void writeAbout(String text)
    {
     if(text.length()==Integer.MAX_VALUE)
     {
         description=text.substring(0,Integer.MAX_VALUE);
     }

    }







    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public int getIconOption() {
        return iconOption;
    }
    public void setIconOption(int iconOption) {
        this.iconOption = iconOption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
