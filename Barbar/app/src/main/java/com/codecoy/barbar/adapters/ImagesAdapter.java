package com.codecoy.barbar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codecoy.barbar.R;
import com.codecoy.barbar.databinding.ImagesSingleItemBinding;

import java.util.ArrayList;


//Custom Adapter Class to show list of images
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    //initializing variables
    private ArrayList<String> list;
    private Context context;
    //Constructor to pass values from activity where its objects in created
    public ImagesAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating layout(images_single_item) for items in list
        ImagesSingleItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.images_single_item
                , parent, false);
        return new ImagesViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        //loading imgaes from arraylist and display in recyclerview
        Glide.with(context).load(list.get(position))
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image).into(holder.itemBinding.selectedimageImageview);
        //setting visibility gone in Imageview(removeimageview)
        holder.itemBinding.removeImageImageview.setVisibility(View.GONE);
    }
    //setting recyclerview item count as size of arraylist(list)
    @Override
    public int getItemCount() {
        return list.size();
    }


    //View Holder for recyclerView it is used to bind views like TextView buttons etc to the layout
    //we are inflating  onCreateViewHolder
    static class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImagesSingleItemBinding itemBinding;

        public ImagesViewHolder(@NonNull ImagesSingleItemBinding itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;
        }
    }
}
