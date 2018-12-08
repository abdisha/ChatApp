package com.toshi.aerke.pigeonfly;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.toshi.aerke.model.Message;
import com.toshi.aerke.viewholder.ChatHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {
     Toolbar toolbar;
     TextView fullname,lastseen;
     EditText message;
     RecyclerView recyclerView;
     CircleImageView imageView;
     ImageButton sendButton;
     String image = "empty",userId ,name,_lastseen,CurrentUserId;
     //firebase objects;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ChatHolder chatHolder;
    List<Message> messages;
    private ActionBar actionBar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

            Initialize();


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void Initialize() {
        firebaseAuth = FirebaseAuth.getInstance();
        messages = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();
        recyclerView = (RecyclerView)findViewById(R.id.chatRecyclerView);
        message =(EditText)findViewById(R.id.edtMessage);
        sendButton =(ImageButton)findViewById(R.id.btnSendMessage);
        toolbar = (Toolbar) findViewById(R.id.ChatToolbar);
        setSupportActionBar(toolbar);
        actionBar =getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =layoutInflater.inflate(R.layout.costome_toolbar_layout,null);
        imageView = (CircleImageView)view.findViewById(R.id.profileImageChat);
        fullname =(TextView)view.findViewById(R.id.txtToolbarFullName);
        lastseen =(TextView)view.findViewById(R.id.txtToolbarLastSeen);

       actionBar.setCustomView(view);
       actionBar.setDisplayHomeAsUpEnabled(true);
          sendButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if(!message.getText().toString().isEmpty()&&message.getText().length()>0){
                      sendMessage();
                      Toast.makeText(getApplicationContext(),"message sent",Toast.LENGTH_LONG).show();
                  }

              }

          });
         userId =getIntent().getStringExtra("userId");
         name =getIntent().getStringExtra("fullName");
         _lastseen =getIntent().getStringExtra("lastSeen");
         image = getIntent().getStringExtra("image");

         if(!image.equals(null)){
             Picasso.get().load(image).into(imageView);
             Log.i("output", "Initialize: image"+image);
         }if (!name.equals(null))
             fullname.setText(name);
        Log.i("output", "Initialize:Name "+name);
         if(!_lastseen.equals(null))
             lastseen.setText(_lastseen);


    }

    @Override
    protected void onStart() {
        super.onStart();
        chatHolder = new ChatHolder(messages, userId);

         DatabaseReference chatReference = databaseReference.child("Message").child(CurrentUserId).child(userId);
         chatReference.keepSynced(true);
                 chatReference.addChildEventListener(new ChildEventListener() {
                     @Override
                     public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                         Message message = dataSnapshot.getValue(Message.class);
                                    dataSnapshot.child("message").getValue().toString();
                         messages.add(message);
                         chatHolder.notifyDataSetChanged();

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
         recyclerView.setAdapter(chatHolder);
         recyclerView.scrollToPosition(chatHolder.getItemCount()+1);
    }
    private void sendMessage(){
        Map<String,String> messegaMap = new HashMap<>();
        String SenderRer = CurrentUserId;
        String ReciverRer =userId;
        String messegRef = databaseReference.child("Message").child(CurrentUserId).child(userId).push().getKey();
        messegaMap.put("from",CurrentUserId);
        messegaMap.put("message",message.getText().toString());
        messegaMap.put("seen","false");
        messegaMap.put("type","Text");
        messegaMap.put("time", Calendar.getInstance().getTime().toString());
        Map messeRefer = new HashMap();
        messeRefer.put(SenderRer+"/"+ReciverRer+"/"+messegRef,messegaMap);
        messeRefer.put(ReciverRer+"/"+SenderRer+"/"+messegRef,messegaMap);
        databaseReference.child("Message").updateChildren(messeRefer).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    message.setText("");
                    Toast.makeText(getApplicationContext(),"Messeg sent",Toast.LENGTH_LONG).show();
                }else {
                    Snackbar.make(message.getRootView(),"couldn't send, please check your connection ",Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

}
