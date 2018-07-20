package com.comorinland.milkman.common;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by deepak on 4/4/18.
 */

public class CustomerDatabaseAsync extends AsyncTask<Void, Void, List<CustomerInfo>>
{
    public DataHandler m_DataHandler = null;
    public Context m_Context = null;

    public CustomerDatabaseAsync(DataHandler callback, Context context)
    {
        m_DataHandler = callback;
        m_Context = context;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        //Perform pre-adding operation here.
    }

    @Override
    protected List<CustomerInfo> doInBackground(Void... voids)
    {
        return CustomerInfoDatabase.getInstance(m_Context).customerInfoDao().getCustomerInfo();
    }

    @Override
    protected void onPostExecute(List<CustomerInfo> lstCustomerInfo)
    {
        super.onPostExecute(lstCustomerInfo);

        //To after addition operation here.
        m_DataHandler.SaveCustomerInfo(lstCustomerInfo);
    }
}