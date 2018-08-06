package com.comorinland.milkman.vendorapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.CustomerInfo;
import com.comorinland.milkman.common.CustomerInfoDatabase;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.common.SharedHelper;
import com.google.gson.JsonObject;

public class VendorCustomerAddition extends AppCompatActivity implements ResponseHandler
{
    private String mStrCustomerID,mStrCustomerName;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_customer_addition);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarVendorCustomerAdddion);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add a Customer");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnVendorCustomerAddition = (Button)findViewById(R.id.btn_vendor_add_default_delivery);

        btnVendorCustomerAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                boolean bAuthenticateOK = true;
                View focusView = null;

                EditText edtTextCustomerID = (EditText)findViewById(R.id.vendor_customer_mobile_no);
                mStrCustomerID = edtTextCustomerID.getText().toString();

                if (TextUtils.isEmpty(mStrCustomerID))
                {
                    edtTextCustomerID.setError(getString(R.string.error_field_required));
                    focusView = edtTextCustomerID;
                    bAuthenticateOK = false;
                }

                // Check for a valid Customer ID (Mobile Number)
                if (!isMobileIDValid(mStrCustomerID))
                {
                    edtTextCustomerID.setError(getString(R.string.error_invalid_mobile));
                    focusView = edtTextCustomerID;
                    bAuthenticateOK = false;
                }

                EditText edtTextCustomerName = (EditText)findViewById(R.id.vendor_customer_name);
                mStrCustomerName = edtTextCustomerName.getText().toString();

                if (TextUtils.isEmpty(mStrCustomerName))
                {
                    edtTextCustomerName.setError(getString(R.string.error_field_required));
                    focusView = edtTextCustomerName;
                    bAuthenticateOK = false;
                }

                if (bAuthenticateOK == false)
                {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                    return;
                }

                EditText edtTextPostalAddress = (EditText)findViewById(R.id.vendor_customer_address);
                String strPostalAddress = edtTextPostalAddress.getText().toString();

                mProgressDialog = new ProgressDialog(VendorCustomerAddition.this);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                new DownloadFromAmazonDBTask(VendorCustomerAddition.this,"VendorApp/SetCustomerInfo.php",mProgressDialog).execute(jsonBuildInfo(strPostalAddress));
            }
        });
    }

    private boolean isMobileIDValid(String mobileNo) {
        //TODO: Replace this with your own logic
        return mobileNo.length() == 10;
    }

    private JsonObject jsonBuildInfo(String strPostalAddress)
    {
        JsonObject jsonCustomerObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id),null);

        jsonCustomerObject.addProperty("CustomerName", mStrCustomerName);
        if (strPostalAddress.isEmpty() == false)
            jsonCustomerObject.addProperty("PostalAddress", strPostalAddress);

        jsonCustomerObject.addProperty("CustomerID", mStrCustomerID);
        jsonCustomerObject.addProperty("VendorID", strVendorID);

        return jsonCustomerObject;
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

        return Constant.JSON_SUCCESS;
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {
            CustomerInfo customerInfo;
            customerInfo = new CustomerInfo(mStrCustomerID,mStrCustomerName);
            new CustomerDatabaseAsync(VendorCustomerAddition.this).execute(customerInfo);
        }
        else
        {
            SharedHelper.showAlertDialog(VendorCustomerAddition.this, "Customer Addition Failed", null);
        }
    }

    private class CustomerDatabaseAsync extends AsyncTask<CustomerInfo, Void, Boolean>
    {
        Context m_Context;

        protected CustomerDatabaseAsync(Context context)
        {
            m_Context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //Perform pre-adding operation here.
        }

        @Override
        protected Boolean doInBackground(CustomerInfo... params)
        {
            CustomerInfoDatabase.getInstance(m_Context).customerInfoDao().insertCustomerInfo(params[0]);
            return Boolean.TRUE;
        }

        @Override
        protected void onPostExecute(Boolean RetCode)
        {
            super.onPostExecute(RetCode);
            Intent intentConfigureDelivery = new Intent(VendorCustomerAddition.this,VendorConfigureDefaultDelivery.class);
            intentConfigureDelivery.putExtra("CustomerID",mStrCustomerID);
            startActivity(intentConfigureDelivery);
        }
    }
}