package com.comorinland.milkman.vendorapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.comorinland.milkman.R;

import java.util.ArrayList;

interface PlaceOrderListener
{
    public void HandlePlaceOrderClick(ArrayList<CustomerInfoDate> e);
};

interface  CancelDeliveryListener
{
    public void HandleCancelDeliveryClick(ArrayList<CustomerCancelInformation> e);
}

interface PlaceDefaultOrderListener
{
    public void HandlePlaceDefaultOrderClick(ArrayList<CustomerInfoDate> e);
}

public class VendorApproveDelivery extends AppCompatActivity implements PlaceOrderListener,CancelDeliveryListener,PlaceDefaultOrderListener
{
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private VendorPagerAdapter mVendorPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_approve_delivery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarVendorApproveDelivery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mVendorPagerAdapter = new VendorPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpagerVendor);
        mViewPager.setAdapter(mVendorPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabApproveDelivery);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void HandlePlaceOrderClick(ArrayList<CustomerInfoDate> e)
    {
        String strScreenType = "MilkDelivery";
        Intent intentVendorConfirmApproval = new Intent(VendorApproveDelivery.this, VendorConfirmApproval.class);
        intentVendorConfirmApproval.putExtra("ScreenType", strScreenType);
        intentVendorConfirmApproval.putExtra("Customer Info",e);
        startActivity(intentVendorConfirmApproval);
    }

    public void HandleCancelDeliveryClick(ArrayList<CustomerCancelInformation> e)
    {
        String strScreenType = "CancelDelivery";
        Intent intentVendorConfirmApproval = new Intent(VendorApproveDelivery.this, VendorConfirmApproval.class);
        intentVendorConfirmApproval.putExtra("ScreenType", strScreenType);
        intentVendorConfirmApproval.putExtra("Customer Info",e);
        startActivity(intentVendorConfirmApproval);
    }

    public void HandlePlaceDefaultOrderClick(ArrayList<CustomerInfoDate> e)
    {
        String strScreenType = "DefaultDelivery";
        Intent intentVendorConfirmApproval = new Intent(VendorApproveDelivery.this,VendorConfirmApproval.class);
        intentVendorConfirmApproval.putExtra("ScreenType", strScreenType);
        intentVendorConfirmApproval.putExtra("Customer Info",e);
        startActivity(intentVendorConfirmApproval);
    }
}

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

class VendorPagerAdapter extends FragmentPagerAdapter
{
    public VendorPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment frag = null;

        switch (position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            case 0:
                frag = VendorFragmentApproveDelivery.newInstance();
                break;

            case 1:
                frag = VendorFragmentApproveCancelDelivery.newInstance();
                break;

            case 2:
                frag = VendorFragmentApproveDefaultDelivery.newInstance();
                break;
        }

        return frag;
    }

    @Override
    public int getCount()
    {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Approve Delivery";
            case 1:
                return "Approve Cancel Delivery";
            case 2:
                return "Approve Default Delivery";
        }
        return null;
    }
}