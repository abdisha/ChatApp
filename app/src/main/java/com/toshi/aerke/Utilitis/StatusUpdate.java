package com.toshi.aerke.Utilitis;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatusUpdate {
    private String Uid;
    private SimpleDateFormat simpleTimeFormat;
    private SimpleDateFormat simpleDateFormat;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Calendar cal;

    public StatusUpdate(String Uid){
        this.Uid = Uid;
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(Uid).child("UserState");
        simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        simpleTimeFormat = new SimpleDateFormat("HH:mm: a");
         cal = Calendar.getInstance();
    }
    public static StatusUpdate getInstance(String UId){
        return  new StatusUpdate(UId);
    }

    public  void ChangeStatus(boolean status){
         if (status){
             updateStatusToOnline();
         }else{
             updateStatusToOffLine();
         }
    }

    private void updateStatusToOnline() {
       Date date = cal.getTime();
       String TimeStamp = simpleTimeFormat.format(date);
       String DateStamp = simpleDateFormat.format(date);
        Map status = new HashMap<>();
        status.put("date",DateStamp);
        status.put("state","Online");
        status.put("time",TimeStamp);
       databaseReference.updateChildren(status);

    }

    private void updateStatusToOffLine() {
        Date date = cal.getTime();
        String TimeStamp = simpleTimeFormat.format(date);
        String DateStamp = simpleDateFormat.format(date);

        Map<String,Object> status = new HashMap<>();
        status.put("date",DateStamp);
        status.put("state","OffLine");
        status.put("time",TimeStamp);
        databaseReference.updateChildren(status);
    }
    public   Map MapedUser(){
        Date date = cal.getTime();
        String TimeStamp = simpleTimeFormat.format(date);
        String DateStamp = simpleDateFormat.format(date);
        Map<String,Object> status = new HashMap<>();
        status.put("date",DateStamp);
        status.put("state","OffLine");
        status.put("time",TimeStamp);
        return  status;
    }

}
