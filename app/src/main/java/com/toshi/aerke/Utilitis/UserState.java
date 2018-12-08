package com.toshi.aerke.Utilitis;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserState {
    private String UserId;
    private DatabaseReference DbRefence;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;
    public UserState(String userId) {

        this.UserId = userId;
        DbRefence = FirebaseDatabase.getInstance().getReference();
        timeFormat = new SimpleDateFormat("hh:mm a");
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }
    public static UserState getInstance(String UserId){
         return new UserState(UserId);

    }
    public void setUserState(boolean State){
        Date date = new Date();
        Map state = new HashMap();
        String Time = timeFormat.format(date);
        String Date = dateFormat.format(date);
        state.put("time", Time);
        state.put("date",Date);
        if(State){
            state.put("state","Online");
        }else {
            state.put("state","Offline");
        }
        DbRefence.child("User").child(UserId).child("UserState").setValue(state);
    }
}


