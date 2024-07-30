package com.example.missionstatement.Objects;

import com.example.missionstatement.Tools.Functions;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User extends Human  implements Serializable {

    private List<Map<String, Object>> tests = new ArrayList<>();
    private String birthdate;
    private String location;
    private  boolean driver;
    private int countChangeDate = 0;
    private boolean isManager = false;
    private int psychometric=0;
    private String description;//about what he wants from test
    private String descriptionText;

    public User(String phonenumber, String Username, String password, String email, String gender) {
        super(phonenumber, Username, password, email, gender);

    }

    public User() {
    }

    public User(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
    }

    public String getPosition() {
        return "USER";
    }


    public Map<String, Object> ResultMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ph", getPhoneNumber());
        result.put("description", getDescription());
        result.put("birthdate", getBirthdate());
        result.put("location", getLocation());
        result.put("countChangeDate", getCountChangeDate());
        result.put("isManager", isManager());
        result.put("psych", getPsychometric());
        result.put("driver", isDriver());
        result.put("descriptionText", getDescriptionText());
        result.put("tests", getTestsMap());
        return result;
    }
    // Method to create a User object from a HashMap
    public static User fromMap(HashMap<String, Object> map) {
        User user = new User();
        user.setPhoneNumber((String) map.get("ph"));
        user.setDescription((String) map.get("description"));
        user.setBirthdate((String) map.get("birthdate"));
        user.setLocation((String) map.get("location"));
        user.setCountChangeDate((int) map.get("countChangeDate"));
        user.setManager((boolean) map.get("isManager"));
        user.setDescriptionText((String) map.get("descriptionText"));
        user.setDriver((boolean) map.get("driver"));
        user.setPsychometric((int)map.get("psych"));
        // Assuming tests is stored as List<Map<String, Test>>
        List<Map<String, Object>> testsList = (List<Map<String, Object>>) map.get("tests");
        user.tests = new ArrayList<>();
        if (testsList != null) {
            for (Map<String, Object> testMap : testsList) {
                Map<String, Object> testEntry = new HashMap<>();
                for (Map.Entry<String, Object> entry : testMap.entrySet()) {
                    testEntry.put(entry.getKey(), entry.getValue());
                }
                user.tests.add(testEntry);
            }
        }
        return user;
    }
    private List<Map<String, Object>> getTestsMap() {
        return tests;
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

    public void setBirthdate(String birthdate) {
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

    public String getBirthdate() {
        return birthdate;
    }

    //                    if (test.getFileLocation().getPath().replace(" ","").equals(Functions.sanitizeKey(path.replace(" ","")))) {
    public boolean isTestName(String path) {
        boolean ok = false;
        if(tests==null||tests.isEmpty())
        {
            return  false;
        }
        for (int i = 0; i <tests.size() ; i++) {
            Map<String,Object>map=tests.get(i);
            if(map.containsKey(Functions.sanitizeKey(path)))
            {
                return  true;
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


    public List<Map<String, Object>> getTests() {
        return tests;
    }

    public void setTests(List<Map<String, Object>> tests) {
        this.tests = tests;
    }

    public int getPsychometric() {
        return psychometric;
    }

    public void setPsychometric(int psychometric) {
        this.psychometric = psychometric;
    }

    public boolean isDriver() {
        return driver;
    }

    public void setDriver(boolean driver) {
        this.driver = driver;
    }
}