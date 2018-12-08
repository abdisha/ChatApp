package com.toshi.aerke.pigeonfly;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toshi.aerke.model.User;
import com.toshi.aerke.viewholder.FriendViewHolder;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link FriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //the firebase initialization objects;
    FirebaseAuth firebaseAuth;
    String UserId;
    DatabaseReference databaseReference;

    //the fragment component initializing
    RecyclerView recyclerView;
     List<String> Users;
     FloatingActionButton addNewFriend;
     TextView Empty;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FriendViewHolder viewHolder;

//the costume layout component



    public FriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendFragment newInstance(String param1, String param2) {
        FriendFragment fragment = new FriendFragment();
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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth =FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
              Empty =(TextView)view.findViewById(R.id.emptyExpression);
              recyclerView =(RecyclerView)view.findViewById(R.id.friendRecycler);
              recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
              recyclerView.setHasFixedSize(true);
              addNewFriend =(FloatingActionButton) view.findViewById(R.id.addNew);
              addNewFriend.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent FriendActivity =new Intent(getActivity(),People.class);
                      startActivity(FriendActivity);
                  }
              });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
      private void setRecyclerView(){
                    Users = new ArrayList<>();
          viewHolder = new FriendViewHolder(Users,getActivity());
                databaseReference.child("Friends").child(UserId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                            Log.i("output", "Friends Id: "+dataSnapshot1.child("UserId").getValue().toString());
                                            Users.add(dataSnapshot1.child("UserId").getValue().toString());
                                        }

                                      viewHolder.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                if(Users==null){
                       Empty.setVisibility(View.VISIBLE);
                }else {
                    Empty.setVisibility(View.GONE);
                    recyclerView.setAdapter(viewHolder);
                }



      }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        setRecyclerView();

    }
}
