package com.comorinland.milkman.common;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Locale;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.babushkatext.BabushkaText;


/**
 * Created by deepak on 18/4/17.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static DateMessageInterface dateMessageInterface;

    public DatePickerFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dateMessageInterface = (DateMessageInterface) context;
    }

    @Override
    public DatePickerDialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
        return dpd;
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        Typeface typefaceSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");

        BabushkaText txtScheduledDeliveryDate = (BabushkaText) getActivity().findViewById(R.id.txt_day_scheduled_delivery);
        txtScheduledDeliveryDate.reset();
        txtScheduledDeliveryDate.setText("");
        txtScheduledDeliveryDate.setText(String.valueOf(day));

        BabushkaText txtScheduledWeekDay = (BabushkaText) getActivity().findViewById(R.id.txt_weekday_scheduled_delivery);
        txtScheduledWeekDay.reset();
        txtScheduledWeekDay.setText("");

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        String strWeekDayName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        txtScheduledWeekDay.addPiece(new BabushkaText.Piece.Builder(strWeekDayName).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        txtScheduledWeekDay.display();

        BabushkaText txtScheduledMonth = (BabushkaText) getActivity().findViewById(R.id.txt_month_scheduled_delivery);
        txtScheduledMonth.reset();
        txtScheduledMonth.setText("");

        String strMonthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        txtScheduledMonth.addPiece(new BabushkaText.Piece.Builder(strMonthName).typeFace(typefaceSemiBold).textColor(Color.parseColor("#8d8d8d")).build());
        txtScheduledMonth.display();

        dateMessageInterface.DateMessageListener(year, month+1, day);
    }
}