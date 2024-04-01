package com.example.missionstatement.Objects;

import com.example.missionstatement.Objects.Human;

import java.util.HashMap;
import java.util.Map;

public class User extends Human {

    private Test[] tests;

    public User(String phonenumber, String Username, String password, String email, String gender) {
        super(phonenumber, Username, password, email, gender);

    }

    public String getPosition() {
        return "USER";
    }


    public Map<String, Object> UserMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", getEmail());
        for (int i = 0; i < tests.length; i++) {
            if (tests[i] != null) {
                result.put("tests", tests[i].toString());
            }
        }
        return result;
    }
}

