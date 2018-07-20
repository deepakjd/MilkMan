/************************************************************************************************
 File Name   : CalendarFragment.java
 Purpose     : Implements a fragment that displays a calendar. It also allows dates to be selected.
 Author      : Deepak J. Daniel
 @Copyright Grace Samadhanam Development Company.
 *************************************************************************************************
 */

package com.comorinland.milkman.customerapp;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.comorinland.milkman.R;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class CalendarFragment extends DialogFragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener = null;
    CalendarPickerView calendar_view;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vw = inflater.inflate(R.layout.fragment_calendar, container, false);

        Button btnDatePicker = (Button) vw.findViewById(R.id.btn_show_dates);
        btnDatePicker.setOnClickListener(this);
        return vw;
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        setShowsDialog(true);
        calendar_view = (CalendarPickerView) getView().findViewById(R.id.calendar_view);

        //Getting current day
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.MONTH, 1);
        Date today = new Date();

        //Add one year to calendar from today's date
        calendar_view.init(today, nextMonth.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_dates:
                //fetch dates
                List<Date> dates = calendar_view.getSelectedDates();
                mListener.onFragmentInteraction(dates);
                getDialog().dismiss();
                break;

            default:
                break;
        }
        return;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson
     * <a href="http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(List<Date> dateList);
    }
}
