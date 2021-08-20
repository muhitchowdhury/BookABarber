package com.codecoy.barbar.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.codecoy.barbar.R;
import com.codecoy.barbar.adapters.ViewPagerAdapter;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.databinding.ActivityCustomerBarberBinding;
import com.codecoy.barbar.projectUtils.SharedPref;
import com.google.gson.Gson;

/**
 * This activity contains two Barber pages:
 * Profile and Calendar
 */
public class CustomerBarbersActivity extends AppCompatActivity {
    private static final String TAG = CustomerBarbersActivity.class.getSimpleName();
    //Simple tag used to in the log classes


    //Instance of activity binding use to get reference to the views of xml layout  like textview etc
    private ActivityCustomerBarberBinding mBinding;

    //SignUpModel object to store user data
    private SignUpModel signUpModel;

    //String variable to store user id
    private String user_id;

    @Override //Oncreate functions run when activity is created
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Binding the data our binding instance to the layout so that we can access its views (TextView , EditText Etc)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_barber);

        //Set toolbar as action bar
        setSupportActionBar(mBinding.toolbar);
        //Set title on toolbar
        mBinding.toolbar.setTitle("Barber");


        //Get Extra Data Bundle passed from the class which called this class
        Bundle bundle = getIntent().getExtras();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Enable back button on support action bar


        //checking if data is passed from previous class or not
        if (bundle != null)
        {
            //Get user_id  from Data and Store In type Variable for later use
            user_id = bundle.getString("user_id");
        }


        //Create instance og Gson Class
        Gson gson = new Gson();

        //Get user data from shared prefernce
        String json = SharedPref.getInstance(CustomerBarbersActivity.this).getUSER();

        //Convert Json data to SignUpModel object
        signUpModel = gson.fromJson(json, SignUpModel.class);

        //Set user name on toolbar
        mBinding.toolbar.setTitle(signUpModel.getUser_first_name() + " " + signUpModel.getUser_last_name());

        //Create ViewPage Adapter Instance
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), user_id, CustomerBarbersActivity.this);

        //Set Adapter on ViewPager
        mBinding.viewPager.setAdapter(viewPagerAdapter);

        //SetUp Tabs With ViewPager
        mBinding.tabs.setupWithViewPager(mBinding.viewPager);

        //Set icon on first tab
        mBinding.tabs.getTabAt(0).setIcon(R.drawable.ic_baseline_account_circle_24);

        //Set icon on second tab
        mBinding.tabs.getTabAt(1).setIcon(R.drawable.ic_baseline_date_range_24);

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
}