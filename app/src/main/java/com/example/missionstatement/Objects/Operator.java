package com.example.missionstatement.Objects;

import com.example.missionstatement.AREA;
import com.example.missionstatement.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operator extends Human {
    private  int icon;
    private  float rating;
    private AREA area;
    private List<String> clients;
    private Category proffession;
    private String description;
    public Operator( String phonenumber, String Username, String password, String email, String gender)
    {
        super(phonenumber, Username, password, email, gender);
        setPosition("OPERATOR");// usually Automaticially come before
    }

    public Operator() {

    }

    public Operator(Category category, AREA area,String description,float rating,int icon)
    {
        this.area=area;
        this.proffession=category;
        this.description=description;
        this.rating=rating;
        this.icon=icon;
        this.clients=new ArrayList<>();

    }

    public Map<String, Object> OperatorMap() {
        HashMap<String, Object> results = new HashMap<>();
        HashMap<String, Object> result = new HashMap<>();
        result.put("area",getArea().name());
        result.put("PhoneNumber",getPhoneNumber());
        result.put("email",getEmail());
        result.put("category", getProffession().name());
        result.put("description", getDescription());
        result.put("rating", String.valueOf(getRating()));
        result.put("icon", String.valueOf(getIcon()));
        result.put("clients", getClients());
        results.put(getPhoneNumber(),result);
        return results;
    }    public HashMap<String, String> OperatorStringMap() {
        HashMap<String, String> results = new HashMap<>();
        HashMap<String, String> result = new HashMap<>();
        result.put("area",getArea().name());
        result.put("category", getProffession().name());
        result.put("description", getDescription());
        result.put("rating", String.valueOf(getRating()));
        result.put("icon", String.valueOf(getIcon()));
        result.put("PhoneNumber",getPhoneNumber());
        result.put("email",getEmail());
        return result;
    }

    public void writeAbout(String text)
    {
        if(text.length()==Integer.MAX_VALUE)
        {
            description=text.substring(0,Integer.MAX_VALUE);
        }

    }







    public float getRating() {
        return rating;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AREA getArea() {
        return area;
    }

    public void setArea(AREA area) {
        this.area = area;
    }

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }

    public Category getProffession() {
        return proffession;
    }

    public void setProffession(Category proffession) {
        this.proffession = proffession;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
