package com.toshi.aerke.pigeonfly;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.toshi.aerke.Utilitis.UserState;

public class Home extends AppCompatActivity {
        private Toolbar toolbar;
         UserState userState;
         FirebaseAuth firebaseAuth;
         String userId = null;
        private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar)findViewById(R.id.HomeToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar =getSupportActionBar();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            userId =firebaseAuth.getCurrentUser().getUid();
            if(userId!=null)
                userState = UserState.getInstance(userId);
        }
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
                         return true;
                     case R.id.menu_contact:
                         setFragment(new ContactFragment());
                         return true;
                     case R.id.menu_friend:
                         setFragment(new FriendFragment());
                         return true;
                     case R.id.menu_request:
                         setFragment(new RequestFragment());
                         return true;
                     case R.id.menu_setting:
                         setFragment(new SettingFragment());
                         return true;
                 }
                 return false;
             }
         });
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.FragmentContainer,fragment,null).addToBackStack(null).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
       if(userState !=null)
           userState.setUserState(true);
    }

}
