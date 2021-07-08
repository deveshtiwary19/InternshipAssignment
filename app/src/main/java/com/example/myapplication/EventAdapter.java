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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Events> list;
    private Context mContext;
    private Activity activity;

    public EventAdapter(List<Events> list, Context mContext, Activity activity) {
        this.list = list;
        this.mContext = mContext;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.itemlayout_events,parent,false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EventAdapter.EventViewHolder holder, int position) {

        Events events=list.get(position);

        //Setting the data from the event toitemView
        Picasso.get().load(events.getImage()).placeholder(R.drawable.placeholder).into(holder.imageView);
        holder.title.setText(events.getTitle());
        holder.rest.setText(events.getDate()+" / "+events.getCity()+" / "+events.getFee());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder
    {
        //Following are the objecyts on the item layout
        ImageView imageView;
        TextView title;
        TextView rest;

        public EventViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            //Assigning the objects to ids on teh item layout
            imageView=itemView.findViewById(R.id.image_eventItem);
            title=itemView.findViewById(R.id.titl_eventItem);
            rest=itemView.findViewById(R.id.rest_eventItem);

        }
    }


}
