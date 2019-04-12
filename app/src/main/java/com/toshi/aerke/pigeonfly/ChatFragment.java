package com.toshi.aerke.pigeonfly;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.toshi.aerke.model.Message;
import com.toshi.aerke.model.User;
import com.toshi.aerke.model.UserState;
import com.toshi.aerke.viewholder.ChatListViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //Initializing firebase component
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    //view controller initailize
    private RecyclerView chatListRecyclerView;

        ValueEventListener valueEventListener;
        List<User> FriendsUId =new ArrayList<>();
        ChatListViewHolder chatListViewHolder;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_chat, container, false);
          chatListRecyclerView =(RecyclerView)view.findViewById(R.id.chatListRecyclerView);
          chatListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
          chatListRecyclerView.setHasFixedSize(true);

          databaseReference = FirebaseDatabase.getInstance().getReference();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth =FirebaseAuth.getInstance();
        final String userId = firebaseAuth.getCurrentUser().getUid();
        chatListViewHolder = new ChatListViewHolder(FriendsUId,getActivity());
       FriendsUId.clear();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        FirebaseDatabase.getInstance().getReference().child("User")
                                .child(snapshot.child("UserId").getValue().toString())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            User user = dataSnapshot.getValue(User.class);
                                             if ( dataSnapshot.child("UserState").hasChildren()){
                                                 user.setUserState(dataSnapshot.getValue(UserState.class));
                                                 Log.i("output", "onDataChange ChatList: "+dataSnapshot.getValue(UserState.class));

                                             }

                                            FriendsUId.add(user);
                                        }
                                        chatListViewHolder.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };



         Query query =FirebaseDatabase.getInstance().getReference("Friends").child(userId).orderByChild("UserId");
         query.addListenerForSingleValueEvent(valueEventListener);
       chatListRecyclerView.setAdapter(chatListViewHolder);



    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
