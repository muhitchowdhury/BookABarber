package com.codecoy.barbar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.codecoy.barbar.R;
import com.codecoy.barbar.Repository.Repository;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.databinding.ActivityCustomerDashboardBinding;
import com.codecoy.barbar.listneres.OnUserProfileLoadListeners;
import com.codecoy.barbar.projectUtils.SharedPref;
import com.codecoy.barbar.projectUtils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Three buttons are included in this page:
 * Logout, a button to redirect to profile and a button to load Barber List
 */
public class CustomerDashboardActivity extends AppCompatActivity {

    private static final String TAG = CustomerDashboardActivity.class.getSimpleName();
    //Simple tag used to in the log classes


    //Instance of activity binding use to get reference to the views of xml layout  like textview etc
    private ActivityCustomerDashboardBinding mBinding;

    private SignUpModel signUpModel;
    //instance of progress bar , loading bar
    private KProgressHUD kProgressHUD;

    //instance to firebase authentication used to get current user details
    private FirebaseAuth mAuth;

    @Override //Oncreate functions run when activity is created
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Binding the data our binding instance to the layout so that we can access its views (TextView , EditText Etc)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_dashboard);

        //Creating firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //Creating instance of progress bar  in utils class we have static method getProgressDialog which returns object of KProgressHUD
        kProgressHUD = Utils.getProgressDialog(this, "Please wait...");


        setListeners(); // method to select on press listene for view  (like for buttons etc )
    }


    // method to select on press listene for view  (like for buttons etc )
    private void setListeners()
    {


        //Barber Button Click Listener
        mBinding.barberBtn.setOnClickListener(v -> {

            //Create Intent to go to BarberListActivity class
            Intent intent = new Intent(CustomerDashboardActivity.this, BarberListActivity.class);
            startActivity(intent);    //Open Barber List Class
        });


        //LogOut  Button Click Listener
        mBinding.logoutTv.setOnClickListener(v -> {
            mAuth.signOut(); //Signout from firebase auth
            //Delte data from SharefPreference
            SharedPref.getInstance(CustomerDashboardActivity.this).setUSER(null);

            //Create Intent to go to SelectionActivity class
            startActivity(new Intent(CustomerDashboardActivity.this, SelectionActivity.class));
            finish();//Close this screen
        });


        //Edit Button click Listener
        mBinding.editTv.setOnClickListener(v -> {


            //Create Intent to go to v class
            Intent intent = new Intent(CustomerDashboardActivity.this, SignUpActivity.class);
            intent.putExtra("type", signUpModel.getUser_as()); //Put User Type
            intent.putExtra("action", "update"); //Put User Action
            startActivity(intent); //Open Sign Up  Class
        });
    }



    //Method to load user data from database
    private void getUserProfile()
    {
        //Show progress bar on screen
        kProgressHUD.show();

        //calling getMyProfile from Repository class to load user data
        Repository.getMyProfile(mAuth.getCurrentUser().getUid(), new OnUserProfileLoadListeners() {
            @Override
            public void onUserProfileLoaded(SignUpModel userProfile)  //onUserProfileLoaded runs when data is loaded
            {

                //Hide Progress Dialog
                kProgressHUD.dismiss();

                //Create instance og Gson Class
                Gson gson = new Gson();
                //Convet User Date to json String
                String json = gson.toJson(userProfile);
                //Save Data to Shared Pref Class
                SharedPref.getInstance(CustomerDashboardActivity.this).setUSER(json);

                //Save user data in signUpModel Variable
                signUpModel = userProfile;

                //Load user Image With Glide Library to imageView
                Glide.with(CustomerDashboardActivity.this).load(userProfile.getUser_image()).placeholder(R.drawable.loading_image)
                        .error(R.drawable.no_image).into(mBinding.barberImageview);

                //Show user name on Screen
                mBinding.barberNameTv.setText(userProfile.getUser_first_name() + " " + userProfile.getUser_last_name());
            }

            @Override
            public void onFailure(String e) //onFailure runs when database thorws some error
            {
                //Hide Progress Bar
                kProgressHUD.dismiss();

            }

            @Override
            public void onEmpty(String message)//onEmpty() method runs when there is  no data on database
            {
                //Hide Progress Bar
                kProgressHUD.dismiss();
                Log.i(TAG, "onEmpty: " + message);
            }
        });
    }

    @Override//onResume method runs when activity is resumed
    protected void onResume()
    {
        super.onResume();
        getUserProfile();//Calling getUserProfile Method to load user data from database
    }
}