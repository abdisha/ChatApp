package com.toshi.aerke.pigeonfly;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toshi.aerke.Utilitis.StatusUpdate;
import com.toshi.aerke.Utilitis.UserState;

public class Home extends AppCompatActivity {
        private Toolbar toolbar;
         private StatusUpdate statusUpdate;
         FirebaseAuth firebaseAuth;
         String userId = null;
        private BottomNavigationView bottomNavigationView;
    private ActionBar actionBar;
    private DatabaseReference databaseReferenceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar)findViewById(R.id.HomeToolbar);
        setSupportActionBar(toolbar);
        actionBar =getSupportActionBar();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null){
            userId =firebaseAuth.getCurrentUser().getUid();
            databaseReferenceStatus = FirebaseDatabase.getInstance().getReference("User/"+userId+"/UserState");

            statusUpdate=StatusUpdate.getInstance(userId);
            statusUpdate.ChangeStatus(true
            );
            databaseReferenceStatus.onDisconnect().updateChildren(statusUpdate.MapedUser());
            fragmentController();
        }



    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.FragmentContainer,fragment,null).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private  void fragmentController(){
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.menuNavigation);
        actionBar.setTitle("Chat");
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
        setFragment(new ChatFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_chat:
                        setFragment(new ChatFragment());
                        actionBar.setTitle("Chat");
                        return true;
//                     case R.id.menu_contact:
//                         return true;
                    case R.id.menu_friend:
                        setFragment(new FriendFragment());
                        actionBar.setTitle("Friend");
                        return true;
                    case R.id.menu_request:
                        setFragment(new RequestFragment());
                        actionBar.setTitle("Request");
                        return true;
                    case R.id.menu_setting:
                        setFragment(new SettingFragment());
                        actionBar.setTitle("Setting");
                        return true;
                }
                return false;
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (userId!=null){
            statusUpdate.ChangeStatus(false);
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (userId!=null){
            statusUpdate.ChangeStatus(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser()==null){
            startActivity(new Intent(Home.this,splashScreenActivity.class));
        }
    }
}
