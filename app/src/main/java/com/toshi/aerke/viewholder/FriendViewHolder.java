package com.toshi.aerke.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.toshi.aerke.model.User;
import com.toshi.aerke.model.UserState;
import com.toshi.aerke.pigeonfly.Chat;
import com.toshi.aerke.pigeonfly.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendViewHolder extends RecyclerView.Adapter<FriendViewHolder.Views>{
         private List<String> userId;
         private List<User> userList;
         private UserState state;
         private DatabaseReference databaseReference;
         Context context;
    private String fullName="",Bio="",Image = "";

    public FriendViewHolder(List<String> userId,Context context) {
        this.userId = userId;
        this.context = context;
        this.userList = new ArrayList<>();
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
                                    Image = dataSnapshot.child("image").getValue() == null? "":dataSnapshot.child("image").getValue().toString();
                                    fullName = dataSnapshot.child("fullName").getValue().toString();
                                    Bio = dataSnapshot.child("bio").getValue().toString();
                                    if (dataSnapshot.child("UserState").hasChildren()){

                                        state = dataSnapshot.child("UserState").getValue(UserState.class);
                                    }else{
                                        state = null;
                                    }
                                }else{

                                }

                                     User user = new User( fullName,  "",  Image,  Bio, UserId,state );
                                userList.add(user);
                               holder.setContent(user);

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                       holder.cardView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                                String state ="Offline";
                               if (userList.get(position).getUserState()!=null){
                                    if (userList.get(position).getUserState().getState().equals("Online")){
                                        state = "Online";
                                    }else {
                                        state = userList.get(position).getUserState().getDate();
                                    }
                               }
                               Log.i("output", "onClick Friend USer Id: "+userList.get(position).getUid());
                                        Intent chat =new Intent(context.getApplicationContext(), Chat.class);
                                       chat.putExtra("userId",userList.get(position).getUid());
                                       chat.putExtra("fullName",userList.get(position).getFullName());
                                       chat.putExtra("lastSeen",state);
                                       chat.putExtra("image",userList.get(position).getImage()==null?"":userList.get(position).getImage());
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

        public void setContent(User user){

            final String image =user.getImage()==null?"":user.getImage();
            if (image.equals("")){
                circleImageView.setImageResource(R.drawable.avatar);
            }else{
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .into(circleImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                                Picasso.get().load(image).placeholder(R.drawable.avatar).into(circleImageView);

                            }


                        });
            }
            if (user.getUserState()==null){
                status.setText("Offline");
                status.setTextColor(itemView.getResources().getColor(R.color.seconderText) );

            }else {
                if(user.getUserState().getState().equals("Online")){
                    status.setText("Online");
                    status.setTextColor(itemView.getResources().getColor(R.color.colorPrimaryDark) );
                }else{
                    status.setTextColor(itemView.getResources().getColor(R.color.seconderText) );
                    status.setText("Last seen "+user.getUserState().getDate());
                }
            }


            if(!user.getBio().equals(null))
                lastSeen.setText("Bio, "+user.getBio());
            if(!user.getFullName().equals(null))
                name.setText(user.getFullName());
        }
    }
}
