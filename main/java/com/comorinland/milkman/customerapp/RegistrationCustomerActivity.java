package com.comorinland.milkman.customerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.common.SharedHelper;
import com.google.gson.JsonObject;

public class RegistrationCustomerActivity extends AppCompatActivity implements ResponseHandler
{
    private ProgressDialog mProgressDialog;

    String mStrCustomerName;
    String mStrCustomerID;
    String mStrCustomerPassword;
    String mStrPasswordRepeat;
    String mStrVendorID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_customer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCustomerRegistration);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Registration");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final RelativeLayout contentView = (RelativeLayout) findViewById(R.id.root_content_view);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                LinearLayout llCustomerBottomBar = (LinearLayout) findViewById(R.id.ll_customer_registration_bottom_bar);

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    llCustomerBottomBar.setVisibility(View.INVISIBLE);
                }
                else {
                    // keyboard is closed
                    llCustomerBottomBar.setVisibility(View.VISIBLE);
                }
            }
        });

        Button btnCustomerRegister =(Button)findViewById(R.id.btn_customer_register);
        Button btnAddressAddition = (Button) findViewById(R.id.btn_customer_add_address);

        btnCustomerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                boolean result = authenticateCustomerInfo();

                if (result == true)
                {
                    // Create thread to get data from the server.
                    mProgressDialog = new ProgressDialog(RegistrationCustomerActivity.this);
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    /**
                     * An asynchronous task used to save the user information
                     * in the Customer Information Database. The Vendor database is queried
                     * to get the City and Distribution Company Name.
                     */

                    new DownloadFromAmazonDBTask(RegistrationCustomerActivity.this, "CustomerApp/SetCustomerInfo.php", mProgressDialog).execute(jsonBuildInfo());
                }
            }
        });

        btnAddressAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                boolean result = authenticateCustomerInfo();

                if (result == true) {
                    // Call the address addition activity
                    Intent intentCustomerAddressRegistration = new Intent(RegistrationCustomerActivity.this, CustomerAddressAddition.class);
                    intentCustomerAddressRegistration.putExtra("CustomerName", mStrCustomerName);
                    intentCustomerAddressRegistration.putExtra("CustomerID", mStrCustomerID);
                    intentCustomerAddressRegistration.putExtra("CustomerPassword", mStrCustomerPassword);
                    intentCustomerAddressRegistration.putExtra("VendorID", mStrVendorID);
                    startActivity(intentCustomerAddressRegistration);
                }
            }
        });

    }

    private boolean authenticateCustomerInfo()
    {
        EditText edtCustomerNameView;
        EditText edtCustomerIDView;
        EditText edtPasswordView;
        EditText edtPasswordRepeatView;

        edtCustomerNameView = (EditText) findViewById(R.id.editCustomerName);
        edtCustomerIDView = (EditText) findViewById(R.id.editCustomerID);
        edtPasswordView = (EditText) findViewById(R.id.editCustomerPassword);
        edtPasswordRepeatView = (EditText) findViewById(R.id.editPasswordRepeat);

        mStrCustomerName = edtCustomerNameView.getText().toString();
        mStrCustomerID = edtCustomerIDView.getText().toString();
        mStrCustomerPassword = edtPasswordView.getText().toString();
        mStrPasswordRepeat = edtPasswordRepeatView.getText().toString();


        boolean bAuthenticateOK = true;
        View focusView = null;

        // Check to see that the the Customer Name is not empty.
        if (TextUtils.isEmpty(mStrCustomerName))
        {
            edtCustomerNameView.setError(getString(R.string.error_field_required));
            focusView = edtCustomerNameView;
            bAuthenticateOK = false;
        }

        if (TextUtils.isEmpty(mStrCustomerID))
        {
            edtCustomerNameView.setError(getString(R.string.error_field_required));
            focusView = edtCustomerNameView;
            bAuthenticateOK = false;
        }

        // Check for a valid Customer ID (Mobile Number)
        if (!isMobileIDValid(mStrCustomerID))
        {
            edtCustomerIDView.setError(getString(R.string.error_invalid_mobile));
            focusView = edtCustomerIDView;
            bAuthenticateOK = false;
        }

        // Check for a valid Customer Password
        if (!TextUtils.isEmpty(mStrCustomerPassword) && !isPasswordValid(mStrCustomerPassword))
        {
            edtPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = edtPasswordView;
            bAuthenticateOK = false;
        }

        // Check if the confirm password is same as the original password
        if ((mStrPasswordRepeat.equals(mStrCustomerPassword)) == false)
        {
            edtPasswordRepeatView.setError(getString(R.string.error_password_mismatch));
            focusView = edtPasswordRepeatView;
            bAuthenticateOK = false;
        }

        if (bAuthenticateOK == false)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        return bAuthenticateOK;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isMobileIDValid(String mobileNo) {
        //TODO: Replace this with your own logic
        return mobileNo.length() == 10;
    }

    private JsonObject jsonBuildInfo()
    {
        JsonObject jsonCustomerObject = new JsonObject();

        jsonCustomerObject.addProperty("CustomerName", mStrCustomerName);
        jsonCustomerObject.addProperty("CustomerID", mStrCustomerID);
        jsonCustomerObject.addProperty("CustomerPassword", mStrCustomerPassword);

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

        /* Sucessfully recieved the Vendor ID */
        mStrVendorID = strResponse;

        return Constant.JSON_SUCCESS;
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {
            /* The registration process has been a success. We will store important customer
              related information that is going to be used throughout the app in the profile
              database.
            */

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString(getString(R.string.vendor_id), mStrVendorID);
            editor.commit();

            editor.putString(getString(R.string.customer_id),mStrCustomerID);
            editor.commit();

            editor.putString(getString(R.string.customer_password),mStrCustomerPassword);
            editor.commit();

            Intent intent = new Intent(RegistrationCustomerActivity.this,CustomerLogin.class);
            SharedHelper.showAlertDialog(RegistrationCustomerActivity.this, "You have been successfully registered.", intent);
        }
        else
        {
            SharedHelper.showAlertDialog(RegistrationCustomerActivity.this, "There is a prolem in registration.Please try again",null);
        }
    }
}