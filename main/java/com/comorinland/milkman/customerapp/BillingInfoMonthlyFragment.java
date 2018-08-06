package com.comorinland.milkman.customerapp;

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
import android.widget.ImageButton;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.common.SharedHelper;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.comorinland.milkman.common.babushkatext.BabushkaText;



public class BillingInfoMonthlyFragment extends Fragment implements ResponseHandler {

    Number mIndex;
    Number maxEntries;
    String mStrBillingStartDate, mStrBillingEndDate,mStrBillStatus;
    int    mBillPrice;
    private ProgressDialog mProgressDialog;

    public BillingInfoMonthlyFragment()
    {
        // Required empty public constructor
        maxEntries = 3;
        mIndex =0;
    }

    public static BillingInfoMonthlyFragment newInstance() {
        BillingInfoMonthlyFragment billingInfoMonthly = new BillingInfoMonthlyFragment();
        return billingInfoMonthly;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mStrBillingEndDate = "";
        mStrBillingStartDate = "";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_billing_info_monthly, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        ImageButton imgBillInfoLeft, imgBillInfoRight;
        int iCurrentYear,iCurrentMonth,iCurrentDay;

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Calendar c = Calendar.getInstance();

        iCurrentYear = c.get(Calendar.YEAR);
        iCurrentMonth = c.get(Calendar.MONTH);
        iCurrentDay = c.get(Calendar.DAY_OF_MONTH);


        new DownloadFromAmazonDBTask(this, "CustomerApp/GetMonthlyBillInfo.php", mProgressDialog).execute(jsonBuildInfo(iCurrentDay,iCurrentMonth + 1,iCurrentYear));

        imgBillInfoLeft = (ImageButton) getActivity().findViewById(R.id.img_billinfo_left);

        imgBillInfoLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int iCurrentYear,iCurrentMonth,iCurrentDay;
                if (mIndex.intValue() < maxEntries.intValue())
                {
                    mIndex = mIndex.intValue() + 1;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    Calendar c = Calendar.getInstance();
                    try
                    {
                        c.setTime(sdf.parse(mStrBillingStartDate));
                    }
                    catch (ParseException e)
                    {
                        return;
                    }
                    c.add(Calendar.DATE, -5);  // number of days to subtract

                    iCurrentYear = c.get(Calendar.YEAR);
                    iCurrentMonth = c.get(Calendar.MONTH);
                    iCurrentDay = c.get(Calendar.DAY_OF_MONTH);
                    new DownloadFromAmazonDBTask(BillingInfoMonthlyFragment.this, "CustomerApp/GetMonthlyBillInfo.php", mProgressDialog).execute(jsonBuildInfo(iCurrentDay,iCurrentMonth + 1,iCurrentYear));

                }
            }
        });

        imgBillInfoRight = (ImageButton) getActivity().findViewById(R.id.img_billinfo_right);

        imgBillInfoRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int iCurrentYear, iCurrentMonth, iCurrentDay;

                if (mIndex.intValue() > 0)
                {
                    mIndex = mIndex.intValue() - 1;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    Calendar c = Calendar.getInstance();
                    try
                    {
                        c.setTime(sdf.parse(mStrBillingEndDate));
                    }
                    catch (ParseException e)
                    {
                        return;
                    }

                    c.add(Calendar.DATE, 5);  // number of days to add

                    iCurrentYear = c.get(Calendar.YEAR);
                    iCurrentMonth = c.get(Calendar.MONTH);
                    iCurrentDay = c.get(Calendar.DAY_OF_MONTH);

                    new DownloadFromAmazonDBTask(BillingInfoMonthlyFragment.this, "CustomerApp/GetMonthlyBillInfo.php", mProgressDialog).execute(jsonBuildInfo(iCurrentDay,iCurrentMonth + 1,iCurrentYear));
                }
            }
        });
    }

    /* This function is going to get the string response and store it in a string array */
    @Override
    public String HandleJsonResponse(String serverJsonResponse)
    {

        if (serverJsonResponse == null)
        {
            return Constant.RESPONSE_UNAVAILABLE;
        }
        if (serverJsonResponse.equals("DB_EXCEPTION"))
        {
            return Constant.DB_ERROR;
        }
        else if (serverJsonResponse.equals("JSON_EXCEPTION"))
        {
            return Constant.JSON_EXCEPTION;
        }
        else
        {
            try
            {
                JSONObject jsonCustomerBill = new JSONObject(serverJsonResponse);

                mStrBillingStartDate = jsonCustomerBill.getString("BillingStartDate");
                mStrBillingEndDate   = jsonCustomerBill.getString("BillingEndDate");
                mBillPrice        = jsonCustomerBill.getInt("Price");

                if (mBillPrice != 0)
                {
                    mStrBillStatus = jsonCustomerBill.getString("Status");
                }
                else
                {
                    mStrBillStatus = "";
                }
            }
            catch (JSONException e)
            {
                return Constant.JSON_EXCEPTION;
            }
        }
        return Constant.JSON_SUCCESS;
    }


    public void UpdateMilkInfoDisplay(String strReturnCode)
    {

        if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date dateBillingStart;
            Date dateBillingEnd;

            Typeface typefaceJFBold = Typeface.createFromAsset(getActivity().getAssets(), "font/JosefinSans-Bold.ttf");
            Typeface typefaceJFSemiBold= Typeface.createFromAsset(getActivity().getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");
            Typeface typefaceQSBold = Typeface.createFromAsset(getActivity().getAssets(), "font/Quicksand-Bold.ttf");

            try
            {
                dateBillingStart = sdf.parse(mStrBillingStartDate);
                dateBillingEnd   = sdf.parse(mStrBillingEndDate);
            }
            catch (ParseException e)
            {
                return;
            }

            sdf = new SimpleDateFormat("MMM dd, yyyy");
            String strBillingStartDate = sdf.format(dateBillingStart);
            String strBillingEndDate   = sdf.format(dateBillingEnd);

            BabushkaText txtBillDates = (BabushkaText) getActivity().findViewById(R.id.txt_bill_dates);
            txtBillDates.reset();
            txtBillDates.setText("");
            txtBillDates.addPiece(new BabushkaText.Piece.Builder(strBillingStartDate + " to " + strBillingEndDate).textColor(Color.parseColor("#8d8d8d")).textSizeRelative(.9f).typeFace(typefaceQSBold).build());
            txtBillDates.display();

            BabushkaText txtBillInfo = (BabushkaText) getActivity().findViewById(R.id.txt_bill_info);
            txtBillInfo.reset();
            txtBillInfo.setText("");
            txtBillInfo.addPiece(new BabushkaText.Piece.Builder("Bill Amount : \u20b9" + mBillPrice).textColor(Color.parseColor("#bdbdbd")).typeFace(typefaceQSBold).build());
            txtBillInfo.display();

            BabushkaText txtBillStatus = (BabushkaText) getActivity().findViewById(R.id.txt_bill_status);
            txtBillStatus.reset();
            txtBillStatus.setText("");

            if (mStrBillStatus.isEmpty() == false)
            {
                txtBillStatus.addPiece(new BabushkaText.Piece.Builder(mStrBillStatus).typeFace(Typeface.DEFAULT_BOLD).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceQSBold).build());
                txtBillStatus.display();
            }
        }
        else
        {
            if ((strReturnCode.equals(Constant.DB_ERROR)) || (strReturnCode.equals(Constant.JSON_EXCEPTION)))
            {
                SharedHelper.showAlertDialog(getActivity(), "Sorry for the problem. Please contact your adminstrator",null);
            }
            else if (strReturnCode.equals(Constant.RESPONSE_UNAVAILABLE))
            {
                SharedHelper.showAlertDialog(getActivity(), "Please check your connection",null);
            }
        }
    }

    private JsonObject jsonBuildInfo(int day, int month, int year)
    {
        JsonObject jsonCustomerObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String strCustomerID = sharedPref.getString(getString(R.string.customer_id), null);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id),null);

        jsonCustomerObject.addProperty("CustomerID", strCustomerID);
        jsonCustomerObject.addProperty("VendorID", strVendorID);
        jsonCustomerObject.addProperty("QueryDay", day);
        jsonCustomerObject.addProperty("QueryMonth", month);
        jsonCustomerObject.addProperty("QueryYear",year);

        return jsonCustomerObject;
    }
}
