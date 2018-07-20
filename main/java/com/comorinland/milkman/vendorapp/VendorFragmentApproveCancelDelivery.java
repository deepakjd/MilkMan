package com.comorinland.milkman.vendorapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.CustomerDatabaseAsync;
import com.comorinland.milkman.common.CustomerInfo;
import com.comorinland.milkman.common.CustomerInfoDatabase;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.common.DataHandler;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

class CustomerCancelInformation implements Serializable
{
    String CustomerID;
    String CustomerName;
    String CancellationType;
    Date   FromDate;
    Date   ToDate;

    public CustomerCancelInformation(String customerID, String customerName, String cancellationType, Date fromDate, Date toDate)
    {
        CustomerID       = customerID;
        CustomerName     = customerName;
        CancellationType = cancellationType;
        FromDate         = fromDate;
        ToDate           = toDate;
    }

    public CustomerCancelInformation(String customerID, String customerName, String cancellationType)
    {
        CustomerID       = customerID;
        CustomerName     = customerName;
        CancellationType = cancellationType;
    }

    public int getType()
    {
        int type = 0 ;

        if (CancellationType.equals("Monthly"))
        {
            type = 3;
        }
        else if (FromDate.equals(ToDate))
        {
            type = 1;
        }
        else
            type = 2;

        return type;
    }
}

public class VendorFragmentApproveCancelDelivery extends Fragment implements ResponseHandler,DataHandler
{
    private ProgressDialog mProgressDialog;
    ArrayList<CustomerCancelInformation> arrListCustomerCancelInformation = new ArrayList<>();
    ArrayList<CustomerInfo> mlistCustomerInfo = new ArrayList<>();
    ListView               mCancelApprovalListView;
    CancelDeliveryListener mCancelDeliveryListener;

    public VendorFragmentApproveCancelDelivery()
    {
        // Required empty public constructor
    }

    public static VendorFragmentApproveCancelDelivery newInstance()
    {
        VendorFragmentApproveCancelDelivery fragment = new VendorFragmentApproveCancelDelivery();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.vendor_fragment_approve_cancel_delivery, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        int actionBarHeight = 50;
        int marginInPx;

        super.onActivityCreated(savedInstanceState);

        Button btnApproveDelivery = (Button)getActivity().findViewById(R.id.btn_vendor_approve_cancel_delivery);
        mCancelDeliveryListener = (CancelDeliveryListener)getActivity();

        TypedValue tv = new TypedValue();

        if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btnApproveDelivery.getLayoutParams();

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        marginInPx = Math.round( 8 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        lp.setMargins(marginInPx,marginInPx,marginInPx,marginInPx + actionBarHeight);
        btnApproveDelivery.setLayoutParams(lp);

        new CustomerDatabaseAsync(this,getActivity()).execute();

        btnApproveDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VendorCancelListAdapter cancelListAdapter;
                cancelListAdapter = (VendorCancelListAdapter) mCancelApprovalListView.getAdapter();
                if (cancelListAdapter != null) {

                    ArrayList<CustomerCancelInformation> e = cancelListAdapter.getSelectedApprovals();
                    mCancelDeliveryListener.HandleCancelDeliveryClick(e);
                }
            }
        });
    }

    private JsonObject jsonBuildInfo()
    {
        JsonObject jsonVendorObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String strVendorId = sharedPref.getString(getString(R.string.vendor_id), null);

        jsonVendorObject.addProperty("VendorID", strVendorId);
        return jsonVendorObject;
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
            JSONArray responseArray = new JSONArray(strResponse);

            for (int i = 0; i < responseArray.length(); i++)
            {
                JSONObject approveDeliveryObject = responseArray.getJSONObject(i);

                String strCustomerID = approveDeliveryObject.getString("CustomerID");

                String strCustomerName = getCustomerNameFromId(strCustomerID);

                String strCancellationType = approveDeliveryObject.getString("CancellationType");

                if (strCancellationType.equals("Monthly"))
                {
                    CustomerCancelInformation e = new CustomerCancelInformation(strCustomerID, strCustomerName, strCancellationType);
                    arrListCustomerCancelInformation.add(e);
                    continue;
                }

                String strFromDate = approveDeliveryObject.getString("FromDate");
                String strToDate   = approveDeliveryObject.getString("ToDate");

                if (strFromDate.isEmpty() || strFromDate.equals(null))
                {
                    continue;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date dateCancelFrom, dateCancelTo = null;

                try
                {
                     dateCancelFrom = sdf.parse(strFromDate);
                     dateCancelTo   = sdf.parse(strToDate);
                }
                catch (ParseException e)
                {
                    return Constant.JSON_EXCEPTION;
                }

                CustomerCancelInformation e = new CustomerCancelInformation(strCustomerID, strCustomerName, strCancellationType,dateCancelFrom,dateCancelTo);
                arrListCustomerCancelInformation.add(e);
            }
        }
        catch (JSONException e)
        {
            return Constant.JSON_EXCEPTION;
        }
        return Constant.JSON_SUCCESS;
    }

    public void SaveCustomerInfo(List<CustomerInfo> lstCustomerInfo)
    {
        mlistCustomerInfo.addAll(lstCustomerInfo);

        // Create thread to get data from the server.
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        new DownloadFromAmazonDBTask(this, "VendorApp/GetWaitingCancelApprovals.php", mProgressDialog).execute(jsonBuildInfo());
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
                strCustomerName = xCustomerInfo.CustomerName;
            else
                strCustomerName = "Name Unknown";
        }
        return strCustomerName;
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {
            VendorCancelListAdapter cancelListAdapter;

            mCancelApprovalListView = (ListView) getActivity().findViewById(R.id.lst_approve_cancel_delivery);

            cancelListAdapter = new VendorCancelListAdapter(getActivity(), arrListCustomerCancelInformation);

            mCancelApprovalListView.setAdapter(cancelListAdapter);
        }
    }
}