package com.codecoy.barbar.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codecoy.barbar.R;
import com.codecoy.barbar.projectUtils.Utils;

import java.util.ArrayList;
//Custom Adapter class to display list of selected images
public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.SelectedImagesViewHolder> {
    //initializing variables
    private ArrayList<String> images_list;
    private Context context;
    private DeleteImageCallBack mDeleteImageCallBack;
    //Constructor to pass values in this class from where its objects in created
    public SelectedImagesAdapter(ArrayList<String> images_list, Context context) {
        this.images_list = images_list;
        this.context = context;

        Log.i("TAG", "SelectedImages_Adapter: " + images_list.size());

        try {
            mDeleteImageCallBack = (DeleteImageCallBack) context;
        } catch (Exception e) {
            Utils.ERROR_TOAST(context, "Please implement interface in activity:" + e.getLocalizedMessage());
        }
    }

    public interface DeleteImageCallBack {
        void onDeleteCallBack(int pos);
    }

    @NonNull
    @Override
    public SelectedImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //adding layout(image_single_item) for each items in recyclerview
        View view = LayoutInflater.from(context).inflate(R.layout.images_single_item, parent, false);
        return new SelectedImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImagesViewHolder holder, final int position) {
        //Load images from list to imageview in each item of recyclerview
        Glide.with(context)
                .load(images_list.get(position))
                .centerCrop()
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image)
                .into(holder.selectedimage_imageview);
        //setting visibilty of removeimage_imageview as gone
        holder.removeImage_imageview.setVisibility(View.GONE);
        //on removeimage_imageview is clicked
        holder.removeImage_imageview.setOnClickListener(v -> mDeleteImageCallBack.onDeleteCallBack(position));
    }
    //setting recyclerview items count equals to size of arraylist(list)
    @Override
    public int getItemCount() {
        return images_list.size();
    }


    //View Holder for recyclerView it is used to bind views like TextView buttons etc to the layout
    //we are inflating  onCreateViewHolder
    static class SelectedImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView selectedimage_imageview, removeImage_imageview;

        public SelectedImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            //delcaring ids to imageview in layout of item to be used
            selectedimage_imageview = itemView.findViewById(R.id.selectedimage_imageview);
            removeImage_imageview = itemView.findViewById(R.id.removeImage_imageview);
        }
    }
}
