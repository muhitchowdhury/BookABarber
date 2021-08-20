package com.codecoy.barbar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.codecoy.barbar.R;
import com.codecoy.barbar.Repository.Repository;
import com.codecoy.barbar.adapters.SelectedImagesAdapter;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.database_controller.DatabaseUploader;
import com.codecoy.barbar.databinding.ActivityBarbarDashboardBinding;
import com.codecoy.barbar.firestorage_controller.FireStorageAddresses;
import com.codecoy.barbar.firestorage_controller.FireStoreUploader;
import com.codecoy.barbar.listneres.OnFileUploadListeners;
import com.codecoy.barbar.listneres.OnTaskListeners;
import com.codecoy.barbar.listneres.OnUserProfileLoadListeners;
import com.codecoy.barbar.projectUtils.SharedPref;
import com.codecoy.barbar.projectUtils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import gun0912.tedimagepicker.builder.TedImagePicker;

/**
 * Barber Dashboard where the barber could find his Profile details, add image and check his calendar
 */
public class BarberDashboardActivity extends AppCompatActivity implements SelectedImagesAdapter.DeleteImageCallBack {

    //Simple tag used to in the logs
    private static final String TAG = BarberDashboardActivity.class.getSimpleName();

    //Instance of activity binding use to get reference to the views of activity_barbar_dashboard.xml like textview etc
    private ActivityBarbarDashboardBinding mBinding;
    private SignUpModel signUpModel;
    //instance of progress bar , loading bar
    private KProgressHUD dialog;

    //instance to firebase authentication used to get current user details
    private FirebaseAuth mAuth;

