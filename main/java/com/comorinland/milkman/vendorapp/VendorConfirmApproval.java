package com.comorinland.milkman.vendorapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.customerapp.DailyMilkPlaceOrder;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VendorConfirmApproval extends AppCompatActivity implements ResponseHandler
{
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final String strScreenType;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_confirm_approval);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarVendorConfirmApproval);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ListView listViewApproveDelivery = (ListView)findViewById(R.id.lst_vendor_confirm_approval_delivery);
        strScreenType = (String)getIntent().getExtras().get("ScreenType");

        if (strScreenType.equals("MilkDelivery"))
        {
            ArrayList<CustomerInfoDate> e = (ArrayList<CustomerInfoDate>)getIntent().getExtras().getSerializable("Customer Info");
            listViewApproveDelivery.setAdapter(new ApprovedDeliveryListAdapter(VendorConfirmApproval.this,e));
        };

        if (strScreenType.equals("CancelDelivery"))
        {
            ArrayList<CustomerCancelInformation> e = (ArrayList<CustomerCancelInformation>)getIntent().getExtras().getSerializable("Customer Info");
            VendorCancelListAdapter vendorCancelListAdapter = new VendorCancelListAdapter(VendorConfirmApproval.this,e);
            vendorCancelListAdapter.MakeCheckBoxesInvisible();
            listViewApproveDelivery.setAdapter(vendorCancelListAdapter);
        }

        if (strScreenType.equals("DefaultDelivery"))
        {
            ArrayList<CustomerInfoDate> e = (ArrayList<CustomerInfoDate>)getIntent().getExtras().getSerializable("Customer Info");
            listViewApproveDelivery.setAdapter(new ApprovedDeliveryListAdapter(VendorConfirmApproval.this,e));
        }

        Button btnConfirmApproval = (Button)findViewById(R.id.btn_vendor_confirm_approval_delivery);

        btnConfirmApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            mProgressDialog = new ProgressDialog(VendorConfirmApproval.this);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            if (strScreenType.equals("MilkDelivery"))
            {
                ArrayList<CustomerInfoDate> e = (ArrayList<CustomerInfoDate>)getIntent().getExtras().getSerializable("Customer Info");
                new DownloadFromAmazonDBTask(VendorConfirmApproval.this,"VendorApp/VendorApproveMilkDelivery.php",mProgressDialog).execute(jsonBuildCustomerInfo(e));
            }
            if (strScreenType.equals("CancelDelivery"))
            {
                ArrayList<CustomerCancelInformation> e = (ArrayList<CustomerCancelInformation>)getIntent().getExtras().getSerializable("Customer Info");
                new DownloadFromAmazonDBTask(VendorConfirmApproval.this,"VendorApp/VendorApproveCancelDelivery.php",mProgressDialog).execute(jsonBuildCustomerCancelInfo(e));
            }
            if (strScreenType.equals("DefaultDelivery"))
            {
                ArrayList<CustomerInfoDate> e = (ArrayList<CustomerInfoDate>)getIntent().getExtras().getSerializable("Customer Info");
                new DownloadFromAmazonDBTask(VendorConfirmApproval.this,"VendorApp/VendorApproveDefaultDelivery.php",mProgressDialog).execute(jsonBuildCustomerInfo(e));
            }
            }
        });
    }

    private JsonObject jsonBuildCustomerCancelInfo(ArrayList<CustomerCancelInformation> e)
    {
        JsonObject jsonVendorObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorId = sharedPref.getString(getString(R.string.vendor_id), null);

        jsonVendorObject.addProperty("VendorID", strVendorId);

        for (int index=0;index < e.size(); index++)
        {
            JsonObject jsonCustomerObject = new JsonObject();
            CustomerCancelInformation customerCancelInfo = e.get(index);
            jsonCustomerObject.addProperty("CustomerID",customerCancelInfo.CustomerID);

            if (customerCancelInfo.getType() == 3)
            {
                jsonCustomerObject.addProperty("CancellationType","Monthly");
            }
            else
            {
                jsonCustomerObject.addProperty("CancellationType","Daily");
                Date dateFromDate = customerCancelInfo.FromDate;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                jsonCustomerObject.addProperty("FromDate",sdf.format(dateFromDate));

                Date dateToDate = customerCancelInfo.ToDate;
                jsonCustomerObject.addProperty("ToDate",sdf.format(dateToDate));
            }
            jsonVendorObject.add("CustomerInfo"+index,jsonCustomerObject);
        }
        return jsonVendorObject;
    }



    private JsonObject jsonBuildCustomerInfo(ArrayList<CustomerInfoDate> e)
    {
        JsonObject jsonVendorObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorId = sharedPref.getString(getString(R.string.vendor_id), null);

        jsonVendorObject.addProperty("VendorID", strVendorId);

        for (int index=0;index < e.size();index++)
        {
            JsonObject jsonCustomerObject = new JsonObject();
            CustomerInfoDate customerInfoDate = e.get(index);
            jsonCustomerObject.addProperty("CustomerID",customerInfoDate.CustomerID);

            if (customerInfoDate.RequestDate != null) {
                Date dateRequestDate = customerInfoDate.RequestDate;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                jsonCustomerObject.addProperty("Date", sdf.format(dateRequestDate));
            }

            jsonVendorObject.add("CustomerInfo"+index,jsonCustomerObject);
        }
        return jsonVendorObject;
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
        else
            return Constant.JSON_SUCCESS;
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(VendorConfirmApproval.this).create();

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
            alertDialog.setMessage("Your change is successful");
        }

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog    closed
                finish();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
}
