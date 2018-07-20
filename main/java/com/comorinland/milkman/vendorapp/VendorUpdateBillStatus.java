package com.comorinland.milkman.vendorapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;

import com.google.gson.JsonObject;
import java.util.ArrayList;

public class VendorUpdateBillStatus extends AppCompatActivity implements ResponseHandler {

    private ProgressDialog mProgressDialog;
    private String  mStringBillDate;
    ArrayList<CustomerBillInfo> mArrListCustomerBillInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ListView    lstViewUpdateBillStatus;
        ListAdapter lstUpdateBillStatusAdapter;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_update_bill_status);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUpdateBillStatus);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBillInfoScreen = new Intent(VendorUpdateBillStatus.this, VendorBillingInfoDisplay.class);
                startActivity(intentBillInfoScreen);
                finish();
            }
        });

        // Create thread to get data from the server.
        mProgressDialog = new ProgressDialog(VendorUpdateBillStatus.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mStringBillDate = (String) getIntent().getExtras().getSerializable("Bill Start Date");
        mArrListCustomerBillInfo = (ArrayList<CustomerBillInfo>) getIntent().getExtras().getSerializable("Bill Update");

        lstViewUpdateBillStatus = (ListView) findViewById(R.id.lst_vendor_update_bill_status);

        lstUpdateBillStatusAdapter = new VendorBillingListAdapter(VendorUpdateBillStatus.this, mArrListCustomerBillInfo,"VENDOR_UPDATE_BILL");

        lstViewUpdateBillStatus.setAdapter(lstUpdateBillStatusAdapter);

        Button btnUpdateBullStatus = (Button)findViewById(R.id.btn_vendor_billing_confirm);

        btnUpdateBullStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Create thread to update data to the server.
                new DownloadFromAmazonDBTask(VendorUpdateBillStatus.this, "VendorApp/VendorUpdateBillInfo.php", mProgressDialog).execute(jsonBuildInfo());
            }
        });
    }

    private JsonObject jsonBuildInfo()
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        JsonObject jsonUpdateBillObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorId = sharedPref.getString(getString(R.string.vendor_id), null);

        jsonUpdateBillObject.addProperty("VendorID", strVendorId);
        jsonUpdateBillObject.addProperty("BillStartDate", mStringBillDate);

        for (int index=0; index<mArrListCustomerBillInfo.size();index++)
        {
            JsonObject jsonCustomerBillInfo = new JsonObject();
            jsonCustomerBillInfo.addProperty("CustomerID",mArrListCustomerBillInfo.get(index).mCustomerID);
            jsonCustomerBillInfo.addProperty("Status",mArrListCustomerBillInfo.get(index).mStatus);
            jsonUpdateBillObject.add("CustomerBillInfo"+index,jsonCustomerBillInfo);
        }

        return jsonUpdateBillObject;
    }

    /* This function is going to get the string response and store it in a string array */
    @Override
    public String HandleJsonResponse(String serverJsonResponse)
    {
        if (serverJsonResponse == null)
        {
            return Constant.RESPONSE_UNAVAILABLE;
        }
        if (serverJsonResponse == "QUERY_EMPTY")
        {
            return Constant.INFO_NOT_FOUND;
        }
        if (serverJsonResponse.equals("DB_EXCEPTION"))
        {
            return Constant.DB_ERROR;
        }
        else
            return Constant.JSON_SUCCESS;
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(VendorUpdateBillStatus.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert");

        if (strReturnCode.equals(Constant.DB_ERROR))
        {
            // Setting Dialog Message
            alertDialog.setMessage("Sorry there was a problem. Please contact your administrator");
        }
        else if (strReturnCode.equals(Constant.RESPONSE_UNAVAILABLE))
        {
            alertDialog.setMessage("Please check your connection");
        }
        else
        {
            alertDialog.setMessage("Bill Status changes have been updated");
        }

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog    closed
                Intent intentBillInfoScreen = new Intent(VendorUpdateBillStatus.this, VendorBillingInfoDisplay.class);
                startActivity(intentBillInfoScreen);
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
