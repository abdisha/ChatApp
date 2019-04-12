package com.toshi.aerke.Utilitis;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

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
    DatabaseReference connectedRef;
    private FirebaseAuth firebaseAuth;
    private boolean connectionStatus = true;
    public UserState() {
        firebaseAuth =FirebaseAuth.getInstance();
        DbRefence = FirebaseDatabase.getInstance().getReference();
        timeFormat = new SimpleDateFormat("hh:mm a");
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    }
    public static UserState getInstance(){
         return new UserState();

    }
    public void setUserState(boolean value){

        Date date = new Date();
        Map state = new HashMap();
        String Time = timeFormat.format(date);
        String Date = dateFormat.format(date);
        state.put("time", Time);
        state.put("date",Date);
        if(value && FirebaseUserStatus()){
            state.put("state","Online");
        }else {
            state.put("state","Offline");
        }
        if(firebaseAuth.getCurrentUser()!=null){
            UserId =firebaseAuth.getCurrentUser().getUid();
            DbRefence.child("User").child(UserId).child("UserState").setValue(state);


        }
    }

    private boolean FirebaseUserStatus(){

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected ) {
                    connectionStatus = true;
                    Log.i("output", "onDataChange: online true");
                } else {
                    connectionStatus =false;
                    Log.i("output", "onDataChange: offline ture");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
                connectionStatus =false;
            }
        });


return  connectionStatus;
    }


}


