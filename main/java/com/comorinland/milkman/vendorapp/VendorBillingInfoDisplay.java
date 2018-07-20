package com.comorinland.milkman.vendorapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.CustomerDatabaseAsync;
import com.comorinland.milkman.common.CustomerInfo;
import com.comorinland.milkman.common.DataHandler;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.common.babushkatext.BabushkaText;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


class CustomerBillInfo implements Serializable
{
    String mCustomerID;
    String mCustomerName;
    Number mPrice;
    String mStatus;

    public CustomerBillInfo(String customerID, String customerName, Number Price, String strStatus)
    {
        mCustomerID = customerID;
        mCustomerName = customerName;
        mPrice = Price;
        mStatus = strStatus;
    }

    public CustomerBillInfo(CustomerBillInfo e)
    {
        mCustomerID = e.mCustomerID;
        mCustomerName = e.mCustomerName;
        mPrice = e.mPrice;
        mStatus = e.mStatus;
    }
}

public class VendorBillingInfoDisplay extends AppCompatActivity implements DataHandler,ResponseHandler
{
    private ProgressDialog mProgressDialog;
    ArrayList<CustomerBillInfo> mArrayListCustomerBillInfo = new ArrayList<>();
    ArrayList<CustomerInfo> mlistCustomerInfo = new ArrayList<>();
    ListView mlistViewBillInfoDisplay;
    String mBillingStartDate, mBillingEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_billing_info_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBillingInfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBillingEndDate = "";
        mBillingStartDate = "";

        Button btnUpdateBillStatus = (Button)findViewById(R.id.btn_vendor_billing_info_update);

        btnUpdateBillStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            VendorBillingListAdapter billingListAdapter = (VendorBillingListAdapter)mlistViewBillInfoDisplay.getAdapter();

            if (billingListAdapter != null)
            {
                ArrayList<CustomerBillInfo> e = new ArrayList<CustomerBillInfo>();
                e.addAll(billingListAdapter.getSelectedApprovals());

                if (e.isEmpty())
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(VendorBillingInfoDisplay.this).create();

                    // Setting Dialog Title
                    alertDialog.setMessage("Nothing has been changed to update");

                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog    closed
                            return;
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                }

