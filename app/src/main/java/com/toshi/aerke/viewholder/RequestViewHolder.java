package com.toshi.aerke.viewholder;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.toshi.aerke.model.Request;
import com.toshi.aerke.model.User;
import com.toshi.aerke.pigeonfly.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestViewHolder extends RecyclerView.Adapter<RequestViewHolder.RequestView>{

    List<String>UserId;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    public RequestViewHolder(List<String> userId) {
        UserId = userId;
    }

    @NonNull
    @Override
    public RequestView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.request_received_layout,null);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
         firebaseAuth =FirebaseAuth.getInstance();
        return new RequestView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestView holder, final int position) {
                       String userId = UserId.get(position);
                       if(!userId.equals(null)){
                           databaseReference.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if(dataSnapshot.exists()){
                                       holder.setCircleImageView(dataSnapshot.child("image").getValue().toString());
                                       holder.setDate(dataSnapshot.child("UserState/state").getValue().toString());
                                       holder.setFullname(dataSnapshot.child("fullName").getValue().toString());

                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });
                       }
                       final String _userId =firebaseAuth.getCurrentUser().getUid();

                       holder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               final String senderUserId = UserId.get(position);
                               if (!(_userId.equals(null)) && !(senderUserId.equals(null))) {
                                   String key = databaseReference.child("Friends").child(_userId+"/"+"UserId").push().getKey();
                                   Map friend = new HashMap();
                                   friend.put(_userId + " / " +key+ " / "+"UserId", senderUserId);
                                   friend.put(senderUserId + " / "+key + " / "+"UserId", _userId);
//                                   final Map removeRequest = new HashMap();
//
//                                   removeRequest.put(_userId + "/" + "ReceivedRequest" + "/" + "senderId", null);
//                                   removeRequest.put(senderUserId + "/" + "SentRequest" + "/" + "receiverId", null);
                                   databaseReference.child("Friends").updateChildren(friend)
                                           .addOnCompleteListener(new OnCompleteListener() {
                                               @Override
                                               public void onComplete(@NonNull Task task) {
                                                   if (task.isSuccessful()) {
                                                       DatabaseReference d1 = databaseReference.child("Request").child(_userId).child("ReceivedRequest/senderId").equalTo(senderUserId).getRef();
                                                       d1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {
                                                               if(task.isSuccessful()){
                                                                   DatabaseReference d2 = databaseReference.child("Request").child(_userId).child("SentRequest/receiverId").equalTo(senderUserId).getRef();
                                                                   d2.removeValue().isSuccessful();
                                                               }
                                                           }
                                                       });
                                                       Snackbar.make(holder.itemView.getRootView(), "Congrats !, you got friend", Snackbar.LENGTH_LONG).show();
                                                   }
                                               }
                                           });
                               }
                           }
                       });

                       holder.cancelReqeust.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               final String senderUserId = UserId.get(position);
                               if (!(_userId.equals(null)) && !(senderUserId.equals(null))) {
                                   DatabaseReference d1 =databaseReference.child("Request").child(_userId).child("SentRequest/receiverId").equalTo(senderUserId).getRef();
                                   d1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful()){
                                               DatabaseReference d2=databaseReference.child("Request").child(senderUserId).child("ReceivedRequest/senderId").equalTo(_userId).getRef();
                                               d2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {

                                                       Snackbar.make(holder.itemView.getRootView(),"Request canceled", Snackbar.LENGTH_LONG).show();
                                                   }
                                               });
                                           }
                                       }
                                   });
                               }
                           }

                       });


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class RequestView extends RecyclerView.ViewHolder {
    CircleImageView circleImageView;
    TextView fullname, lastseen ;
    Button acceptRequest,cancelReqeust;

        public RequestView(View itemView) {
            super(itemView);
            circleImageView =(CircleImageView)itemView.findViewById(R.id.profileImageRequest);
            fullname = (TextView)itemView.findViewById(R.id.txtFullNameRequest);
            lastseen =(TextView)itemView.findViewById(R.id.txtLastSeenRequest);
            acceptRequest =(Button)itemView.findViewById(R.id.btnAcceptRequest);
            cancelReqeust =(Button)itemView.findViewById(R.id.btnCancelRequest);
        }

        public void setCircleImageView(String image) {
            if(!circleImageView.equals(null))
                Picasso.get().load(image).placeholder(R.drawable.avatar).into(circleImageView);
        }

        public void setDate(String date) {
            if(!date.equals(null))
                this.lastseen.setText(date);
        }

        public void setFullname(String fullname) {
            if(!fullname.equals(null))
                this.fullname.setText(fullname);
        }
    }
}