    @Override //Oncreate functions run when activity is created
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Binding the data our binding instance to the layout so that we can access its views (TextView , EditText Etc)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_barbar_dashboard);

        init(); //init methos to intializa Firebase Auth And Progress Bar
        setListeners(); // method to select on press listene for view  (like for buttons etc )
    }


    //setDataOnViews Method is used to show data on screen
    private void setDataOnViews() {
        Log.i(TAG, "setDataOnViews: images_list:" + signUpModel.getImages_list().size());

        //Load image of user using glide library
        Glide.with(this).load(signUpModel.getUser_image()).placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image).into(mBinding.barberImageview);


        //Show user name on screen
        mBinding.barberNameTv.setText(signUpModel.getUser_first_name() + " " + signUpModel.getUser_last_name());

        //Create GridLayoutManager object to be set on recyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        //Set LayoutManager as GridLayout on Recycler View
        mBinding.imagesRecyclerview.setLayoutManager(gridLayoutManager);

        //Set RecyclerView As Fixed Size
        mBinding.imagesRecyclerview.setHasFixedSize(true);

        //Create Adapter For Recycler View
        SelectedImagesAdapter adapter = new SelectedImagesAdapter(signUpModel.getImages_list(), BarberDashboardActivity.this);
        //Set Adapter On Recycler View
        mBinding.imagesRecyclerview.setAdapter(adapter);
    }

    // Load User data on every activity onResume
    private void getUserProfile()
    {
        //Call getMyProfile method from Repository class  to get user profile
        Repository.getMyProfile(mAuth.getCurrentUser().getUid(), new OnUserProfileLoadListeners() {
            @Override
            public void onUserProfileLoaded(SignUpModel userProfile) {


                //Create instance og Gson Class
                Gson gson = new Gson();
                //Convet User Date to json String
                String json = gson.toJson(userProfile);
                //Save Data to Shared Pref Class
                SharedPref.getInstance(BarberDashboardActivity.this).setUSER(json);

                signUpModel = userProfile;
                setDataOnViews();//Show Data On Screen
            }

            @Override //onFailure method is used when there is some error when loading the data
            public void onFailure(String e) {
                //dialog.dismiss();
                //Show Message On Console
                Log.i(TAG, "onFailure: " + e);
            }

            @Override //onEmpty method is used when there is no data in the database
            public void onEmpty(String message) {
                //dialog.dismiss();
                //Show Message On Console
                Log.i(TAG, "onEmpty: " + message);
            }
        });
    }

    //setListeners method to set Click Listeners For Views
    private void setListeners()
    {


        //When Calander Image is clicked open BarberDateActivity class
        mBinding.calanderImageview.setOnClickListener(v -> startActivity(new Intent(BarberDashboardActivity.this, BarberDateActivity.class)));



        //LogOut  Button Click Listener
        mBinding.logoutTv.setOnClickListener(v -> {
            mAuth.signOut(); //Signout from firebase auth
            //Delte data from SharefPreference
            SharedPref.getInstance(BarberDashboardActivity.this).setUSER(null);

            //Create Intent to go to SelectionActivity class
            startActivity(new Intent(BarberDashboardActivity.this, SelectionActivity.class));
            finish();//Close this screen
        });



        //Edit Button click Listener
        mBinding.editTv.setOnClickListener(v -> {


            //Create Intent to go to v class
            Intent intent = new Intent(BarberDashboardActivity.this, SignUpActivity.class);
            intent.putExtra("type", signUpModel.getUser_as()); //Put User Type
            intent.putExtra("action", "update"); //Put User Action
            startActivity(intent); //Open Sign Up  Class
        });



        //When add Button is clicked
        mBinding.addBtn.setOnClickListener(v ->


                //User TedImagePicker Library to select image from phone
                TedImagePicker.with(BarberDashboardActivity.this)
                .start(uri -> {

                    //Show progress bar
                    dialog.show();


                    //call static uploadPhoto method in FireStoreUploader class to upload image on the firebase storage
                    FireStoreUploader.uploadPhoto(uri, new OnFileUploadListeners() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) //if image is uploaded successfully
                        {


                            //Get link to download image
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> {

                                //Update Message On Dialog Box
                                dialog.setLabel(getString(R.string.updating_data));
                                //images_list.add(String.valueOf(uri));


                                //   updateImages used to update image url of user profile
                                DatabaseUploader.updateImages(String.valueOf(uri1), mAuth.getCurrentUser().getUid(), new OnTaskListeners() {
                                    @Override
                                    public void onTaskSuccess()  //onTaskSuccess runs when task is loaded successfully
                                    {
                                        //Hide Progress Bar
                                        dialog.dismiss();
                                        Log.i(TAG, "onTaskSuccess: images saved");

                                        //Show Success Message
                                        Utils.SUCCESS_TOAST(BarberDashboardActivity.this, "Image Saved Successfully");
                                    }

                                    @Override   //onFailure method is used when there is some error when loading the data
                                    public void onTaskFail(String e)
                                    {
                                        //Hide Progress Bar
                                        dialog.dismiss();
                                        //Show Error Message
                                        Utils.ERROR_TOAST(BarberDashboardActivity.this, e);
                                    }
                                });
                            });
                        }

                        @Override  //onProgress method is used when the data load is in progress
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //Calculate progress bar in percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //show progress on dialog box
                            dialog.setProgress((int) progress);
                        }

                        @Override   //onFailure method is used when there is some error when loading the data
                        public void onFailure(String e)
                        {
                            //Hide progress bar
                            dialog.dismiss();
                            Log.i(TAG, "onFailure: failed to upload:");
                            //Show error message on screen
                            Utils.ERROR_TOAST(BarberDashboardActivity.this, "Failed to upload");
                        }
                    }, FireStorageAddresses.getUsersProfiles());
                }));
    }

    private void init() {

        //Creating firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //Creating instance of progress bar  in utils class we have static method getProgressDialog which returns object of KProgressHUD
        dialog = Utils.getProgressDialog(this, "Please Wait");
    }


    @Override//onResume method runs when activity is resumed
    protected void onResume()
    {
        super.onResume();
        getUserProfile();//Calling getUserProfile Method to load user data from database
    }
    @Override
    public void onDeleteCallBack(int pos) {
    }
}