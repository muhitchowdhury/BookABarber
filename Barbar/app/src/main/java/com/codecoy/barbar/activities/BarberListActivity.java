package com.codecoy.barbar.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.codecoy.barbar.R;
import com.codecoy.barbar.Repository.Repository;
import com.codecoy.barbar.adapters.BarberListAdapter;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.databinding.ActivityBarbarListBinding;
import com.codecoy.barbar.listneres.OnBarbersLoadListener;
import com.codecoy.barbar.projectUtils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

/**
 * The purpose of this activity is to load and list all Barbers
 */
public class BarberListActivity extends AppCompatActivity {
    //Simple tag used to in the log classes
    private static final String TAG = BarberListActivity.class.getSimpleName();

    //instance of progress bar , loading bar
    private KProgressHUD kProgressHUD;



    //Instance of activity binding use to get reference to the views of xml layout  like textview etc
    private ActivityBarbarListBinding mBinding;
    private ArrayList<SignUpModel> barber_list;
    private BarberListAdapter adapter;

    @Override //Oncreate functions run when activity is created
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Binding the data our binding instance to the layout so that we can access its views (TextView , EditText Etc)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_barbar_list);

        barber_list = new ArrayList<>();

        //Creating instance of progress bar  in utils class we have static method getProgressDialog which returns object of KProgressHUD
        kProgressHUD = Utils.getProgressDialog(this, "Please wait...");


        //Setting SupportActionBar Title And Enabling Back Button (Support action is  like toolbar we see on top of screen  )
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Barber List"); //Setting title on supportAction Bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Enabling Back button
        }


        getBarbersList();//getBarbersList method to load barber list from database
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setFilter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.setFilter(query);

                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }


    //getBarbersList method to load barber list from database
    private void getBarbersList()
    {

        //Show progress bar
        kProgressHUD.show();


        //calling getBarbers method in Repository class to load barbers list
        Repository.getBarbers(new OnBarbersLoadListener() {
            @Override

            //onBarberLoad Methos runs when the data is loaded
            public void onBarberLoad(ArrayList<SignUpModel> list)
            {
                //Hide progress bar
                kProgressHUD.dismiss();


                //Create GridLayoutManager object to be set on recyclerView
                GridLayoutManager gridLayoutManager = new GridLayoutManager(BarberListActivity.this, 2);

                //Set LayoutManager as GridLayout on Recycler View
                mBinding.barbersRecyclerview.setLayoutManager(gridLayoutManager);

                //Set RecyclerView As Fixed Size
                mBinding.barbersRecyclerview.setHasFixedSize(true);

                //Create Adapter For Recycler View
                adapter = new BarberListAdapter(BarberListActivity.this, list);

                //Set Adapter On Recycler View
                mBinding.barbersRecyclerview.setAdapter(adapter);
            }

            @Override
            public void onEmpty(String message) //onEmpty method is used when there is no data in the database
            {
                //Hide progress bar
                kProgressHUD.dismiss();

                //Show Message To user
                Utils.INFO_TOAST(BarberListActivity.this, message);

            }

            @Override
            public void onFailure(String e)  //onFailure method is used when there is some error when loading the data
            {
                //Hide progress bar
                kProgressHUD.dismiss();
                Log.i(TAG, "onFailure: e:" + e);

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