package com.toshi.aerke.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toshi.aerke.model.User;
import com.toshi.aerke.pigeonfly.People;
import com.toshi.aerke.pigeonfly.R;
import com.toshi.aerke.pigeonfly.profileViewer;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleViewHolder> {

    List<User> list;
    Context context;

    public PeopleAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.people_layout,parent,false);
        return new PeopleViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PeopleViewHolder holder, final int position) {
        final User model =list.get(position);
        if(model.getFullName()!=null){
            Log.i("output", "onBindViewHolder: Search "+model.getFullName());
             holder.setImage(model.getImage());

            holder.setNameandBio(model.getFullName(),model.getBio());


        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserId = list.get(position).getUid();
                Intent intent = new Intent(context.getApplicationContext(),profileViewer.class);
                intent.putExtra("UserId",UserId);
                context.startActivity(intent);
            }

        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
}
