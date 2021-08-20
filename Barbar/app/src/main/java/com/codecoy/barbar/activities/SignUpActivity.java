package com.codecoy.barbar.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.codecoy.barbar.R;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.database_controller.DatabaseUploader;
import com.codecoy.barbar.databinding.ActivitySignUpBinding;
import com.codecoy.barbar.firestorage_controller.FireStorageAddresses;
import com.codecoy.barbar.firestorage_controller.FireStoreUploader;
import com.codecoy.barbar.listneres.OnFileUploadListeners;
import com.codecoy.barbar.listneres.OnTaskListeners;
import com.codecoy.barbar.projectUtils.SharedPref;
import com.codecoy.barbar.projectUtils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Essentially this activity is for creating a new account for Customer and Barber
 * It's also used to Update an already existing account
 */
public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    //Simple tag used to in the log classes


    //Instance of activity binding use to get reference to the views of xml layout  like textview etc
    private ActivitySignUpBinding mBinding;

    //Variable to store Seller Address
    private String seller_address;
    //Object of geopoint use to get address from lat and long
    private GeoPoint geoPoint;
    //FusedLocationProviderClient used to get current location of user
    FusedLocationProviderClient mFusedLocationClient;

    //lat lan to store latitude and longnitude of current user location
    private Double lat = 0.0;
    private Double lan = 0.0;

    //Instance firebase firestore database
    private FirebaseFirestore db;

    //instance of progress bar , loading bar
    private KProgressHUD dialog;

    //Uri to store image uri of user
    private Uri pickedUri = null;

    //Variable to store first names last name email etc
    private String firstName, lastName, email, mobileno, password;

    //Variable to store device token later used for firebase messaging
    private String device_token_new;

    private String type; // as barber or customer
    private String action; // new or update

    //SignUpModel object to store user information if he is going to update his account
    private SignUpModel signUpModel; // if update

    @Override //Oncreate functions run when activity is created
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Binding the data our binding instance to the layout so that we can access its views (TextView , EditText Etc)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        //Getting instace of Fused location which tells us about the user current location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Using FirebaseMessaging class to get the token this token will be used to send notification to a particular user
        //Every device has different token
        FirebaseMessaging.getInstance().getToken()
                //Add on complete listener method runs when request is completed
                .addOnCompleteListener(task ->
                {

                    //If request is fail
                    if (!task.isSuccessful())
                    {
                        //then print error message on console log
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token and save in variable token for later use
                    device_token_new = task.getResult();
                    Log.i(TAG, "onCreate: token:" + device_token_new);

                });


        //Get Extra Data Bundle passed from the class which called this class
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)   //checking if data is passed from previous class or not
        {
            //Get User Type from Data and Store In type Variable for later use
            type = bundle.getString("type");

            //Get Action type (New Account Or Update Profile )and store in action variable
            action = bundle.getString("action");
        }

        init(); //init methos to intializa Firebase Auth And Progress Bar

        checkLocationPremissions(); //Method to check location permission is givern by user or not

        setListeners(); // method to select on press listene for view  (like for buttons etc )


        //Check if Signup class is opened to create new account or update exsisting account
        // if opened for update
        if (action.equals("update"))
        {

            //Create json object
            Gson gson = new Gson();
            //get json data of user store in SharedPref
            String json = SharedPref.getInstance(this).getUSER();

            //convert json data to SignUpModel object
            signUpModel = gson.fromJson(json, SignUpModel.class);

            //call setDataOnViews method to show data on the views
            setDataOnViews();

        }
    }

    private void setDataOnViews()
    {

        //Set Result Text view Text As Profile
        mBinding.resulttv.setText("Profile");

        //Loat the image using Libaray named as Glide
        Glide.with(this)
                .load(signUpModel.getUser_image()) // Load image from the link
                .placeholder(R.drawable.loading_image) //set loading place holder
                .error(R.drawable.no_image) //When error comes while loading show error image
                .into(mBinding.profilePicture);// load the image in profilePicture ImageView


        //Enable the Email TextBox
        mBinding.entereEmail.setEnabled(false);

        //Enable the Password TextBox
        mBinding.enterePassword.setEnabled(false);

        //get first name from the signUpModel mode and show on  first name on textView
        mBinding.enterFirstName.setText(signUpModel.getUser_first_name());
        //get Last  name from the signUpModel mode and show on  enterLastName  on textView
        mBinding.enterLastName.setText(signUpModel.getUser_last_name());
        //get Email  from the signUpModel mode and show on  entereEmail on textView
        mBinding.entereEmail.setText(signUpModel.getUser_email());
        //get Password  from the signUpModel mode and show on  enterePassword on textView
        mBinding.enterePassword.setText(signUpModel.getUser_password());
        //get Phone  from the signUpModel mode and show on  enterPhone on textView
        mBinding.enterPhone.setText(signUpModel.getUser_contact_number());
        //get County Name  from the signUpModel mode and show on  enterCountry on textView
        mBinding.enterCountry.setText(signUpModel.getUser_address());
    }


    //Method to intializa some objects
    private void init()
    {

        //make address layout not focsable
        mBinding.enterAddresslayout.getEditText().setFocusable(false);

        //get refernece to firesstore database
        db = FirebaseFirestore.getInstance();
        //Creating instance of progress bar  in utils class we have static method getProgressDialog which returns object of KProgressHUD
        dialog = Utils.getProgressDialog(this, "Please Wait");
    }

    private void setListeners()  //Method to set listners for views
    {


        //setting on click listenet for image which has icon current location
        mBinding.currentLocationImageview.setOnClickListener(view -> {

            //Get last location of user
            getLastLocation();

            //if user lat and long is not empty 0.0 means lat and long is not loaded yet
            if (lat != 0.0 && lan != 0.0) {

                // create alerdialod to show a pop message
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                //set titel on dialog box
                builder.setTitle("GPS Location");

                //set message on dialog box
                builder.setMessage(lat + " Latitude" + "\n" + lan + " Longitude");

                //set cancelable false
                builder.setCancelable(false);

                //add a button on dialog box
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                //show dialog box to screen
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                Log.i(TAG, "onOptionsItemSelected: Null location");
            }
        });


        //Click listener for signup button
        mBinding.Signupaccount.setOnClickListener(view ->
        {

            //Get firstname from the TextBox  enterFirstNameLayout and store in  variable
            firstName = mBinding.enterFirstNameLayout.getEditText().getText().toString().trim();
            //Get lastName from the TextBox  enterLastNameLayout and store in  variable
            lastName = mBinding.enterLastNameLayout.getEditText().getText().toString().trim();
            //Get email from the TextBox  enterEmaillayout and store in  variable
            email = mBinding.enterEmaillayout.getEditText().getText().toString().trim();
            //Get password from the TextBox  enterPasswordlayout and store in  variable
            password = mBinding.enterPasswordlayout.getEditText().getText().toString().trim();
            //Get mobileno from the TextBox  enterPhonelaout and store in  variable
            mobileno = mBinding.enterPhonelaout.getEditText().getText().toString().trim();

            if (isValid())//check if the data given by user is valid
            {
                if (action.equals("new"))//check if  have to create new user or update
                {

                    //call firebase createUserWithEmailAndPassword method to create user with email and password
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, task -> {

                                //when request is completed


                                //check if request is  Successful or not
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");

                                    //call uploadImageOnStorage to Updload the user image on firebase storage
                                    uploadImageOnStorage();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Failed to create account.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                }
                else //if user is not new means we are updating the data of exsisting data
                    {
                    if (pickedUri != null) //check if image is selected or not
                    {
                        //if image is selected the upload the iamge to server
                        uploadImageOnStorage();
                    }
                    else
                        {

                            //call saveDataOnFirestore to Update date on database and pass paramateres
                        saveDataOnFirestore(signUpModel.getUser_image(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                }
            }
        });


        //if ImageView profilePicture is clicked check we have storeage perimisson to reas user gallery or not
        mBinding.profilePicture.setOnClickListener(v -> checkStoragePremissions());

        //if Already have account textview is clicked
        mBinding.alreadyTextview.setOnClickListener(v -> {

            //if Already have account textview is clicked
            //then open LoginACtivity
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            intent.putExtra("type", type);// pass the type
            startActivity(intent); //open activtiy
            finish(); //close current activity
        });
    }


    //Method to updload image on the firebase storage
    private void uploadImageOnStorage()
    {
        // show progress dialog
        dialog.show();
        dialog.setLabel("Uploading Picture..."); //show message on progress dialog

        //call static uploadPhoto method in FireStoreUploader class to upload image on the firebase storage
        FireStoreUploader.uploadPhoto(pickedUri, new OnFileUploadListeners()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)  //if image is uploaded successfully
            {

                //Get link to download image
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri ->
                {


                    //update text on progress dialog
                    dialog.setLabel(getString(R.string.updating_data));

                    // call saveDataOnFirestore method to store data on fireabase
                    saveDataOnFirestore(String.valueOf(uri), FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Log.i(TAG, "onSuccess: url:" + uri);
                });
            }

            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculate progress in percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                //show progress on dialog
                dialog.setProgress((int) progress);
            }

            @Override
            public void onFailure(String e)
            {

                //if failed to upload the image

                //hide progress dialog
                dialog.dismiss();

                //show log on the screenn
                Log.i(TAG, "onFailure: failed to uplaod:");
                //show error toalst
                Utils.ERROR_TOAST(SignUpActivity.this, "Failed to upload");
            }
        }, FireStorageAddresses.getUsersProfiles() //Pass user profile reference
                                     );
    }

    //Method to saveDataOnFirestore
    private void saveDataOnFirestore(String imageurl, String uid)
    {

        //Show log message on console
        Log.i(TAG, "saveDataOnFirestore: imageUrl:" + imageurl + "\n" + "uid:" + uid);

        //Create object of  SignUpModel
        SignUpModel model;

        //Check if user is updating its profile  or not
        if (action.equals("new"))
        {
            // then create object of SignUpModel
            model = new SignUpModel(device_token_new, imageurl, uid, firstName, lastName, email, password, mobileno, seller_address, type, geoPoint, new ArrayList<>());
        }
        else
            {
                // then create object of SignUpModel
            model = new SignUpModel(device_token_new, imageurl, uid, firstName, lastName, email, password, mobileno, seller_address, type, geoPoint, signUpModel.getImages_list());

            }


        // call setUserRecord method in DatabaseUploader class to upload data on firebase
        DatabaseUploader.setUserRecord(model, new OnTaskListeners()
        {
            @Override
            public void onTaskSuccess() //when data  is uploaded
            {

                dialog.dismiss();//hide the progress dialog

                //if account create is new
                if (action.equals("new"))
                {
                    //Show success toast that Registered Successfully
                    Utils.SUCCESS_TOAST(SignUpActivity.this, "Registered Successfully");
                } else {

                    //Show success toast that Updated Successfully
                    Utils.SUCCESS_TOAST(SignUpActivity.this, "Update Successfully");
                }


                //Create  gson object
                Gson gson = new Gson();
                //convert object date to json string
                String json = gson.toJson(model);
                //put the json string in the shared prefence for later user
                SharedPref.getInstance(SignUpActivity.this).setUSER(json);


                //if account created is new one
                if (action.equals("new"))
                {

                    //if user is customer
                    if (model.getUser_as().equals("customer"))
                    {
                        //go to customer dashboard
                        startActivity(new Intent(SignUpActivity.this, CustomerDashboardActivity.class));
                    } else {
                        //go to barber dashboard
                        startActivity(new Intent(SignUpActivity.this, BarberDashboardActivity.class));
                    }
                }

                finish(); //close this activity
            }

            @Override
            public void onTaskFail(String e) //if fail to update date
            {
                dialog.dismiss(); //close progress bar
                //show error message that Something went wrong"
                Utils.ERROR_TOAST(SignUpActivity.this, "Something went wrong");
            }
        });
    }


    //method to check validation of thata
    private boolean isValid()
    {

        boolean result = true;//boolean variable to track data is validated or not


        if (pickedUri == null)  // if image is not selected
        {
            //if user is new
            if (action.equals("new")) {
                //show error message saying select image
                Utils.ERROR_TOAST(SignUpActivity.this, "Please select an image");

                //set validated varaibel to  false
                result = false;
            }


            //check if first name is entered or not
        } else if (firstName.equals("") || firstName.isEmpty())
        {
            // if not entered show error
            mBinding.enterFirstNameLayout.setError("Required");
            //set validated variable to  false
            result = false;


            //check if last name is entered or not
        } else if (lastName.equals("") || lastName.isEmpty()) {
            // if not entered show error
            mBinding.enterLastNameLayout.setError("Required");
            result = false; //set validated variable to  false


            //check if email  is entered or not
        } else if (email.equals("") || email.isEmpty()) {
            // if not entered show error
            mBinding.enterEmaillayout.setError("Required");
            result = false; //set validated variable to  false
        }
        //check if email is valid or not
        else if (!isEmailValid(email)) {
            // if not entered show error
            mBinding.enterEmaillayout.setError("Please enter valid email");
            result = false;
        }
        //check if  mobile no is entered or not
        else if (mobileno.equals("") || mobileno.isEmpty()) {
            // if not entered show error
            mBinding.enterPhonelaout.setError("Required");
            result = false; //set validated variable to  false
        }
        //check if enter pass   is entered or not
        else if (password.equals("") || password.isEmpty()) {
            // if not entered show error
            mBinding.enterPasswordlayout.setError("Required");
            result = false;//set validated variable to  false
        }
        //check ifseller_address is entered or not
        else if (seller_address.equals("") || seller_address.isEmpty()) {
            // if not entered show error
            Utils.ERROR_TOAST(SignUpActivity.this, "Please Select Location");
            result = false; //set validated variable to  false
        }

        return result; // return validated variable
    }


    //Method to check if email is verified or not
    public static boolean isEmailValid(String email)
    {

        //Regex to check email
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        //Check pattern matches to email or not
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches(); //if email matches return tue
    }

    @Override //onActivityResult method to read the image result
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == 11)//if request code matches our image request cdeo
        {

            // if result is successfull
            if (resultCode == RESULT_OK)
            {
                //get data and store image uri
                pickedUri = data.getData();
                Log.i(TAG, "onActivityResult: pickedUri:" + pickedUri);
                //show image on prifile picture
                mBinding.profilePicture.setImageURI(pickedUri);
            } else {

                //if result not successfull then show error
                Utils.INFO_TOAST(SignUpActivity.this, "You haven't picked Image");
            }
        }
    }

    // for checking location permission
    private void checkLocationPremissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    getLastLocation();
                    Log.i(TAG, "onPermissionsChecked: all premissions granted");
                }

                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // show alert dialog navigating to Settings
                    Log.i(TAG, "onPermissionsChecked: anypermissionDenied:" + report.getDeniedPermissionResponses());
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    // for checking storage permission
    private void checkStoragePremissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Log.i(TAG, "onPermissionsChecked: all premissions granted");
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 11);
                }

                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // show alert dialog navigating to Settings
                    Log.i(TAG, "onPermissionsChecked: anypermissionDenied:" + report.getDeniedPermissionResponses());
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                lat = location.getLatitude();
                                lan = location.getLongitude();
                                geoPoint = new GeoPoint(lat, lan);
                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(SignUpActivity.this, Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(lat, lan, 1);
                                    seller_address = addresses.get(0).getAddressLine(0);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    mBinding.enterAddresslayout.getEditText().setText(seller_address);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            );
        } else
            {

            //Show a toast message to user saying Turn on location
            Utils.INFO_TOAST(SignUpActivity.this, "Turn on location");

            //Move to enable location screen
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    // checking for location enabled or not
    private boolean isLocationEnabled()
    {
        //Get location manager class
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //check if devie location is enabled or not
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission") //Function to request new location of user
    private void requestNewLocationData()
    {

        //Create instance to LocationRequest
        LocationRequest mLocationRequest = new LocationRequest();
        //Set location priority High
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //set interval to update location zero
        mLocationRequest.setInterval(0);
        //set interval to update location zero
        mLocationRequest.setFastestInterval(0);
        //set number of updates to get location only 1 we need only 1 time user location
        mLocationRequest.setNumUpdates(1);

        //Create instance of FusedLocation
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //REquest for user current location
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }


    //Location class back listener it runs when fustlocation object
    //returns a location of current user
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult)
        {

            //get user last known location when his location  was enabled
            //and store in Location Object
            Location mLastLocation = locationResult.getLastLocation();

            //Get lattitude and longnitude and store in the lat,lan variable decalred by me on top
            lat = mLastLocation.getLatitude();
            lan = mLastLocation.getLongitude();

            //create instance of geopoint with the current lat long of user
            geoPoint = new GeoPoint(lat, lan);
            Geocoder geocoder;

            //List to store addresses
            List<Address> addresses;

            //Create intstance of geocode so that we can get current address in string
            geocoder = new Geocoder(SignUpActivity.this, Locale.getDefault());
            try {
                //get address from the current lat long of user  and set maxResults to 1
                addresses = geocoder.getFromLocation(lat, lan, 1);

                //Get the current address on save in seller_address
                seller_address = addresses.get(0).getAddressLine(0);// Here 1 represent max location result to returned, by documents it recommended 1 to 5

                //show the current address on TexBox enterAddresslayout
                mBinding.enterAddresslayout.getEditText().setText(seller_address);

            }
            catch (IOException e) //if any error occurs
            {
                e.printStackTrace(); //print error stacktrace on the console
            }
        }
    };
}