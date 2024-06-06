package com.example.missionstatement.Objects;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User extends Human  implements Serializable {

    private List<Test> tests=new ArrayList<>();
    private LocalDate birthdate;
    private String location;
    private int countChangeDate=0;
private boolean isManager=false;

    private String description;//about what he wants from test
private String descriptionText;

    public User(String phonenumber, String Username, String password, String email, String gender) {
        super(phonenumber, Username, password, email, gender);

    }

    public User(){}

    public User(String phoneNumber)
    {
        this.setPhoneNumber(phoneNumber);
    }

    public String getPosition() {
        return "USER";
    }


    public Map<String, Object> UserMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ph", getPhoneNumber());
        result.put("description", getPhoneNumber());

        return result;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public int getCountChangeDate() {
        return countChangeDate;
    }

    public void setCountChangeDate(int countChangeDate) {
        this.countChangeDate = countChangeDate;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }


    public boolean isTestName(String path)
    {
        boolean ok=false;
        for (Test test:tests) {
            if(test.getFileLocation().getPath().equals(path))
            {
             ok=true;
            }
        }
        return  ok;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }
}

