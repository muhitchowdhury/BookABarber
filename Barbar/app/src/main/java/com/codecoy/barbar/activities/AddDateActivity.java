package com.codecoy.barbar.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.codecoy.barbar.R;
import com.codecoy.barbar.dataModels.EventModel;
import com.codecoy.barbar.database_controller.DatabaseUploader;
import com.codecoy.barbar.databinding.ActivityAddDateBinding;
import com.codecoy.barbar.listneres.OnTaskListeners;
import com.codecoy.barbar.projectUtils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddDateActivity extends AppCompatActivity
{

    //Simple tag used to in the log classes
    private static final String TAG = AddDateActivity.class.getSimpleName();

    //Instance of activity binding use to get reference to the views of xml layout  like textview etc
    private ActivityAddDateBinding mBinding;

    //instance of progress bar , loading bar
    private KProgressHUD kProgressHUD;

    //instance to firebase authentication used to get current user details
    private FirebaseAuth mAuth;

    //Variable to store selected data
    private String selectedDate;

    //Variable to store Note
    private String note = "";

    @Override //Oncreate functions run when activity is created
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Binding the data our binding instance to the layout so that we can access its views (TextView , EditText Etc)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_date);



        //Setting SupportActionBar Title And Enabling Back Button (Support action is  like toolbar we see on top of screen  )
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Add Date"); //Setting title on supportAction Bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Enabling Back button
        }


        //Creating firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //Creating instance of progress bar  in utils class we have static method getProgressDialog which returns object of KProgressHUD
        kProgressHUD = Utils.getProgressDialog(this, "Please wait");


        //Getting current system time in mili seconds
        long time = System.currentTimeMillis();

        //setting the current time on calender view so that calender view points to current date and time
        mBinding.calanderView.setDate(time, false, true);

        //Getting Current Date
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c); //printing current date on console


        //creating instance of Date Formmater its  and formating the date in this formate "dd/MM/yyyy"
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDate = df.format(c); //formated date stored in variable selectedDate

        ///printing selected_Date on console log
        Log.i(TAG, "onCreate: selected_Date:" + selectedDate);


        //Creating ondate selected cange listener for CalenderView
        // this listener runs when ever user change date on this calender view
        mBinding.calanderView.setOnDateChangeListener((view, year, month, dayOfMonth) ->
        {

            //After date is changed this method runs


            //Get calender instance
            Calendar c1 = Calendar.getInstance();
            //and move the caleneder to select year month and day
            c1.set(year, month, dayOfMonth);

            //creating instance of Date Formmater its  and formating the date in this formate "dd/MM/yyyy"
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            selectedDate = sdf.format(c1.getTime()); //formated date stored in variable selectedDate
            ///printing selected_Date on console log
            Log.i(TAG, "onSelectedDayChange: date:" + selectedDate);

        });


        //Creating Save Button  Click   listener
        // this listener runs when ever user cliks on  Save Button
        mBinding.saveBtn.setOnClickListener(v -> {

            //Get not text from the not textbox and save it in note variable
            note = mBinding.noteInputLayout.getEditText().getText().toString().trim();


            //Checking if the Note TextBox is not empty TextUtils.isEmpty("Some String") return true if the provided string is empty ""
            if (!TextUtils.isEmpty(note))
            {

                //show the progress bar on the screen
                kProgressHUD.show();

                //Create Reandom Event Id
                String event_id = String.valueOf(Calendar.getInstance().getTimeInMillis());

                //Create instance of EventModel by passing event id selected date and not to constructor of EventModel
                EventModel eventModel = new EventModel(event_id, selectedDate, note);


                //Calling static method setUserDate  which is declared in DatabaseUploader class
                // setUserDate method in setUserDate class requires 3 paramaters
                // whih is user id eventmodel object and onTaskListener interface
                DatabaseUploader.setUserDate(mAuth.getCurrentUser().getUid(), eventModel, new OnTaskListeners() {
                    @Override //this runs when task is sucess
                    public void onTaskSuccess() {
                        //Show a success message to user
                        Utils.SUCCESS_TOAST(AddDateActivity.this, "Date saved successfully");
                        kProgressHUD.dismiss(); //hide progress bar
                        finish(); //close current screen
                    }

                    @Override//this runs when task is Fail
                    public void onTaskFail(String e) {
                        kProgressHUD.dismiss(); // hide progress bar
                        //show error message to user
                        Utils.ERROR_TOAST(AddDateActivity.this, "Unable to save");
                    }
                });
            } else {
                //if textbox not is empty show message to user Required
                mBinding.noteInputLayout.setError("Required");
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
}