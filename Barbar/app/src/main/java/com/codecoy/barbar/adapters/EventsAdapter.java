package com.codecoy.barbar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codecoy.barbar.R;
import com.codecoy.barbar.dataModels.EventModel;
import com.codecoy.barbar.databinding.EventSingleItemBinding;

import java.util.ArrayList;

//Custom Adapter Class to diplay events list
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {
    //initializing variables
    private ArrayList<EventModel> list;
    private Context context;
    //Constructor to pass values in this adapter from activity where its object is created
    public EventsAdapter(ArrayList<EventModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout(event_single_item) for items in list
        EventSingleItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.event_single_item
                , parent, false);
        return new EventsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        //set note in list from arraylist
        holder.itemBinding.noteTv.setText(list.get(position).getNote());
        //set date in list from arraylist
        holder.itemBinding.dateTv.setText(list.get(position).getDate());
    }
    //passing item count in list is same as size of arraylist
    @Override
    public int getItemCount() {
        return list.size();
    }


    //View Holder for recyclerView it is used to bind views like TextView buttons etc to the layout
    //we are inflating  onCreateViewHolder
    static class EventsViewHolder extends RecyclerView.ViewHolder {
        EventSingleItemBinding itemBinding;

        public EventsViewHolder(@NonNull EventSingleItemBinding itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;
        }
    }
}
