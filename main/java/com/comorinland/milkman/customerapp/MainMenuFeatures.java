package com.comorinland.milkman.customerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.comorinland.milkman.R;

class CustomerMenuAdapter extends BaseAdapter
{
    public static final int FEATURE_ID_DAILY_DELIVERY = 0;
    public static final int FEATURE_ID_CANCEL = 1;
    public static final int FEATURE_ID_MONTHLY_DELIVERY = 2;
    public static final int FEATURE_ID_BILLING = 3;

    private LayoutInflater mInflater;
    private Context mContext;
    private static int[] arrayCustomerMenuId = new int[]{R.drawable.customer_daily_delivery,R.drawable.customer_cancel, R.drawable.customer_monthly_delivery, R.drawable.customer_billing};

    public CustomerMenuAdapter(Context context)
    {
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 1
    @Override
    public int getCount()
    {
        return arrayCustomerMenuId.length;
    }

    // 2
    @Override
    public Object getItem(int i)
    {
        Integer obj = arrayCustomerMenuId[i];
        return obj;
    }

    // 3
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    // 4
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.vendor_list_menu_item, parent, false);

        ImageView imgView = (ImageView)rowView.findViewById(R.id.imgVendorMenuItem);
        Integer iInteger = (Integer)getItem(position);

        imgView.setImageDrawable(ContextCompat.getDrawable(mContext,iInteger.intValue()));
        return rowView;
    }
}

public class MainMenuFeatures extends AppCompatActivity
{
    ListView mListCustomerMenu;
    CustomerMenuAdapter mCustomerMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feature);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCustomerMenuContent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Customer Menu");

        mListCustomerMenu = (ListView)findViewById(R.id.lst_customer_menu_feature);

        mCustomerMenuAdapter = new CustomerMenuAdapter(this);
        mListCustomerMenu.setAdapter(mCustomerMenuAdapter);
        mListCustomerMenu.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if ((int)mCustomerMenuAdapter.getItem(position) == R.drawable.customer_daily_delivery)
                {
                    Intent intentDailyDelivery = new Intent(MainMenuFeatures.this,ModifyDelivery.class);
                    startActivity(intentDailyDelivery);
                }
                if ((int)mCustomerMenuAdapter.getItem(position) == R.drawable.customer_cancel)
                {
                    Intent intentCancelDelivery = new Intent(MainMenuFeatures.this,ConfirmCancelDelivery.class);
                    startActivity(intentCancelDelivery);
                }
                if ((int)mCustomerMenuAdapter.getItem(position) == R.drawable.customer_monthly_delivery)
                {
                    Intent intentMonthlyDelivery = new Intent(MainMenuFeatures.this, ChangeDefaultDelivery.class);
                    startActivity(intentMonthlyDelivery);
                }
                if ((int)mCustomerMenuAdapter.getItem(position) == R.drawable.customer_billing)
                {
                    Intent intentBillInfoScreen = new Intent(MainMenuFeatures.this, BillingInformation.class);
                    startActivity(intentBillInfoScreen);
                }
            }
        });

    }
}
