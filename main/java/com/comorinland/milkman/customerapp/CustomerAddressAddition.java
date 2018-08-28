package com.comorinland.milkman.customerapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.*;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class CustomerAddressAddition extends AppCompatActivity implements ResponseHandler
{
    private ProgressDialog mProgressDialog;

    String mStrCustomerName;
    String mStrCustomerID;
    String mStrCustomerPassword;
    String mStrCityName;
    String mStrPostalAddress;

    Boolean mbRegisterCustomer;   /* An internal flag to check if it is query for registering
                                     a customer or for querying the City name
                                  */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_address_addition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCustomerAddressAddition);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Enter Your Address");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final RelativeLayout contentView = (RelativeLayout) findViewById(R.id.root_address_content_view);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {

                Rect r = new Rect();

                contentView.getWindowVisibleDisplayFrame(r);

                int screenHeight = contentView.getRootView().getHeight();

                Button btnAddressRegister = (Button) findViewById(R.id.btn_customer_register_with_address);

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15)
                {
                    // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    btnAddressRegister.setVisibility(View.INVISIBLE);
                }
                else
                {
                    // keyboard is closed
                    btnAddressRegister.setVisibility(View.VISIBLE);
                }
            }
        });

        mbRegisterCustomer = Boolean.FALSE;

        mStrCustomerName = (String) getIntent().getExtras().getSerializable("CustomerName");
        mStrCustomerID = (String) getIntent().getExtras().getSerializable("CustomerID");
        mStrCustomerPassword = (String) getIntent().getExtras().getSerializable("CustomerPassword");

        mProgressDialog = new ProgressDialog(CustomerAddressAddition.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        /**
         * The Vendor database is queried to get the City Name.
         */

        new DownloadFromAmazonDBTask(CustomerAddressAddition.this, "CustomerApp/GetCityName.php", mProgressDialog).execute(jsonBuildInfo());

        Button btnCustomerRegister = (Button) findViewById(R.id.btn_customer_register_with_address);

        btnCustomerRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                boolean result = authenticateCustomerAddress();

                if (result == true)
                {
                    mProgressDialog = new ProgressDialog(CustomerAddressAddition.this);
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    new DownloadFromAmazonDBTask(CustomerAddressAddition.this, "CustomerApp/SetCustomerInfo.php", mProgressDialog).execute(jsonBuildCustomerInfo());
                }
            }
        });
    }

    private boolean authenticateCustomerAddress()
    {
        EditText edtCustomerHomeNo;
        EditText edtCustomerStreet;
        EditText edtCustomerLocality;

        String   strCustomerHomeNo;
        String   strCustomerStreet;
        String   strCustomerLocality;

        boolean bAuthenticateOK = true;

        edtCustomerHomeNo = (EditText) findViewById(R.id.edtCustomerHomeNo);
        edtCustomerStreet = (EditText) findViewById(R.id.edtCustomerStreet);
        edtCustomerLocality = (EditText) findViewById(R.id.edtCustomerLocality);

        strCustomerHomeNo = edtCustomerHomeNo.getText().toString();
        strCustomerStreet = edtCustomerStreet.getText().toString();
        strCustomerLocality = edtCustomerLocality.getText().toString();

        View focusView = null;

        // Check for a valid Customer Home/ Apartment number.
        if (TextUtils.isEmpty(strCustomerHomeNo))
        {
            edtCustomerHomeNo.setError(getString(R.string.error_field_required));
            focusView = edtCustomerHomeNo;
            bAuthenticateOK = false;
        }

        // Check for a valid Street Name.
        if (TextUtils.isEmpty(strCustomerStreet))
        {
            edtCustomerStreet.setError(getString(R.string.error_field_required));
            focusView = edtCustomerStreet;
            bAuthenticateOK = false;
        }

        // Check for a valid Customer locality
        if (TextUtils.isEmpty(strCustomerLocality))
        {
            edtCustomerStreet.setError(getString(R.string.error_field_required));
            focusView = edtCustomerLocality;
            bAuthenticateOK = false;
        }

        if (bAuthenticateOK == false)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        mStrPostalAddress = strCustomerHomeNo + ',' + strCustomerStreet + ',' + strCustomerLocality;
        return bAuthenticateOK;

    }

    private JsonObject jsonBuildInfo()
    {
        JsonObject jsonCustomerObject = new JsonObject();

        jsonCustomerObject.addProperty("CustomerID", mStrCustomerID);

        return jsonCustomerObject;
    }

    private JsonObject jsonBuildCustomerInfo()
    {
        JsonObject jsonCustomerObject = new JsonObject();

        jsonCustomerObject.addProperty("CustomerName", mStrCustomerName);
        jsonCustomerObject.addProperty("CustomerID", mStrCustomerID);
        jsonCustomerObject.addProperty("CustomerPassword", mStrCustomerPassword);
        jsonCustomerObject.addProperty("PostalAddress",mStrPostalAddress);

        return  jsonCustomerObject;
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
            mbRegisterCustomer = Boolean.TRUE;

            JSONObject jsonCustomerObject = new JSONObject(strResponse);

            Iterator<String> iter = jsonCustomerObject.keys();

            while (iter.hasNext())
            {
                String strKey = iter.next();

                if (strKey.equals("City"))
                {
                    mStrCityName = jsonCustomerObject.getString("City");
                    mbRegisterCustomer = Boolean.FALSE;
                }
            }
        }
        catch (JSONException e)
        {
            return Constant.JSON_EXCEPTION;
        }
        return Constant.JSON_SUCCESS;
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        if (strReturnCode.equals(Constant.DB_ERROR))
        {
            Intent intent = new Intent(CustomerAddressAddition.this,CustomerLogin.class);
            com.comorinland.milkman.common.SharedHelper.showAlertDialog(CustomerAddressAddition.this, "Sorry there was a problem. Please contact your vendor",intent);
        }

        if (strReturnCode.equals(Constant.RESPONSE_UNAVAILABLE))
        {
            Intent intent = new Intent(CustomerAddressAddition.this,CustomerLogin.class);
            com.comorinland.milkman.common.SharedHelper.showAlertDialog(CustomerAddressAddition.this, "You have not been added by the vendor yet. Please request the vendor to add your name",intent);
        }

        if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {
            if (mbRegisterCustomer == Boolean.TRUE)
            {
                /* Retain the Customer ID alone. We do not want the user to type Customer mobile number again
                 * in the login screen */
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.customer_id), mStrCustomerID);
                editor.commit();

                Intent intent = new Intent(CustomerAddressAddition.this,CustomerLogin.class);
                com.comorinland.milkman.common.SharedHelper.showAlertDialog(CustomerAddressAddition.this, "Registration succesful. You can login with your password",intent);
            }
            else
            {
                EditText edtCustomerCity = (EditText) findViewById(R.id.edtCustomerCity);
                edtCustomerCity.setText(mStrCityName);
            }
        }
    }
}