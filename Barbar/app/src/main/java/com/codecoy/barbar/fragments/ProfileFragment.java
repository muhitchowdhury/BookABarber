package com.codecoy.barbar.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.codecoy.barbar.R;
import com.codecoy.barbar.Repository.Repository;
import com.codecoy.barbar.adapters.ImagesAdapter;
import com.codecoy.barbar.dataModels.EventModel;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.databinding.FragmentProfileBinding;
import com.codecoy.barbar.listneres.OnUserProfileLoadListeners;
import com.codecoy.barbar.projectUtils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A profile contains: Profile image, Name, Description, address and a list of uploaded images
 */
public class ProfileFragment extends Fragment {

    //Simple tag used to in the log classes
    private static final String TAG = ProfileFragment.class.getSimpleName();


    //Instance of activity binding use to get reference to the views of xml layout  like textview etc

    private FragmentProfileBinding mBinding;
    //instance of progress bar , loading bar
    private KProgressHUD kProgressHUD;

    //String Variable to store User Id
    private String user_id;

    //Variable to store context
    private Context context;


    //Constructor to assign values
    public ProfileFragment(Context context, String user_id) {
        this.user_id = user_id;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        //Create Progress Dialog
        kProgressHUD = Utils.getProgressDialog(context, "Please Wait");
        loadUserData(); //Calling loadUserData method to load data from database

        return mBinding.getRoot();
    }


    //loadUserData method to load data from database
    private void loadUserData()
    {

        //Show progress bar on screen
        kProgressHUD.show();



        //Calling getMyProfile from Repository class to load User Data
        Repository.getMyProfile(user_id, new OnUserProfileLoadListeners()
        {
            @Override
            public void onUserProfileLoaded(SignUpModel userProfile) //this method runs when data is loaded
            {
                //hide progress bar
                kProgressHUD.dismiss();
                SetDataOnViews(userProfile); //show data on screen
            }

            @Override
            public void onFailure(String e)//onFailure runs when there is error while loading data
            {
                //hide progress bar
                kProgressHUD.dismiss();
            }

            @Override
            public void onEmpty(String message) //onEmpty runs when there is no data on the database
            {
                //hide progress bar
                kProgressHUD.dismiss();
            }
        });
    }

    //This method is used to show the data on screen
    private void SetDataOnViews(SignUpModel signUpModel)
    {


        //Loading the image using Glide Library into imageView
        Glide.with(context)
                .load(signUpModel.getUser_image())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image)
                .into(mBinding.barbarImagevew);


        //Showing User Full Name
        mBinding.barbarNameTv.setText(signUpModel.getUser_first_name() + " " + signUpModel.getUser_last_name());

        //Showing User Address
        mBinding.addressTv.setText(signUpModel.getUser_address());

        //Showing User Phone Number And Email
        mBinding.desTv.setText("Phone Number: " + signUpModel.getUser_contact_number() + "\n" + "Email: " + signUpModel.getUser_email());


        //GridLAyout manager for recycler to show images in grid style
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);

        //Setting grid layout on recycler View
        mBinding.imagesRecyclerview.setLayoutManager(gridLayoutManager);
        mBinding.imagesRecyclerview.setHasFixedSize(true);

        //Creating instance og ImagesAdapter to show images on the RecyclerView
        ImagesAdapter adapter = new ImagesAdapter(signUpModel.getImages_list(), context);

        //Setting Adapter On the RecycerView
        mBinding.imagesRecyclerview.setAdapter(adapter);

        //Getting User lattitude and longitude
        double lat = signUpModel.getUser_geopoint().getLatitude();
        double lng = signUpModel.getUser_geopoint().getLongitude();

        mBinding.addressCardview.setOnClickListener(v -> {

            //Formating uri
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);

            //Starting Intent to open maps with lat and long
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
        });
    }
}