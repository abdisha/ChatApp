package com.toshi.aerke.pigeonfly;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.toshi.aerke.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileViewer extends AppCompatActivity {
      String UserID;
      private CircleImageView imageView;
      TextView Bio,Nick,FullName,LastSeen;
      Button sendMessage,sendRequest;
      DatabaseReference databaseReference;
      FirebaseAuth firebaseAuth;
      String fullName,lastSeen,Image;
      boolean haveOption = false;
      ValueEventListener valueEventListener ;
    private String AccountOwnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);
        Bio = (TextView)findViewById(R.id.txtBio);
        Nick=(TextView)findViewById(R.id.txtNickNameProfile);
        FullName =(TextView)findViewById(R.id.txtFullNameProfile);
        LastSeen =(TextView)findViewById(R.id.txtLastSeen);
        sendMessage =(Button)findViewById(R.id.btnSendMesseg);
        sendRequest =(Button)findViewById(R.id.btnSendRequestProfile);
        imageView = (CircleImageView)findViewById(R.id.profileImageViewer);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        UserID = firebaseAuth.getCurrentUser().getUid();
        AccountOwnerId = getIntent().getStringExtra("UserId");

//        if(AccountOwnerId.equals(UserID) ){
//            Log.i("output", "onCreate: senderId match AccountOwnedId");
//            sendMessage.setVisibility(View.INVISIBLE);
//            sendRequest.setVisibility(View.INVISIBLE);
//        }else {
//            sendRequest.setVisibility(View.VISIBLE);
//            sendMessage.setVisibility(View.VISIBLE);
//            Log.i("output", "onCreate: senderId don't match AccountOwnedId");
//
//        }
        databaseReference.child("User").child(AccountOwnerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("image")){
                    Image =dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(Image).placeholder(R.drawable.avatar).into(imageView);

                }
                if(dataSnapshot.child("UserState").exists()){
                    lastSeen = dataSnapshot.child("UserState/date").getValue().toString();
                }
                if(dataSnapshot.hasChild("fullName")){
                    fullName=dataSnapshot.child("fullName").getValue().toString();
                    FullName.setText(fullName);
                    Nick.setText(dataSnapshot.child("nickName").getValue().toString());
                    Bio.setText(dataSnapshot.child("bio").getValue().toString());
                }if(!haveOption){
                    databaseReference.child("Request").child(AccountOwnerId)
                            .child("ReceivedRequest").orderByChild("senderId").equalTo(UserID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){

                                            sendRequest.setEnabled(true);
                                            sendRequest.setText("Accept Request");
                                            haveOption =true;

                                        Log.i("output", "onDataChange: accept Request");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }if(!haveOption){
                    databaseReference.child("Request").child(AccountOwnerId).child("SentRequest")
                            .orderByChild("receiverId").equalTo(UserID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){

                                    sendRequest.setText("Cancel Request");
                                    haveOption =true;
                                Log.i("output", "onDataChange: Receivedaccept");
                                }





                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } if(!haveOption) {
                    databaseReference.child("Friends")
                            .child(AccountOwnerId).orderByChild("UserId").equalTo(UserID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){

                                            sendRequest.setText("UnFriend");
                                            sendRequest.setEnabled(true);
                                            haveOption = true;
                                        Log.i("output", "onDataChange: Friend");

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            }

                 if(!haveOption) {
                    sendRequest.setText("Send Request");
                     Log.i("output", "onDataChange: nothing on send request");


                 }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat = new Intent(profileViewer.this,Chat.class);
                 chat.putExtra("userId",AccountOwnerId);
                 chat.putExtra("fullName",fullName);
                 chat.putExtra("lastSeen",lastSeen);
                 chat.putExtra("image",Image);
                 startActivity(chat);
            }
        });
       sendRequest.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.i("output", "onClick: "+sendRequest.getText().toString());
               if(sendRequest.getText().toString().equals("Send Request")){
                   sendRequest();
                   Log.i("output", "send Request: "+sendRequest.getText().toString());

               }else if(sendRequest.getText().toString().equals("Accept Request")){
                   AcceptRequest();
                   Log.i("output", "Accept Request: "+sendRequest.getText().toString());

               }else if(sendRequest.getText().toString().equals("Cancel Request")){
                   CancelRequest();
                   Log.i("output", "Cancel Request: "+sendRequest.getText().toString());

               }else if(sendRequest.getText().toString().equals("UnFriend")){
                   UnFriend();
               }
           }
       });
    }

    private void UnFriend() {
        final DatabaseReference unFriendReference = databaseReference.child("Friends").child(AccountOwnerId).child("UserId").equalTo(UserID).getRef();
        unFriendReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                        DatabaseReference unFriendReference2 = databaseReference.child("Friends").child(UserID).child("UserId").equalTo(AccountOwnerId).getRef();
                         unFriendReference2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      sendRequest.setText("Send Request");
                                      Snackbar.make(sendMessage.getRootView(),"Know you are n't able to see your  chat",Snackbar.LENGTH_LONG).show();
                                  }
                             }
                         });
                }
            }
        });
    }

    private void AcceptRequest() {
        Map friend = new HashMap();
        String key = databaseReference.child("Friends/"+UserID+"/").push().getKey();
       friend.put(UserID+" / "+key+" / "+"UserId",AccountOwnerId);
       friend.put(AccountOwnerId+" / "+key+" / "+"UserId",UserID);
       //final Map removeRequest = new HashMap();

//       removeRequest.put(UserID+"/"+"ReceivedRequest"+"/"+"senderId",null);
//        removeRequest.put(AccountOwnerId+"/"+"SentRequest"+"/"+"receiverId",null);
        databaseReference.child("Friends").updateChildren(friend)
                .addOnCompleteListener(new OnCompleteListener() {
                                   @Override
                                   public void onComplete(@NonNull Task task) {
                                       if(task.isSuccessful()){
                                          DatabaseReference d1 = databaseReference.child("Request").child(UserID).child("ReceivedRequest/senderId").equalTo(AccountOwnerId).getRef();
                                          d1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if(task.isSuccessful()){
                                                      DatabaseReference d2 = databaseReference.child("Request").child(AccountOwnerId).child("SentRequest/receiverId").equalTo(UserID).getRef();
                                                      d2.removeValue().isSuccessful();
                                                  }
                                              }
                                          });

                                           Snackbar.make(sendMessage.getRootView(),"Congrats !, you got friend",Snackbar.LENGTH_LONG).show();
                                       }
                                   }
                               });
    }

    private void CancelRequest() {
         DatabaseReference d1 =databaseReference.child("Request").child(UserID).child("SentRequest/receiverId").equalTo(AccountOwnerId).getRef();
                 d1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful()){
                     DatabaseReference d2=databaseReference.child("Request").child(AccountOwnerId).child("ReceivedRequest/senderId").equalTo(UserID).getRef();
                             d2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             sendRequest.setText("Send Request");
                             sendRequest.setEnabled(true);
                             Snackbar.make(sendMessage.getRootView(),"Request canceled", Snackbar.LENGTH_LONG).show();
                         }
                     });
                 }
             }
         });
    }

    private  void sendRequest(){

        final Map request = new HashMap();
        String key = databaseReference.child("Request").child(UserID+"/SentRequest").push().getKey();
        request.put(UserID+" / "+"SentRequest"+" / "+key+" / "+"receiverId",AccountOwnerId);
        request.put(AccountOwnerId+" / "+"ReceivedRequest"+" / "+key+" / "+"senderId",UserID);
        databaseReference.child("Request").updateChildren(request).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    sendRequest.setEnabled(false);
                    sendRequest.setText("Request Sent");
                    sendRequest.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    Snackbar.make(sendRequest.getRootView(),"Request sent",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
