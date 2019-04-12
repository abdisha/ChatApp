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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.toshi.aerke.model.Message;
import com.toshi.aerke.model.User;
import com.toshi.aerke.pigeonfly.Chat;
import com.toshi.aerke.pigeonfly.R;

import org.xml.sax.Parser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatListViewHolder extends RecyclerView.Adapter<ChatListViewHolder.ChatListView>{

       List <User> userList;
    private String fullName,lastSeen,status;
    private String image ="";
    private FirebaseAuth firebaseAuth;
    private Query userFetching = null,FreindMessage = null;
    private DatabaseReference databaseReferenceUser;
    private DatabaseReference databaseReference;
    Context context;
    private User users;

    public ChatListViewHolder(List<User> Userlist,Context context) {
        this.userList = Userlist;
        this.context = context;

    }

    @NonNull
    @Override
    public ChatListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_layout,null);
        firebaseAuth =FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        return new ChatListView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListView holder, final int position) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        User Friend = userList.get(position);

        if(Friend.getUid()!=null){
            Log.i("output", "chatlistViewHolder: UserID  "+Friend.getUid());
              FreindMessage= databaseReference.child("Message").child(userId).child(Friend.getUid()).orderByChild("time").limitToFirst(1);
               // Log.i("output", "onBindViewHolder: "+FreindMessage+" /"+Friend.getUid());
                 FreindMessage.keepSynced(true);

            if( FreindMessage!=null){

                image= Friend.getImage() == null ?"": Friend.getImage();
                fullName =Friend.getFullName();
                lastSeen = Friend.getUserState().getDate()==null?"":Friend.getUserState().getDate();

                status = Friend.getUserState().getState()==null?"Offline":Friend.getUserState().getState();
                holder.setCircleImageView(image);
                holder.setFullname(fullName);
                holder.setDate(lastSeen, status);
                FreindMessage.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                holder.setMessage(snapshot.child("message").getValue().toString());
                                holder.setDate(snapshot.child("time").getValue().toString(),status);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }
               holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ls = "Offline"; ;
                    if (userList.get(position).getUserState().getState()!=null)
                      ls = userList.get(position).getUserState().getState().equals("Online")?"Online":userList.get(position).getUserState().getDate();

                Log.i("output", "userstatus: "+userList.get(position).getUserState().getState());

                Intent chat = new Intent(context.getApplicationContext(),Chat.class);
                chat.putExtra("userId",userList.get(position).getUid());
                chat.putExtra("fullName",userList.get(position).getFullName());
                chat.putExtra("lastSeen",ls);
                chat.putExtra("image",userList.get(position).getImage() == null
                ? "": userList.get(position).getImage());
                context.startActivity(chat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ChatListView extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView fullname, message ,date;
        CardView cardView;
        View view;
        public ChatListView(View itemView) {
            super(itemView);
            cardView =(CardView)itemView.findViewById(R.id.cardViewChat);
            circleImageView =(CircleImageView)itemView.findViewById(R.id.profileImageChatList);
            message = (TextView)itemView.findViewById(R.id.txtMessageChat);
            date = (TextView)itemView.findViewById(R.id.txtStatusChat);
            fullname = (TextView)itemView.findViewById(R.id.txtFullNameChat);

        }

        public void setCircleImageView(final String image) {
            final String Image =image;
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

                                    Picasso.get().load(Image).placeholder(R.drawable.avatar).into(circleImageView);

                                }


                        });
        }
        }

        public void setDate(String date,String online) {
            if(!(date.equals("")) || online.equals("Online") ){
                this.date.setTextColor(itemView.getResources().getColor(R.color.colorPrimaryDark));
                this.date.setText("Online");
              }else if(!(date.equals(""))){
                this.date.setText("Last active "+date);
                this.date.setTextColor(itemView.getResources().getColor(R.color.seconderText));
            }else{
                this.date.setTextColor(itemView.getResources().getColor(R.color.seconderText));
                this.date.setText("Offline");

            }
        }

        public void setFullname(String fullname) {
            if(!fullname.equals(null))
                this.fullname.setText(fullname);
        }

        public void setMessage(String message) {
            if(!message.equals(null))
                this.message.setText(message);
        }
    }

}
