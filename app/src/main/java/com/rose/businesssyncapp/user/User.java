package com.rose.businesssyncapp.user;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuzalj on 3/25/2017.
 */
@IgnoreExtraProperties
public class User {

    public String email;
    public String firstName;
    public String lastName;
    public ArrayList<String> cards;

    public User(){

    }

    public User(String email, String firstName, String lastName){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String email){
        this.email = email;
    }

}
