package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List<Place> list;
    private Context mContext;
    private Activity activity;

    public PlaceAdapter(List<Place> list, Context mContext, Activity activity) {
        this.list = list;
        this.mContext = mContext;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.itemlayout_place,parent,false);

        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PlaceAdapter.PlaceViewHolder holder, int position) {

        Place place=list.get(position);

        //Setting up the data
        Picasso.get().load(place.getImage()).placeholder(R.drawable.placeholder).into(holder.imageView);
        holder.name.setText(place.getCity());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView name;

        public PlaceViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            //Assigning objects to tej ids
            imageView=itemView.findViewById(R.id.image_cityItem);
            name=itemView.findViewById(R.id.name_cityItem);

        }
    }
}
