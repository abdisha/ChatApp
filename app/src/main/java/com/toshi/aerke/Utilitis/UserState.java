package com.toshi.aerke.Utilitis;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        timeFormat = new SimpleDateFormat("HH:mm a");
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }
    public static UserState getInstance(String UserId){
         return new UserState(UserId);

    }
    public void setUserState(boolean State){
        Map state = new HashMap();
        state.put("time",timeFormat.getCalendar().getTime());
        state.put("date",dateFormat.getCalendar().getTime());
        if(State){
            state.put("state","Online");
        }else {
            state.put("state","Online");
        }
        DbRefence.child("User").child(UserId).child("UserState").setValue(state);
    }
}

