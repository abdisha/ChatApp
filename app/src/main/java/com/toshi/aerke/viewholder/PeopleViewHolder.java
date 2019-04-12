package com.toshi.aerke.viewholder;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.toshi.aerke.pigeonfly.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleViewHolder extends RecyclerView.ViewHolder {
    View viewItem;
    CircleImageView imageView;
    TextView Name ,Bio;
    CardView cardView;
    public PeopleViewHolder(View itemView) {
        super(itemView);
        viewItem =itemView;
        imageView =(CircleImageView)viewItem.findViewById(R.id.peopleProfileImage);
        Name = (TextView)viewItem.findViewById(R.id.txtPeopleName);
        Bio=(TextView)viewItem.findViewById(R.id.txtPeopleBio);
        cardView =(CardView)viewItem.findViewById(R.id.peopleCardView);


    }
    public void setImage(String Image){
        final String image =Image;
        if(image ==null || image.equals("")){
            imageView.setImageResource(R.drawable.avatar);
        }else{
            Picasso.get().load(Image).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(image).placeholder(R.drawable.avatar).into(imageView);
                            }

                    });
    }


    }
    public void setNameandBio(String Name,String Bio){
        if(!Name.isEmpty())
            this.Name.setText(Name);
        if(!Bio.isEmpty())
            this.Bio.setText(Bio);

    }

}
