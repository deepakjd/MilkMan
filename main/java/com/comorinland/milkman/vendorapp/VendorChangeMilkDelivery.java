package com.comorinland.milkman.vendorapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.DateMessageInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

interface CustomerIdFromDatabaseInterface
{
    public void getCustomerIdFromDatabase();
    public void DateMessageListener(int year, int month, int day);
}

public class VendorChangeMilkDelivery extends AppCompatActivity implements CustomerIdFromDatabaseInterface, DateMessageInterface
{
    public static final int CUSTOMER_ID_SELECTED = 2;

    private VendorChangeMilkDeliveryAdapter mVendorPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_change_delivery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarVendorChangeDelivery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Milk Delivery");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mVendorPagerAdapter = new VendorChangeMilkDeliveryAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpagerChangeDelivery);
        mViewPager.setAdapter(mVendorPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabVendorChangeDelivery);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void getCustomerIdFromDatabase()
    {
        Intent intentCustomerIdFromDatabase = new Intent(VendorChangeMilkDelivery.this, VendorCustomerIdFromDatabase.class);
        startActivityForResult(intentCustomerIdFromDatabase,0);
    }

    // Call Back method  to get the Message form other Activity    override the method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        // fetch the message String
        if (resultCode == CUSTOMER_ID_SELECTED)
        {
            String strCustomerID   = data.getStringExtra("CustomerID");
            Fragment frag = mVendorPagerAdapter.getCurrentFragment();
            if (frag instanceof VendorFragmentChangeMilkDelivery)
            {
                ((VendorFragmentChangeMilkDelivery) frag).DisplayCustomerID(strCustomerID);
            }
        }
    }

    @Override
    public void DateMessageListener(int year, int month, int day)
    {
        String strScheduledDeliveryDate;
        String NEW_FORMAT = "dd/MM/yyyy";

        strScheduledDeliveryDate = String.valueOf(day) + '/' + String.valueOf(month) + '/' + String.valueOf(year);
        SimpleDateFormat sdf = new SimpleDateFormat(NEW_FORMAT);

        try
        {
            Date d = sdf.parse(strScheduledDeliveryDate);
            sdf.applyPattern(NEW_FORMAT);
            strScheduledDeliveryDate = sdf.format(d);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        Fragment frag = mVendorPagerAdapter.getCurrentFragment();
        if (frag instanceof VendorFragmentChangeMilkDelivery)
        {
            ((VendorFragmentChangeMilkDelivery) frag).SetChangeDeliveryDate(strScheduledDeliveryDate);
        }
    }
}

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

class VendorChangeMilkDeliveryAdapter extends FragmentPagerAdapter
{
    public VendorChangeMilkDeliveryAdapter(FragmentManager fm)
    {
        super(fm);
    }

    private Fragment mCurrentFragment;

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            case 0:
                return VendorFragmentChangeMilkDelivery.newInstance();

            case 1:
            //  return VendorFragmentChangeCancelDelivery.newInstance();
        }
        return null;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object)
    {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount()
    {
        // Show 3 total pages.
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Change Milk Delivery";
//          case 1:
//                return "Cancel Delivery";
//            case 2:
//                return "Change Default Delivery";
        }
        return null;
    }
}