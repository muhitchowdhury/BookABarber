package com.codecoy.barbar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codecoy.barbar.R;
import com.codecoy.barbar.activities.CustomerBarbersActivity;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.databinding.BarberListSingleItemBinding;

import java.util.ArrayList;

//Custom Adapter for Recyclerview to display baber list
public class BarberListAdapter extends RecyclerView.Adapter<BarberListAdapter.BarberListViewHolder> {
    //initializing variables
    private Context context;
    private ArrayList<SignUpModel> list;
    private ArrayList<SignUpModel> tempList;
    //constructor to pass the list when the adapter object is made
    public BarberListAdapter(Context context, ArrayList<SignUpModel> list) {
        //assigning list of main activity to this activity
        this.context = context;
        this.list = list;
        tempList = list;
    }

    @NonNull
    @Override
    public BarberListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the item layout in this adapter
        BarberListSingleItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.barber_list_single_item
                , parent, false);
        return new BarberListViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberListViewHolder holder, int position) {
        //(Glide is used to load the image in imageview)
        //loading images in list to baber image view
        Glide.with(context)
                .load(list.get(position).getUser_image())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image)
                .into(holder.itemBinding.barberImageview);
        //displaying barber name from list
        holder.itemBinding.barberNameTv.setText(list.get(position).getUser_first_name() + " " + list.get(position).getUser_last_name());
        //when item is selected in list then it is called
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomerBarbersActivity.class);
            intent.putExtra("user_id", list.get(position).getUser_id());
            context.startActivity(intent);
        });
    }
    //pass number of items in list as size of the list
    @Override
    public int getItemCount() {
        return list.size();
    }
    //filter the result that is search and display in list
    public void setFilter(String query)
    {
        String charSequenceString = query;
        list = new ArrayList<>();
        if (query.equals("")) {
            list = tempList;
        } else {
            for (SignUpModel model : tempList) {
                if (model.getUser_first_name().toLowerCase().contains(charSequenceString.toLowerCase())) {
                    list.add(model);
                }
            }
        }
        //notify that their is change in list so load again
        notifyDataSetChanged();
    }


    //View Holder for recyclerView it is used to bind views like TextView buttons etc to the layout
    //we are inflating  onCreateViewHolder
    static class BarberListViewHolder extends RecyclerView.ViewHolder {
        BarberListSingleItemBinding itemBinding;

        public BarberListViewHolder(@NonNull BarberListSingleItemBinding itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;
        }
    }
}
