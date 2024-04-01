package com.example.missionstatement.Objects;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    private String version;
    private String category;
    private List<String>questions=new ArrayList<>();
    //get his info from text file
    public Test(){
    };

    public void  fillContent(String line, BufferedReader reader )  {
        switch(line.toLowerCase()) {
            case "version":
                    version =(line.substring(line.indexOf("="), line.indexOf(";")));
                break;
            case "category":
                category=(line.substring(line.indexOf("="), line.indexOf(";")));
            case  "qusetions":
                for (int i = 0; i < 8; i++) {
                    try {
                        reader.skip(i);
                        questions.add(line.substring(line.indexOf("<"),line.indexOf(">")));
                    }catch (IOException w)
                    {
                        Log.d(null,"fill"+w.getMessage());
                    }

                }
        }

    }
    @NonNull
    @Override
    public String toString() {

        return  " Category "+getCategory()+ "\n"+ "Version"+getVersion();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
