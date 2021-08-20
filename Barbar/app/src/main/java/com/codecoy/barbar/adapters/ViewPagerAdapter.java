package com.codecoy.barbar.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codecoy.barbar.fragments.CalendarFragment;
import com.codecoy.barbar.fragments.ProfileFragment;
//Custom Adapter class for viewpager in CustomBaberActivity
public class ViewPagerAdapter extends FragmentPagerAdapter {
    //initializing variables
    String user_id;
    Context context;
    //constructor to pass fragment Manger,user)id and context
    public ViewPagerAdapter(FragmentManager fm, String user_id, Context context) {
        super(fm);
        this.user_id = user_id;
        this.context = context;
    }

    @Override //this method returns fragment on given position
    public Fragment getItem(int position) {

        Fragment fragment = null; //Instance of fragment
        //if fragment position is 0 then show profile fragment
        if (position == 0) {
            fragment = new ProfileFragment(context, user_id);
        }
        //if fragment position is 1 then show Calender fragment
        else if (position == 1) {
            fragment = new CalendarFragment(user_id, context);
        }
        return fragment;
    }
    //delaring size of this adapter as 2
    @Override
    public int getCount() {
        return 2;
    }
    //delcaring page titles displayed for each fragment
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "";
        } else if (position == 1) {
            title = "";
        }
        return title;
    }
}
