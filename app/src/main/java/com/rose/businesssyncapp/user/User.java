package com.rose.businesssyncapp.user;

import android.graphics.Bitmap;
import com.google.firebase.database.Exclude;
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
    public String lastName, phone, wrkemail, company;
    public ArrayList<String> cards;
    @Exclude
    public String userID;
    @Exclude
    public Bitmap bitmap;

    public User(){

    }

    public User(String email, String firstName, String lastName, String wrkemail, String phone, String company){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.wrkemail = wrkemail;
        this.phone = phone;
        this.company = company;
    }

    public User(String email){
        this.email = email;
    }

}
