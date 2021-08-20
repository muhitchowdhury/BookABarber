package com.codecoy.barbar.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.codecoy.barbar.R;
import com.codecoy.barbar.Repository.Repository;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.database_controller.DatabaseUploader;
import com.codecoy.barbar.databinding.ActivityLoginBinding;
import com.codecoy.barbar.listneres.OnTaskListeners;
import com.codecoy.barbar.listneres.OnUserProfileLoadListeners;
import com.codecoy.barbar.projectUtils.SharedPref;
import com.codecoy.barbar.projectUtils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

/**
 * Let the user login using an Email address and password.
 * The process is based on Firebase Authentication
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    //Simple tag used to in the log classes


    //Instance of activity binding use to get reference to the views of xml layout  like textview etc
    ActivityLoginBinding mBinding;

    //String variables to store email and password
    private String email, password;

    //Instance of progress dialog
    private ProgressDialog pb;
    //Instance of firebase auth used to authenticate user
    FirebaseAuth mAuth;

    //String variable to store user type (Barber,Customer)
    private String type;

    //Instance Of FirebaseAuth State Listener
    FirebaseAuth.AuthStateListener authStateListener;
    //String Variable to store User Token
    private String token;

    @Override //Oncreate functions run when activity is created
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Binding the data our binding instance to the layout so that we can access its views (TextView , EditText Etc)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);



        //Using FirebaseMessaging class to get the token this token will be used to send notification to a particular user
        //Every device has different token
        FirebaseMessaging.getInstance().getToken()
                //Add on complete listener method runs when request is completed
                .addOnCompleteListener(task -> {

                    //If request is fail
                    if (!task.isSuccessful()) {

                        //then print error message on console log
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token and save in variable token for later use
                    token = task.getResult();
                    Log.i(TAG, "onCreate: token:" + token);

                });

        //Get Extra Data Bundle passed from the class which called this class
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) //checking if data is passed from previous class or not
        {
            //Get User Type from Data and Store In type Variable for later use
            type = bundle.getString("type");
        }

        setAuthStateListener(); //Set autState Listener
        Log.i(TAG, "onCreate: ");

        init(); //init methos to intializa Firebase Auth And Progress Bar
        setListeners(); // method to select on press listene for view  (like for buttons etc )
    }


    //setting on click listener for the views
    private void setListeners() {


        //Setting on click listner for signup text view
        mBinding.signupTextview.setOnClickListener(v ->
        {

            //Creating intent to go to SignUp Class
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.putExtra("type", type); //Putting  user type
            intent.putExtra("action", "new"); //Putting User Action
            startActivity(intent); //open Signup activity
            finish(); //Close this activity

        });


        //OnClickListener for login button
        mBinding.loginBtn.setOnClickListener(view ->
        {

            //Get Email from TextBox Email And Save in a variable
            email = mBinding.emailEdittext.getText().toString().trim();
            //Get Password  from TextBox Password  And Save in a variable
            password = mBinding.passwordEdittext.getText().toString().trim();

            if (isValid()) //Calling isValid() method to check if email and password is valid or not
            {

                //Calling signInWithEmailAndPassword method in FirebaseAuth class
                //to create user with email and password
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {

                            //Check if Request is Successfull Or not
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");

                                //Calling updateUserToken method in  DatabaseUploader class  to upload user token on database
                                DatabaseUploader.updateUserToken(token, mAuth.getCurrentUser().getUid(), new OnTaskListeners() {
                                    @Override
                                    public void onTaskSuccess() //If Task Is Successfull
                                    {
                                        //Print Success message on console
                                        Log.i(TAG, "onSuccess: token updated successfully");
                                    }

                                    @Override
                                    public void onTaskFail(String e) //If Task Is Fail
                                    {
                                        //Print Fail message on console
                                        Log.i(TAG, "onFailure: unable to update token");
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    // isValid() method to check if email and password is valid or not
    private boolean isValid()
    {
        Log.i(TAG, "isValid: ");

        boolean result = true; //boolean variable to store to result value

        //Check if Email TextBox is empty or not
        if (TextUtils.isEmpty(email))
        {
            //If Empty show Error on Email EditText
            mBinding.emailEdittext.setError(getResources().getString(R.string.Required));
            result = false; //Set Reuslt false

        } else
            //Check if password TextBox is empty or not
            if (TextUtils.isEmpty(password))
            {
            mBinding.passwordEdittext.setError(getResources().getString(R.string.Required));
            result = false; //Set Reuslt false
        } else
            //Check if Email is valid or not
            if (!Utils.isEmailValid(email))
            {
                //Set error on email textbox
            mBinding.emailEdittext.setError(getString(R.string.invalid_email));
            result = false; //Set Reuslt false
            }
        return result;
    }

    private void init() {
        //Creating firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //Creating instance of progress bar
        pb = new ProgressDialog(this);

        //Showing message on progress bar
        pb.setMessage("Please Wait");
        //showing title on progress bar
        pb.setTitle("Loading");
        //making progress bar not cancelable means user cannot manually cancel it by tapping on white space of screen
        pb.setCancelable(false);
    }


    //Firebase Aut StateListener
    private void setAuthStateListener() {

        authStateListener = firebaseAuth ->
        {
            //check if user is already logged in
            if (firebaseAuth.getCurrentUser() != null)
            {

                //Show progress bar on screen
                ProgressDialog loading = new ProgressDialog(LoginActivity.this);
                loading.setTitle("Loading"); //Set Title Of Progress Bar As "Loading"
                loading.setMessage("Authenticating user login credentials . . ."); //Set Message On ProgressBar
                loading.setCancelable(false); //Dont Allow user to cancel the progress bar
                loading.show(); //Show progress bar

                //Call getMyProfile method from Repository class  to get user profile
                Repository.getMyProfile(mAuth.getCurrentUser().getUid(), new OnUserProfileLoadListeners() {
                    @Override
                    public void onUserProfileLoaded(SignUpModel model) //this method runs when profile is loaded
                    {

                        //Hide progress bar
                        loading.dismiss();

                        Log.i(TAG, "onUserProfileLoaded: type:" + type);
                        Log.i(TAG, "onUserProfileLoaded: user_as:" + model.getUser_as());


                        //If User Type  equals user loged in user type
                        if (type.equals(model.getUser_as()))
                        {

                            //Create instance og Gson Class
                            Gson gson = new Gson();
                            //Convet User Date to json String
                            String json = gson.toJson(model);

                            //Save Data to Shared Pref Class
                            SharedPref.getInstance(LoginActivity.this).setUSER(json);


                            //If User is Customer
                            if (model.getUser_as().equals("customer"))
                            {
                                //Go to UserDashBoard Class
                                startActivity(new Intent(LoginActivity.this, CustomerDashboardActivity.class));
                            }
                            else //If User Is Barbar
                                {
                                    //Go to BarBar DashBoard class
                                startActivity(new Intent(LoginActivity.this, BarberDashboardActivity.class));
                            }
                            finish(); //Close Current Actiity
                        }
                        else
                            {
                                //Signout from Firebase Auth Class
                            mAuth.signOut();
                            //Delete data from SharefPref
                            SharedPref.getInstance(LoginActivity.this).setUSER(null);

                            //Show Error Message To User
                            Utils.ERROR_TOAST(LoginActivity.this, "Not Registered");
                        }
                    }

                    @Override
                    public void onFailure(String e)  //onFailure runs when database thorws some error
                    {
                        //Hide Progress Bar
                        loading.dismiss();
                        //Show Error Message
                        Utils.ERROR_TOAST(LoginActivity.this, e);
                    }

                    @Override
                    public void onEmpty(String message)//onEmpty() method runs when there is  no data on database
                    {
                        //Hide Progress Bar
                        loading.dismiss();
                        //Show Error Message
                        Utils.ERROR_TOAST(LoginActivity.this, "Not Registered");
//                            startActivity(new Intent(Login.this, SignUp.class));
                    }
                });
            }
        };
    }

    @Override //onStart runs when activity is started
    protected void onStart()
    {
        super.onStart();
        //Add Auth Listener
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override//onStop runs when activity is Stoped
    protected void onStop() {
        super.onStop();
        //Remove Auth Listener
        mAuth.removeAuthStateListener(authStateListener);
    }
}