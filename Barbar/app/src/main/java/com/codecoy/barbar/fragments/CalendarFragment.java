package com.codecoy.barbar.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.codecoy.barbar.R;
import com.codecoy.barbar.Repository.Repository;
import com.codecoy.barbar.adapters.EventsAdapter;
import com.codecoy.barbar.dataModels.EventModel;
import com.codecoy.barbar.databinding.FragmentCalendarBinding;
import com.codecoy.barbar.listneres.OnDatesLoadListener;
import com.codecoy.barbar.projectUtils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.applandeo.materialcalendarview.utils.DateUtils.getCalendar;

/**
 * This fragment is to show the Calendar for Barber and Customer
 */
public class CalendarFragment extends Fragment
{

    //Simple tag used to in the log classes
    private static final String TAG = CalendarFragment.class.getSimpleName();

    //Instance of activity binding use to get reference to the views of xml layout  like textview etc
    private FragmentCalendarBinding mBinding;
    //instance of progress bar , loading bar
    private KProgressHUD kProgressHUD;
    //ArrayList to store events list
    private ArrayList<EventModel> event_list;

    //String Variable to store User Id
    private String user_id;

    //Variable to store context
    private Context context;


    //Constructor to assign values
    public CalendarFragment(String user_id, Context context)
    {
        this.user_id = user_id;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);

        //Create Progress Dialog
        kProgressHUD = Utils.getProgressDialog(context, "Please wait...");

        //Creating ArrayList
        event_list = new ArrayList<>();

        loadDates(); //Calling loadDates method to load data from database and on the recyclerView
        return mBinding.getRoot();
    }


    //loadDates method to load data from database and on the recyclerView
    private void loadDates()
    {

        //Show progress bar on screen
        kProgressHUD.show();

        //Calling getBarberEvent from Repository class to load events
        Repository.getBarberEvent(user_id, new OnDatesLoadListener() {
            @Override
            public void onDatesLoad(ArrayList<EventModel> list) //this method runs when data is loaded
            {
                //hide progress bar
                kProgressHUD.dismiss();
                event_list.addAll(list); //Add all events to event_list
                setDataOnViews(); //show data on screen
            }

            @Override
            public void onEmpty(String message) //onEmpty runs when there is no data on the database
            {

                //hide progress bar
                kProgressHUD.dismiss();
                //Show message on screen
                Utils.INFO_TOAST(context, "No Data Found");
            }

            @Override
            public void onFailure(String e)  //onFailure runs when there is error while loading data
            {

                //hide progress bar
                kProgressHUD.dismiss();
                //Show error message on the console
                Log.i(TAG, "onFailure: " + e);
            }
        });
    }

    private void setDataOnViews()
    {

        //Create event list
        List<EventDay> events = new ArrayList<>();


        //Loop on all event list
        for (int i = 0; i < event_list.size(); i++)
        {

            //Creating instance of DateFormatter
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

            //Creating instance of date Object
            Date d = null;
            try
            {
                //Convert String Date to Date Object
                d = format.parse(event_list.get(i).getDate());
            }
            catch (ParseException e)  //if any error while converting date to Date Object
            {
                //Show error message on console
                e.printStackTrace();
            }

            //Creating Calender Object
            Calendar c = getCalendar();
            c.setTime(d); //set time on calander

            //Add event in events list
            events.add(new EventDay(c, R.drawable.dot_icon_new, Color.parseColor("#228B22")));
        }


        //Set Events on calenderVie
        mBinding.calendarView.setEvents(events);


        //Create instance of EventsAdapter Which will be used for RecyclerView
        EventsAdapter adapter = new EventsAdapter(event_list, context);

        //Create Linear Layout Manager Object  and set orientation as VERTICAL
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);

        //Set layout on the Recycler view
        mBinding.datesRecyclerview.setLayoutManager(layoutManager);
        //Set Fixed Size to RecyclerView
        mBinding.datesRecyclerview.setHasFixedSize(true);

        //Set Adapter on recyclerView
        mBinding.datesRecyclerview.setAdapter(adapter);
    }
}