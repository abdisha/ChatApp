package com.toshi.aerke.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.toshi.aerke.pigeonfly.Chat;
import com.toshi.aerke.pigeonfly.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendViewHolder extends RecyclerView.Adapter<FriendViewHolder.Views>{
         List<String> userId;
         DatabaseReference databaseReference;
         Context context;
    private String fullName,lastSeen,Image,active;

    public FriendViewHolder(List<String> userId,Context context) {
        this.userId = userId;
        this.context = context;
    }

    @NonNull
    @Override
    public Views onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.costume_friend_layout,null);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        Log.i("output", "onCreateViewHolder: "+userId);
        return new Views(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Views holder, final int position) {
                   final String UserId = userId.get(position);

        Log.i("ouput", "onBindViewHolder friend found: "+UserId);
                   if(!UserId.equals(null)){
                       databaseReference.child(UserId).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Image = dataSnapshot.child("image").getValue().toString();
                                    fullName = dataSnapshot.child("fullName").getValue().toString();
                                    lastSeen = dataSnapshot.child("UserState/date").getValue().toString();
                                    active = dataSnapshot.child("UserState/state").getValue().toString();
                                }
                               holder.setContent(Image,active,fullName,lastSeen);

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                       holder.cardView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                                    String ownerId = userId.get(position);
                               Log.i("output", "onClick Friend USer Id: "+ownerId);
                                        Intent chat =new Intent(context.getApplicationContext(), Chat.class);
                                       chat.putExtra("userId",ownerId);
                                       chat.putExtra("fullName",fullName);
                                       chat.putExtra("lastSeen",lastSeen);
                                       chat.putExtra("image",Image);
                                        context.startActivity(chat);

                           }
                       });

                   }

    }

    @Override
    public int getItemCount() {
        return userId.size();
    }

    public class Views extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        CardView cardView;
        TextView name,lastSeen,status;
        public Views(View itemView) {
            super(itemView);
            name =(TextView)itemView.findViewById(R.id.txtFullNameFriend);
            lastSeen =(TextView)itemView.findViewById(R.id.txtLastSeenFriend);
            status =(TextView)itemView.findViewById(R.id.txtStatus);
            cardView =(CardView)itemView.findViewById(R.id.cardViewFriend);
            circleImageView =(CircleImageView)itemView.findViewById(R.id.profileImageFriend);
        }

        public void setContent(String Image,String Status,String Name,String LastSeen){
            if(!Image.equals(null)){
                final String image = Image;
                Picasso.get().load(Image).networkPolicy(NetworkPolicy.OFFLINE)
                        .into(circleImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(image).into(circleImageView);

                            }
                        });
            }
            if(!status.equals(null))
                status.setText(Status);
            if(!LastSeen.equals(null))
                lastSeen.setText("Last seen "+LastSeen);
            if(!Name.equals(null))
                name.setText(Name);
        }
    }
}
