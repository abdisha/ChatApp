package com.toshi.aerke.pigeonfly;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.toshi.aerke.model.User;
import com.toshi.aerke.viewholder.RequestViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
   // Component
    private RecyclerView recyclerView;
    //firebase component initializing
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private RequestViewHolder requestViewHolder;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<String> requestUserId;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
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
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        firebaseAuth =FirebaseAuth.getInstance();
        View view= inflater.inflate(R.layout.fragment_request, container, false);
          recyclerView = (RecyclerView)view.findViewById(R.id.requestRecycler);
          recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
          recyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        String userId = firebaseAuth.getCurrentUser().getUid();
        requestUserId = new ArrayList<>();
        requestViewHolder = new RequestViewHolder(requestUserId);
          databaseReference.child("Request").child(userId).child("ReceivedRequest")
                  .addChildEventListener(new ChildEventListener() {
                      @Override
                      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                           if(dataSnapshot.exists()){
                               requestUserId.add(dataSnapshot.child("SenderID").getValue().toString());
                           }
                           requestViewHolder.notifyDataSetChanged();

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
          recyclerView.setAdapter(requestViewHolder);

    }
}
