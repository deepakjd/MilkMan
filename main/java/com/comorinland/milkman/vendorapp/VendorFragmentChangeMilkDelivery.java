package com.comorinland.milkman.vendorapp;


import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.DatePickerFragment;
import com.comorinland.milkman.common.MilkInfoFragment;
import com.comorinland.milkman.common.babushkatext.BabushkaText;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */

public class VendorFragmentChangeMilkDelivery extends Fragment
{
    CustomerIdFromDatabaseInterface mInterfaceCustomerIdFromDatabase;
    String mStrCustomerID;
    String mStrScheduledDeliveryDate;

    public VendorFragmentChangeMilkDelivery()
    {
        // Required empty public constructor
    }

    public static VendorFragmentChangeMilkDelivery newInstance()
    {
        VendorFragmentChangeMilkDelivery fragment = new VendorFragmentChangeMilkDelivery();
        return fragment;
    }

    public void DisplayCustomerID(String strCustomerID)
    {
        mStrCustomerID = strCustomerID;

        EditText edtCustomerID = (EditText)getActivity().findViewById(R.id.edit_vendor_customer_id);
        edtCustomerID.setText(strCustomerID);

        setCurrentDateInCalendarView();
    }

    public void SetChangeDeliveryDate(String date)
    {
        mStrScheduledDeliveryDate = date;
    }

    private void setCurrentDateInCalendarView()
    {
        Typeface typefaceSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");

        Calendar c = Calendar.getInstance();
        c.setTime(Calendar.getInstance().getTime());

        int day = c.get(Calendar.DAY_OF_MONTH);

        BabushkaText txtScheduledDeliveryDate = (BabushkaText) getActivity().findViewById(R.id.txt_day_scheduled_delivery);
        txtScheduledDeliveryDate.setText(String.valueOf(day));

        BabushkaText txtScheduledWeekDay = (BabushkaText) getActivity().findViewById(R.id.txt_weekday_scheduled_delivery);
        String strWeekDayName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        txtScheduledWeekDay.reset();
        txtScheduledWeekDay.setText("");
        txtScheduledWeekDay.addPiece(new BabushkaText.Piece.Builder(strWeekDayName).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        txtScheduledWeekDay.display();

        BabushkaText txtScheduledMonth = (BabushkaText) getActivity().findViewById(R.id.txt_month_scheduled_delivery);
        String strMonthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        txtScheduledMonth.reset();
        txtScheduledMonth.setText("");
        txtScheduledMonth.addPiece(new BabushkaText.Piece.Builder(strMonthName).typeFace(typefaceSemiBold).textColor(Color.parseColor("#8d8d8d")).build());
        txtScheduledMonth.display();

        mInterfaceCustomerIdFromDatabase.DateMessageListener(c.get(Calendar.YEAR), Calendar.MONTH, day);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.vendor_fragment_change_milk_delivery, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mInterfaceCustomerIdFromDatabase = (CustomerIdFromDatabaseInterface)context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        EditText edtCustomerID = (EditText)getActivity().findViewById(R.id.edit_vendor_customer_id);
        edtCustomerID.setInputType(InputType.TYPE_NULL);

        edtCustomerID.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mInterfaceCustomerIdFromDatabase.getCustomerIdFromDatabase();
            }

        });

        ImageView imageView = (ImageView)getActivity().findViewById(R.id.imgCalendarView);

        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                DialogFragment fragmentDatePicker = new DatePickerFragment();
                fragmentDatePicker.show(getFragmentManager(), "Date Pick");
            }
        });

        Button btnGetInfo = (Button)getActivity().findViewById(R.id.btn_get_info);

        btnGetInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Fragment fragmentDisplayMilk = MilkInfoFragment.newInstance(mStrScheduledDeliveryDate, mStrCustomerID,"VendorApp");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_display_milk_info, fragmentDisplayMilk, "visible_fragment");
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
    }
}