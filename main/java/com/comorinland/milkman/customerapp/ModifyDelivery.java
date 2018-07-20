/************************************************************************************************
 File Name   : ModifyDelivery.java
 Purpose     : This file has functionality to customer_daily_delivery the monthly_delivery for a particular day. The customer
 can select milk monthly_delivery information for a particular day. The user can then request
 a modification of monthly_delivery for that particular day.
 Author      : Deepak J. Daniel
 @Copyright Grace Samadhanam Development Company.
 *************************************************************************************************
 */

package com.comorinland.milkman.customerapp;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.DateMessageInterface;
import com.comorinland.milkman.common.DatePickerFragment;
import com.comorinland.milkman.common.MilkInfoFragment;
import com.comorinland.milkman.common.babushkatext.BabushkaText;


public class ModifyDelivery extends AppCompatActivity implements DateMessageInterface
{
    private String mStrScheduledDeliveryDate;

    private void setCurrentDateInCalendarView()
    {
        Typeface typefaceSemiBold = Typeface.createFromAsset(ModifyDelivery.this.getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");

        Calendar c = Calendar.getInstance();
        c.setTime(Calendar.getInstance().getTime());

        int day = c.get(Calendar.DAY_OF_MONTH);

        BabushkaText txtScheduledDeliveryDate = (BabushkaText) findViewById(R.id.txt_day_scheduled_delivery);
        txtScheduledDeliveryDate.setText(String.valueOf(day));

        BabushkaText txtScheduledWeekDay = (BabushkaText) findViewById(R.id.txt_weekday_scheduled_delivery);
        String strWeekDayName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        txtScheduledWeekDay.addPiece(new BabushkaText.Piece.Builder(strWeekDayName).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        txtScheduledWeekDay.display();

        BabushkaText txtScheduledMonth = (BabushkaText) findViewById(R.id.txt_month_scheduled_delivery);
        String strMonthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        txtScheduledMonth.addPiece(new BabushkaText.Piece.Builder(strMonthName).typeFace(typefaceSemiBold).textColor(Color.parseColor("#8d8d8d")).build());
        txtScheduledMonth.display();

        DateMessageListener(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+ 1, day);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Button btnScheduledDelivery;
        CardView cv;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_delivery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarModifyDelivery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setCurrentDateInCalendarView();

        cv = (CardView) findViewById(R.id.cardview_modify_delivery);
        cv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DialogFragment fragmentDatePicker = new DatePickerFragment();
                fragmentDatePicker.show(getFragmentManager(), "Date Pick");
            }
        });

        btnScheduledDelivery = (Button) findViewById(R.id.btn_get_info);

        btnScheduledDelivery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String strCustomerId = sharedPref.getString(getString(R.string.customer_id), null);

                Fragment fragmentDisplayMilk = MilkInfoFragment.newInstance(mStrScheduledDeliveryDate, strCustomerId,"CustomerApp" );
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_display_milk_info, fragmentDisplayMilk, "visible_fragment");
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
    }

    @Override
    public void DateMessageListener(int year, int month, int day)
    {
        String NEW_FORMAT = "dd/MM/yyyy";

        mStrScheduledDeliveryDate = String.valueOf(day) + '/' + String.valueOf(month) + '/' + String.valueOf(year);

        SimpleDateFormat sdf = new SimpleDateFormat(NEW_FORMAT);

        try
        {
            Date d = sdf.parse(mStrScheduledDeliveryDate);
            sdf.applyPattern(NEW_FORMAT);
            mStrScheduledDeliveryDate = sdf.format(d);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }
}