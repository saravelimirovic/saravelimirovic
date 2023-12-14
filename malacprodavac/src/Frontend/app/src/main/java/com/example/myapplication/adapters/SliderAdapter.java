package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    int[] images;

    public SliderAdapter(int[] images) {
        this.images = images;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(SliderAdapter.Holder viewHolder, int position) {
        viewHolder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    public class Holder extends SliderViewAdapter.ViewHolder {
        ImageView imageView;
        public Holder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.sliderImageView);
        }

    }
}
