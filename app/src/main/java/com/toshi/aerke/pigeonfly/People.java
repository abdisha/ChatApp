package com.toshi.aerke.pigeonfly;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.toshi.aerke.model.User;
import com.toshi.aerke.viewholder.PeopleViewHolder;

public class People extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private RecyclerView peoplesContainer;
    private ProgressBar bar;
    Toolbar toolbar;
    private DatabaseReference databaseReference;
    private String currentUserId;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        peoplesContainer =(RecyclerView)findViewById(R.id.peopleRecyclerView);
        bar =(ProgressBar)findViewById(R.id.Loading);
        peoplesContainer.setLayoutManager(new LinearLayoutManager(this));
        peoplesContainer.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);actionBar.setDisplayHomeAsUpEnabled(true);

                // databaseReferenceRequest =FirebaseDatabase.getInstance().getReference();
        currentUserId =FirebaseAuth.getInstance().getCurrentUser().getUid();
        bar.setVisibility(View.VISIBLE);
    }
    private void SetRecyclerView(){

        FirebaseRecyclerOptions<User> options =new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(databaseReference,User.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, PeopleViewHolder>(options) {
            @NonNull
            @Override
            public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.people_layout,parent,false);
                return new PeopleViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PeopleViewHolder holder, final int position, @NonNull  User model) {
                holder.setImage(model.getImage());
                holder.setNameandBio(model.getFullName(),model.getBio());
                String ownerId= getRef(position).getKey();





                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String UserId = getRef(position).getKey();
                        Intent intent = new Intent(People.this,profileViewer.class);
                        intent.putExtra("UserId",UserId);
                        startActivity(intent);
                    }
                });
            }
        };
        bar.setVisibility(View.GONE);
        peoplesContainer.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        SetRecyclerView();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
