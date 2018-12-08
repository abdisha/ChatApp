package com.toshi.aerke.Utilitis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toshi.aerke.pigeonfly.R;

import de.hdodenhof.circleimageview.CircleImageView;
public class PageSlider extends PagerAdapter {
    TextView title;
    CircleImageView circleImageView;
    Context context;
    String [] concept = {"Lovely","Fast and Quick","Reliable","Photo"};
    int [] drawable ={R.drawable.slider3,R.drawable.image_dove,R.drawable.slider,R.drawable.ic_add_a_photo_black_24dp};
    @Override
    public int getCount() {
        return drawable.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(RelativeLayout)object;
    }

    public PageSlider(Context context) {
        super();
        this.context =context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =inflater.inflate(R.layout.slider_layout,container,false);
        circleImageView =(CircleImageView)view.findViewById(R.id.image_splash);
        title =(TextView)view.findViewById(R.id.txtConcept);
        circleImageView.setImageResource(drawable[position]);
        title.setText(concept[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
}