                Intent intentUpdateBillStatus = new Intent(VendorBillingInfoDisplay.this, VendorUpdateBillStatus.class);
                intentUpdateBillStatus.putExtra("Bill Update",e);
                intentUpdateBillStatus.putExtra("Bill Start Date", mBillingStartDate);
                startActivity(intentUpdateBillStatus);
                finish();
            }
            }
        });

        ImageButton btnImgBillInfoLeft = (ImageButton)findViewById(R.id.img_billinfo_left);

        btnImgBillInfoLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int iCurrentYear, iCurrentMonth, iCurrentDay;

                mProgressDialog = new ProgressDialog(VendorBillingInfoDisplay.this);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Calendar c = Calendar.getInstance();
                try
                {
                    c.setTime(sdf.parse(mBillingStartDate));
                }
                catch (ParseException e)
                {
                    return;
                }
                c.add(Calendar.DATE, -5);  // number of days to subtract

                iCurrentYear = c.get(Calendar.YEAR);
                iCurrentMonth = c.get(Calendar.MONTH);
                iCurrentDay = c.get(Calendar.DAY_OF_MONTH);

                new DownloadFromAmazonDBTask(VendorBillingInfoDisplay.this, "VendorApp/GetCustomerBillInfo.php", mProgressDialog).execute(jsonBuildInfo(iCurrentDay,iCurrentMonth+1,iCurrentYear));
            }
        });

        ImageButton btnImgBillInfoRight = (ImageButton) findViewById(R.id.img_billinfo_right);

        btnImgBillInfoRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int iCurrentYear, iCurrentMonth, iCurrentDay;

                mProgressDialog = new ProgressDialog(VendorBillingInfoDisplay.this);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Calendar c = Calendar.getInstance();
                try
                {
                    c.setTime(sdf.parse(mBillingEndDate));
                }
                catch (ParseException e)
                {
                    return;
                }
                c.add(Calendar.DATE, 5);  // number of days to add

                iCurrentYear = c.get(Calendar.YEAR);
                iCurrentMonth = c.get(Calendar.MONTH);
                iCurrentDay = c.get(Calendar.DAY_OF_MONTH);

                new DownloadFromAmazonDBTask(VendorBillingInfoDisplay.this, "VendorApp/GetCustomerBillInfo.php", mProgressDialog).execute(jsonBuildInfo(iCurrentDay,iCurrentMonth + 1,iCurrentYear));
            }
        });

        BabushkaText babushkaTextBillDateInfo = (BabushkaText)findViewById(R.id.txt_bill_dates);
        babushkaTextBillDateInfo.addPiece(new BabushkaText.Piece.Builder("Bill Period  \n").textSize(40).build());
        babushkaTextBillDateInfo.display();

        new CustomerDatabaseAsync(this, getApplicationContext()).execute();
    }

    private JsonObject jsonBuildInfo(int day, int month, int year)
    {
        JsonObject jsonVendorObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorId = sharedPref.getString(getString(R.string.vendor_id), null);

        jsonVendorObject.addProperty("VendorID", strVendorId);

        jsonVendorObject.addProperty("QueryDay", day);
        jsonVendorObject.addProperty("QueryMonth", month);
        jsonVendorObject.addProperty("QueryYear",year);

        return jsonVendorObject;
    }

    public void SaveCustomerInfo(List<CustomerInfo> lstCustomerInfo)
    {
        int iCurrentYear,iCurrentMonth,iCurrentDay;

        mlistCustomerInfo.addAll(lstCustomerInfo);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Calendar c = Calendar.getInstance();

        iCurrentYear = c.get(Calendar.YEAR);
        iCurrentMonth = c.get(Calendar.MONTH);
        iCurrentDay = c.get(Calendar.DAY_OF_MONTH);

        // Create thread to get data from the server.
        new DownloadFromAmazonDBTask(this, "VendorApp/GetCustomerBillInfo.php", mProgressDialog).execute(jsonBuildInfo(iCurrentDay,iCurrentMonth + 1,iCurrentYear));
    }

    public String getCustomerNameFromId(String strCustomerId)
    {
        String strCustomerName = null;

        if (mlistCustomerInfo.isEmpty())
        {
            strCustomerName = "Name Unknown";
            return strCustomerName;
        }

        for (CustomerInfo xCustomerInfo : mlistCustomerInfo)
        {
            if (xCustomerInfo.CustomerID.equals(strCustomerId))
            {
                strCustomerName = xCustomerInfo.CustomerName;
                break;
            }
            else
                strCustomerName = "Name Unknown";
        }

        return strCustomerName;
    }

    /* This function is going to get the string response and store it in a string array */
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
            JSONObject jsonCustomerBillList = new JSONObject(strResponse);

            Iterator<String> iter = jsonCustomerBillList.keys();

            while (iter.hasNext())
            {
                String key = iter.next();

                if (key.toString().equals("BillingStartDate"))
                {
                    mBillingStartDate = jsonCustomerBillList.getString("BillingStartDate");
                    continue;
                }
                if (key.toString().equals("BillingEndDate"))
                {
                    mBillingEndDate = jsonCustomerBillList.getString("BillingEndDate");
                    continue;
                }

                JSONObject jsonCustomerBill = jsonCustomerBillList.getJSONObject(key);
                String strCustomerName = getCustomerNameFromId(jsonCustomerBill.getString("CustomerID"));
                mArrayListCustomerBillInfo.add(new CustomerBillInfo(jsonCustomerBill.getString("CustomerID"),strCustomerName, jsonCustomerBill.getInt("Price"),jsonCustomerBill.getString("Status")));
            }
            if (mBillingStartDate.isEmpty())
            {
                return Constant.INFO_NOT_FOUND;
            }
            if (mBillingEndDate.isEmpty())
            {
                return Constant.INFO_NOT_FOUND;
            }
        }
        catch (JSONException e)
        {
            return Constant.JSON_EXCEPTION;
        }

        return Constant.JSON_SUCCESS;
    }

    public String ConvertDateToDisplayFormat(String strDateForDisplay)
    {
        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMM");

        Date dateYMDFormat = null;

        try
        {
            dateYMDFormat = sdfIn.parse(strDateForDisplay);
        }
        catch (ParseException e)
        {
            return Constant.JSON_EXCEPTION;
        }
        String strDisplayDate = sdfOut.format(dateYMDFormat);
        return strDisplayDate;
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {
            ListAdapter lstBillInfoAdapter;
            String strBillStartDate, strBillEndDate;
            strBillStartDate = ConvertDateToDisplayFormat(mBillingStartDate);
            strBillEndDate = ConvertDateToDisplayFormat(mBillingEndDate);

            BabushkaText babushkaTextBillDateInfo = (BabushkaText)findViewById(R.id.txt_bill_dates);
            babushkaTextBillDateInfo.reset();
            babushkaTextBillDateInfo.setText("");
            babushkaTextBillDateInfo.addPiece(new BabushkaText.Piece.Builder("Bill Period  \n\n").textSize(40).build());
            babushkaTextBillDateInfo.addPiece(new BabushkaText.Piece.Builder(strBillStartDate + " to " + strBillEndDate).textColor(Color.parseColor("#8d8d8d")).build());
            babushkaTextBillDateInfo.display();

            mlistViewBillInfoDisplay = (ListView) findViewById(R.id.lst_vendor_bill_display);

            lstBillInfoAdapter = new VendorBillingListAdapter(VendorBillingInfoDisplay.this, mArrayListCustomerBillInfo,"VENDOR_BILLING_DISPLAY");

            mlistViewBillInfoDisplay.setAdapter(lstBillInfoAdapter);

            mArrayListCustomerBillInfo.clear();
        }
    }
}