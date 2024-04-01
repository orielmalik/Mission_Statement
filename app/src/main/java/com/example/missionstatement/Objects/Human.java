package com.example.missionstatement.Objects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Human implements Serializable {
    protected int Age;
    private boolean edit=false;
    private   HashMap<String, String> result ;
    protected String  Email, Gender, Username, Password, position,PhoneNumber;
    //in this object i dont do exceptions - i check the insert data before always
    public static int id = 1;

    public Human() {
        id++;
    }


    public Human( String phonenumber, String Username, String password, String email, String gender) {
        this.PhoneNumber=phonenumber;
        this.Email=email;
        this.Username=Username;
        this.Password=password;
        this.Gender = gender;
        id++;
    }


    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getEmail() {
        return Email;
    }


    public String getGender() {
        return Gender;
    }


    public String getPassword() {
        return Password;
    }

    public String getUsername() {
        return Username;
    }

    public void setAge(int age) {
        this.Age = age;
    }
    public void setGender(String gender) {
        Gender = gender;
    }

    public  void setId(int i) {
        this.id = i;
    }


    public void setPassword(String password) {
        this.Password = password;
    }



    public void setUsername(String username){
        this.Username = username;
    }

    public boolean checkAge(int age) {
        return age < 121 && age > 0;
    }




    public void setPhoneNumber(String phoneNumber) {
            this.PhoneNumber = phoneNumber;

    }

    public void setEmail(String email) {
            this.Email = email;

    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public Human factory(String p)
    {
        switch (p)
        {

            case "USER":
                return new User(getPhoneNumber(),getUsername(),getPassword(),getEmail(),getGender());
            case "OPERATOR":
                return new Operator(getPhoneNumber(),getUsername(),getPassword(),getEmail(),getGender());
            case "MANAGER":

            default:
                return null;
        }
    }
    public Map<String, String> toMap() {
        result = new HashMap<>();

        result.put("Username",getUsername());
        result.put("Password",getPassword());
        result.put("email",getEmail());
        result.put("Gender",getGender());
        result.put("PhoneNumber", String.valueOf(getPhoneNumber()));
        result.put("position",getPosition());
        result.put("id",String.valueOf(getId()));

        return result;
    }

    public static int getId() {
        return id;
    }

    public Human fromMap(Map<String, String>map) {

      Human h=new Human(map.get("PhoneNumber"),map.get("Username"),map.get("Password"),map.get("email"),map.get("gender"));
      h.factory(map.get("position"));

        return h;
    }


    @Override
    public String toString()
    {
        return  "USERNAME :1"+getUsername()+"-\n Password is 2"+getPassword()+"\n position:3"+getPosition()+
                "4\n phoneNumber is 5"+getPhoneNumber();

    }



    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }


}
