package com.toshi.aerke.viewholder;

import android.icu.lang.UScript;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.toshi.aerke.model.Message;
import com.toshi.aerke.pigeonfly.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatHolder extends RecyclerView.Adapter<ChatHolder.singleChatHolder> {

    FirebaseAuth firebaseaAuth;
    String UserId;
    DatabaseReference databaseReference;
    String ReceiverId;
    List<Message> messages;

    public ChatHolder(List<Message> messages,String ReceiverId) {
        this.messages = messages;
        this.ReceiverId =ReceiverId;
        firebaseaAuth =FirebaseAuth.getInstance();
        UserId =firebaseaAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public singleChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.keepSynced(true);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_room_layout,parent,false);
        view.findViewById(R.id.txtSentMessageSeen).setVisibility(View.INVISIBLE);
        return new singleChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final singleChatHolder holder, int position) {


           Message message = messages.get(position);

             if(message.getFrom().equals(UserId)){
                 holder.setSendMessage(message.getMessage(),message.getTime(),message.getSeen());
           }else{

                 holder.setReceivedMessage(message.getMessage(),message.getTime(),message.getMID());

           }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class singleChatHolder extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        TextView receivedMessage,sendMessage,seen,textClockReciever, textClockSender;


        private String image;

        public singleChatHolder(View itemView) {
            super(itemView);
            imageView =(CircleImageView)itemView.findViewById(R.id.chatProfileImage);
            imageView.setVisibility(View.INVISIBLE);
            receivedMessage =(TextView)itemView.findViewById(R.id.txtReceivedMessage);
            receivedMessage.setVisibility(View.INVISIBLE);
            sendMessage=(TextView)itemView.findViewById(R.id.txtsendMessage);
            sendMessage.setVisibility(View.INVISIBLE);
            textClockSender = (TextView)itemView.findViewById(R.id.txtSentMessageTime);
            textClockSender.setVisibility(View.INVISIBLE);
            textClockReciever = (TextView)itemView.findViewById(R.id.txtReceivedMessageTime);
            textClockReciever.setVisibility(View.INVISIBLE);
            seen =(TextView)itemView.findViewById(R.id.txtSentMessageSeen);
            setImageView();
        }

                public void setImageView(){
                    databaseReference.child(ReceiverId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("image"))
                              image =dataSnapshot.child("image").getValue() == null ? "": dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(dataSnapshot.child("image").getValue().toString()).
                                        networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        if (image.equals("")){
                                            imageView.setImageResource(R.drawable.avatar);
                                        }else{
                                            Picasso.get().load(image).placeholder(R.drawable.avatar).into(imageView);

                                        }
                                    }
                                });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
              public  void setReceivedMessage(String Message,String Time,String Mid){
                  sendMessage.setVisibility(View.INVISIBLE);
                  receivedMessage.setVisibility(View.VISIBLE);
                  imageView.setVisibility(View.VISIBLE);
                  receivedMessage.setText(Message);
                  textClockReciever.setVisibility(View.VISIBLE);
                  textClockSender.setVisibility(View.INVISIBLE);
                  seen.setVisibility(View.INVISIBLE);
                    textClockReciever.setText(Time);
                    updateMessageViewStatues(Mid);
              }

        private void updateMessageViewStatues(String Mid) {

        }

        public void setSendMessage(String Message,String Time,String Seen){
                    sendMessage.setVisibility(View.VISIBLE);
                    sendMessage.setText(Message);
                    textClockReciever.setVisibility(View.INVISIBLE);
                    textClockSender.setVisibility(View.VISIBLE);
                  seen.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    textClockSender.setText(Time);
                    receivedMessage.setVisibility(View.INVISIBLE);
                    if (Seen.equals("true")){
                        seen.setVisibility(View.VISIBLE);

                    }
              }
    }


}
