package com.toshi.aerke.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.squareup.picasso.Picasso;
import com.toshi.aerke.model.Message;
import com.toshi.aerke.pigeonfly.Chat;
import com.toshi.aerke.pigeonfly.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatListViewHolder extends RecyclerView.Adapter<ChatListViewHolder.ChatListView>{
       List<Message> messages ;
    private String image,fullName,lastSeen;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceUser;
    private DatabaseReference databaseReference;
    Context context;
    public ChatListViewHolder(List<Message> messages,Context context) {
        this.messages = messages;
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
        final String userId = firebaseAuth.getCurrentUser().getUid();
        final Message model = messages.get(position);
        Log.i("ouput", "chatlistViewHolder: messageslist this "+messages.size());
        if(messages.size()>0){
            if(userId !=model.getTo()){
                databaseReferenceUser = databaseReference.child("User").child(model.getTo()).getRef();

                databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            image = dataSnapshot.child("image").getValue().toString();
                            fullName =dataSnapshot.child("fullName").getValue().toString();
                            lastSeen =dataSnapshot.child("UserState/state").getValue().toString();

                            holder.setCircleImageView(image);
                            holder.setFullname(fullName);
                            holder.setDate(model.getTime());
                            holder.setMessage(model.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }
               holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat = new Intent(context.getApplicationContext(),Chat.class);
                chat.putExtra("userId",messages.get(position).getTo());
                chat.putExtra("fullName",fullName);
                chat.putExtra("lastSeen",lastSeen);
                chat.putExtra("image",image);
                context.startActivity(chat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ChatListView extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView fullname, message ,date;
        View view;
        public ChatListView(View itemView) {
            super(itemView);
            circleImageView =(CircleImageView)itemView.findViewById(R.id.profileImageChatList);
            message = (TextView)itemView.findViewById(R.id.txtMessageChat);
            date = (TextView)itemView.findViewById(R.id.txtStatusChat);

        }

        public void setCircleImageView(String image) {
            if(!circleImageView.equals(null))
                Picasso.get().load(image).placeholder(R.drawable.avatar).into(circleImageView);
        }

        public void setDate(String date) {
            if(!date.equals(null))
                this.date.setText(date);
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
