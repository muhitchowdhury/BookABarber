package com.codecoy.barbar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codecoy.barbar.R;
import com.codecoy.barbar.Repository.Repository;
import com.codecoy.barbar.adapters.EventsAdapter;
import com.codecoy.barbar.dataModels.EventModel;
import com.codecoy.barbar.databinding.ActivityBarberDateBinding;
import com.codecoy.barbar.listneres.OnDatesLoadListener;
import com.codecoy.barbar.projectUtils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

/**
 * This activity is loaded when clicking on Calendar Icon from BarberDashboardActivity
 * It contains Dates list
 */
public class BarberDateActivity extends AppCompatActivity {
    //Simple tag used to in the log classes
    private static final String TAG = BarberDateActivity.class.getSimpleName();

    //Instance of activity binding use to get reference to the views of activity_barbar_dashboard.xml like textview etc
    private ActivityBarberDateBinding mBinding;
    //instance of progress bar , loading bar
    private KProgressHUD kProgressHUD;

    //instance to firebase authentication used to get current user details
    private FirebaseAuth mAuth;

    @Override //Oncreate functions run when activity is created
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Binding the data our binding instance to the layout so that we can access its views (TextView , EditText Etc)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_barber_date);


        //Creating firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //Creating instance of progress bar  in utils class we have static method getProgressDialog which returns object of KProgressHUD
        kProgressHUD = Utils.getProgressDialog(this, "Please wait...");

        //Setting SupportActionBar Title And Enabling Back Button (Support action is  like toolbar we see on top of screen  )
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Schedule"); //Setting title on supportAction Bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Enabling Back button
        }


        //when Add Button is Clicked Open AddDateActivity Class
        mBinding.addDateBtn.setOnClickListener(v -> startActivity(new Intent(BarberDateActivity.this, AddDateActivity.class)));
    }


    //Calling loadDates Method to load Event  data from database
    private void loadDates()
    {

        //Show progress bar
        kProgressHUD.show();

        //calling getBarberEvent method in Repository class to load data
        Repository.getBarberEvent(mAuth.getCurrentUser().getUid(), new OnDatesLoadListener() {
            @Override
            public void onDatesLoad(ArrayList<EventModel> list) //onDatesLoad is used to load the list of barberEvent data
            {

                //Hide progress bar
                kProgressHUD.dismiss();

                //Create Adapter For Recycler View
                EventsAdapter adapter = new EventsAdapter(list, BarberDateActivity.this);

                //Create LinearLayoutManager object to be set on recyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(BarberDateActivity.this, RecyclerView.VERTICAL, false);

                //Set LayoutManager as LinearLayoutManager on Recycler View
                mBinding.datesRecyclcerview.setLayoutManager(layoutManager);
                //Set RecyclerView As Fixed Size
                mBinding.datesRecyclcerview.setHasFixedSize(true);
                //Set Adapter On Recycler View
                mBinding.datesRecyclcerview.setAdapter(adapter);
            }

            @Override
            public void onEmpty(String message)  //onEmpty method is used when there is no data in the database
            {
                //Hide progress bar
                kProgressHUD.dismiss();
                //Show Message To user
                Utils.INFO_TOAST(BarberDateActivity.this, "No Data Found");
            }

            @Override
            public void onFailure(String e)  //onFailure method is used when there is some error when loading the data
            {
                //Hide progress bar
                kProgressHUD.dismiss();
                Log.i(TAG, "onFailure: " + e);
            }
        });
    }

    @Override //onOptionsItemSelected runs when user select any menu option action bar / tool bar
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        //if the selected option is back button of toolbar
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); //call on backpress method
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); //calling super onBackPressed to handle backpressed request
    }

    @Override//onResume method runs when activity is resumed
    protected void onResume()
    {
        super.onResume();
        loadDates();//Calling loadDates Method to load Event  data from database
    }

}