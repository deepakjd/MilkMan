package com.comorinland.milkman.customerapp;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DatePickerFragment;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.MilkInfoListAdapter;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.common.SharedHelper;
import com.comorinland.milkman.common.babushkatext.BabushkaText;
import com.comorinland.milkman.common.MilkInfo;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */

public class BillingInfoDailyFragment extends Fragment implements ResponseHandler
{
    private String strBillPriceDate;
    private final ArrayList<MilkInfo> arrayListMilkInfo;
    private Number numMilkCost;
    private ProgressDialog mProgressDialog;

    public BillingInfoDailyFragment() {
        // Required empty public constructor
        arrayListMilkInfo = new ArrayList<MilkInfo>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_billing_info_daily, container, false);
    }

    public void setRequestedBillDate(int year, int month, int day)
    {
        String NEW_FORMAT = "dd/MM/yyyy";

        String strDate = String.valueOf(day) + '/' + String.valueOf(month) + '/' + String.valueOf(year);

        SimpleDateFormat sdf = new SimpleDateFormat(NEW_FORMAT);

        try
        {
            Date d = sdf.parse(strDate);
            sdf.applyPattern(NEW_FORMAT);
            strDate = sdf.format(d);
        }
        catch (ParseException e)
        {

        }
        strBillPriceDate = strDate;
    }

    private void setCurrentDateInCalendarView() {

        Typeface typefaceSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");

        Calendar c = Calendar.getInstance();
        c.setTime(Calendar.getInstance().getTime());

        int day = c.get(Calendar.DAY_OF_MONTH);

        BabushkaText txtScheduledDeliveryDate = (BabushkaText) getActivity().findViewById(R.id.txt_day_scheduled_delivery);
        txtScheduledDeliveryDate.reset();
        txtScheduledDeliveryDate.setText("");
        txtScheduledDeliveryDate.setText(String.valueOf(day));

        BabushkaText txtScheduledWeekDay = (BabushkaText) getActivity().findViewById(R.id.txt_weekday_scheduled_delivery);
        txtScheduledWeekDay.reset();
        txtScheduledWeekDay.setText("");
        String strWeekDayName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        txtScheduledWeekDay.addPiece(new BabushkaText.Piece.Builder(strWeekDayName).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        txtScheduledWeekDay.display();

        BabushkaText txtScheduledMonth = (BabushkaText) getActivity().findViewById(R.id.txt_month_scheduled_delivery);
        txtScheduledMonth.reset();
        txtScheduledMonth.setText("");
        String strMonthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        txtScheduledMonth.addPiece(new BabushkaText.Piece.Builder(strMonthName).typeFace(typefaceSemiBold).textColor(Color.parseColor("#8d8d8d")).build());
        txtScheduledMonth.display();

        setRequestedBillDate(c.get(Calendar.YEAR), Calendar.MONTH, day);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Button btnDailyBillRequest = (Button) getActivity().findViewById(R.id.btn_get_billing_info_daily);

        btnDailyBillRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                arrayListMilkInfo.clear();
                new DownloadFromAmazonDBTask(BillingInfoDailyFragment.this, "CustomerApp/GetBillForDate.php", mProgressDialog).execute(jsonBuildInfo());
            }
        });

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.img_select_date);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragmentDatePicker = new DatePickerFragment();
                fragmentDatePicker.show(getFragmentManager(), "Date Pick");
            }
        });

        setCurrentDateInCalendarView();
    }

    private JsonObject jsonBuildInfo()
    {
        JsonObject jsonCustomerObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String strCustomerID = sharedPref.getString(getString(R.string.customer_id), null);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id), null);
        String strDistributorName = sharedPref.getString(getString(R.string.distribution_company),null);
        String strCityName = sharedPref.getString(getString(R.string.city_name),null);

        jsonCustomerObject.addProperty("CustomerID", strCustomerID);
        jsonCustomerObject.addProperty("VendorID", strVendorID);
        jsonCustomerObject.addProperty("QueryDate", strBillPriceDate);
        jsonCustomerObject.addProperty("City",strCityName);
        jsonCustomerObject.addProperty("DistributorName",strDistributorName);

        return jsonCustomerObject;
    }

    /* This function is going to get the string response and store it in a
    string array */

    @Override
    public String HandleJsonResponse(String strResponse)
    {
        if (strResponse == null)
        {
            return Constant.RESPONSE_UNAVAILABLE;
        }
        if (strResponse.equals("QUERY_EMPTY"))
        {
            return Constant.INFO_NOT_FOUND;
        }
        if (strResponse.equals("DB_EXCEPTION"))
        {
            return Constant.DB_ERROR;
        }

        try
        {
            JSONObject dataObject = new JSONObject(strResponse);

            String strDate = dataObject.getString("Date");

            if (strDate.isEmpty() || strDate.equals(null)) {
                return Constant.JSON_EXCEPTION;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date dateSelected = null;

            try
            {
                dateSelected = sdf.parse(strDate);
            }
            catch (ParseException e)
            {
                return Constant.JSON_EXCEPTION;
            }

            sdf = new SimpleDateFormat("MMM dd, yyyy");
            String strDisplayDateString = sdf.format(dateSelected);


            JSONObject deliveryObject = dataObject.getJSONObject("Delivery");

            Iterator<String> iter = deliveryObject.keys();

            while (iter.hasNext())
            {
                String key = iter.next();

                if (key.contains("Packet"))
                {

                    JSONObject jsonObjMilk = deliveryObject.getJSONObject(key);
                    MilkInfo milkInfoObject = new MilkInfo(jsonObjMilk.getString("Type"), jsonObjMilk.getInt("PacketNo"), jsonObjMilk.getString("Quantity"));
                    arrayListMilkInfo.add(milkInfoObject);
                }
            }
            numMilkCost = dataObject.getInt("Price");
        }
        catch (JSONException e)
        {
            return Constant.JSON_EXCEPTION;
        }
        return Constant.JSON_SUCCESS;
    }

    /* This function updates the display as shown in the screen. */
    public void UpdateMilkInfoDisplay(String strReturnCode) {
        Typeface typefaceSansItalic = Typeface.createFromAsset(getActivity().getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");
        Typeface typefaceQuicksandBold = Typeface.createFromAsset(getActivity().getAssets(), "font/Quicksand-Bold.ttf");

        BabushkaText txtMilkPrice = (BabushkaText) getActivity().findViewById(R.id.txt_billing_info_daily_price);

        /* Enable the button again */
        Button btnDailyBillRequest = (Button) getActivity().findViewById(R.id.btn_get_billing_info_daily);
        btnDailyBillRequest.setEnabled(true);

        if (strReturnCode.equals(Constant.INFO_NOT_FOUND))
        {
            SharedHelper.showAlertDialog(getActivity(), "Price information not available for this date",null);
            return;
        }
        if ((strReturnCode.equals(Constant.DB_ERROR)) || (strReturnCode.equals(Constant.JSON_EXCEPTION)))
        {
            SharedHelper.showAlertDialog(getActivity(), "Encountered an issue. Please try again", null);
            return;
        }

        ListView lstMilkInfo = (ListView) getActivity().findViewById(R.id.list_milk_info);
        lstMilkInfo.setAdapter(new MilkInfoListAdapter(getActivity(), arrayListMilkInfo));

        txtMilkPrice.reset();
        txtMilkPrice.addPiece(new BabushkaText.Piece.Builder("Milk Price :  ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSansItalic).build());
        txtMilkPrice.addPiece(new BabushkaText.Piece.Builder(String.valueOf(numMilkCost)).textColor(Color.parseColor("#bdbdbd")).typeFace(typefaceQuicksandBold).build());
        txtMilkPrice.display();
    }
}