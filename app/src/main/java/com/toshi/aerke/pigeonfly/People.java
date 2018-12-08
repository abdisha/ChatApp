package com.toshi.aerke.pigeonfly;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.toshi.aerke.model.User;
import com.toshi.aerke.viewholder.PeopleAdapter;
import com.toshi.aerke.viewholder.PeopleViewHolder;

import java.util.ArrayList;
import java.util.List;

public class People extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private RecyclerView peoplesContainer;
    private ProgressBar bar;
    Toolbar toolbar;
    private DatabaseReference databaseReference;
    private String currentUserId;
    ValueEventListener  valueEventListener;
    private List<User> userList = new ArrayList<>();
    private PeopleAdapter peopleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        peoplesContainer =(RecyclerView)findViewById(R.id.peopleRecyclerView);
        bar =(ProgressBar)findViewById(R.id.Loading);
        peoplesContainer.setLayoutManager(new LinearLayoutManager(this));
        peoplesContainer.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.keepSynced(true);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
                // databaseReferenceRequest =FirebaseDatabase.getInstance().getReference();
        currentUserId =FirebaseAuth.getInstance().getCurrentUser().getUid();
        peopleAdapter = new PeopleAdapter(userList,this);
       valueEventListener = new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists() && !dataSnapshot.equals(null)){
                  for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                      User user = new User(snapshot.child("fullName").getValue().toString(),
                              snapshot.child("nickName").getValue().toString(),
                              snapshot.child("image").getValue().toString(),
                              snapshot.child("bio").getValue().toString(),
                              snapshot.child("uid").getValue().toString());
                              userList.add(user);
                      Log.i("output", "onDataChange: search people and recycler adapter"+user.getFullName());
                  }

               }
               peopleAdapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       };
    }
    private void setFirebaseRecyclerAdapter(){
        Query query;


            query =FirebaseDatabase.getInstance().getReference("User");


             peopleAdapter = new PeopleAdapter(userList,this);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               if(dataSnapshot.exists() && !dataSnapshot.equals(null)){
                   User user = dataSnapshot.getValue(User.class);


                   userList.add(user);
               }
                peopleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        bar.setVisibility(View.GONE);
       peoplesContainer.setAdapter(peopleAdapter);

    }
         private void searchFirebase(String searchKey){
        userList.clear();
             Query query=  FirebaseDatabase.getInstance().getReference("User")
                     .orderByChild("fullName").startAt(searchKey).endAt(searchKey+ "\uf8ff");
             Log.i("output", "setFirebaseRecyclerAdapter: "+query);
             query.addListenerForSingleValueEvent(valueEventListener);
             peoplesContainer.setAdapter(peopleAdapter);
         }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu_item,menu);
        MenuItem menuItem = menu.findItem(R.id.search_menu);
        MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setMenuItem(menuItem);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.isEmpty()){
                    searchFirebase(query);
                    Toast.makeText(getApplicationContext(),"Onquery Submit",Toast.LENGTH_LONG).show();

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                peoplesContainer.removeAllViews();
                searchFirebase(newText);
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                userList.clear();
            }

            @Override
            public void onSearchViewClosed() {
                userList.clear();
                setFirebaseRecyclerAdapter();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        bar.setVisibility(View.VISIBLE);
        setFirebaseRecyclerAdapter();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();

    }


}
