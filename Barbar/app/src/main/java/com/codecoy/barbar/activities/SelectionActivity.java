package com.codecoy.barbar.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.codecoy.barbar.R;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.projectUtils.SharedPref;
import com.google.gson.Gson;

/**
 * SelectionActivity is the entry point of the Application.
 * It allows the user to choose if he want to login as a Barber or as a Customer.
 * Then it redirects him to Login Activity
 */
public class SelectionActivity extends AppCompatActivity {

    //Simple tag used to in the log classes
    private static final String TAG = SelectionActivity.class.getSimpleName();

    //Variable to store user type
    private String type = "barber";

    @Override //Oncreate functions run when activity is created
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Binding the data our binding instance to the layout so that we can access its views (TextView , EditText Etc)
        com.codecoy.barbar.databinding.ActivitySelectionBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_selection);

        //Creating Gson object
        Gson gson = new Gson();

        // Getting User Date Stored in  SharedPreference
        String json = SharedPref.getInstance(SelectionActivity.this).getUSER();

        //Converting Json Date to SignUpModel Class
        SignUpModel signUpModel = gson.fromJson(json, SignUpModel.class);


        //Check if user data is empty or not
        if (signUpModel != null)   //if user data is not empty
        {

            //Get user type from user Data
            type = signUpModel.getUser_as();

            //Open LoginActivity Class and pass user type
            Intent intent = new Intent(SelectionActivity.this, LoginActivity.class);
            intent.putExtra("type", type);//pass user type
            startActivity(intent);//Open  LoginActivity
            finish(); //Close this Screen
        }


        //On click Listener For Button
        mBinding.btn.setOnClickListener(v ->
        {

            //Open LoginActivity Class and pass user type
            Intent intent = new Intent(SelectionActivity.this, LoginActivity.class);
            intent.putExtra("type", type);//pass user type
            startActivity(intent);//Open  LoginActivity
            finish(); //Close this Screen
        });

        // Radio Group that contains two types: Barber and Customer
        mBinding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            //Check which switch is enabled
            switch (checkedId)
            {
                //if barbar switch is enabled set type as barber
                case R.id.barbar_radio_btn:
                    type = "barber";
                    break;

                //if Customer  switch is enabled set type as barber
                case R.id.customer_radio_btn:
                    type = "customer";
                    break;
            }
        });
    }
}