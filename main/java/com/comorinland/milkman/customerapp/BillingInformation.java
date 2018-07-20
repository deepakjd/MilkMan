/************************************************************************************************
 File Name   : BillingInformation.java
 Purpose     : This displays the customer_billing information of a customer. It can be displayed either
 month wise or day wise. The default monthly_delivery information of the customer is also
 displayed.
 Author      : Deepak J. Daniel
 @Copyright Grace Samadhanam Development Company.
 *************************************************************************************************
 */

package com.comorinland.milkman.customerapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.DateMessageInterface;

import java.util.ArrayList;

public class BillingInformation extends AppCompatActivity implements DateMessageInterface
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_billing_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBillingInfo);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Fragment fragmentBillingInfo = new BillingInfoMonthlyFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_billinfo_container, fragmentBillingInfo, "FRAGMENT_MONTHLY");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_billing_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.menu_daily_item)
        {
            BillingInfoMonthlyFragment myFragment = (BillingInfoMonthlyFragment) getFragmentManager().findFragmentByTag("FRAGMENT_MONTHLY");
            if (myFragment != null && myFragment.isVisible())
            {
                Fragment fragmentBillingInfo = new BillingInfoDailyFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_billinfo_container, fragmentBillingInfo, "FRAGMENT_DAILY");
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }

        if (id == R.id.menu_monthly_item)
        {
            BillingInfoDailyFragment myFragment = (BillingInfoDailyFragment) getFragmentManager().findFragmentByTag("FRAGMENT_DAILY");
            if (myFragment != null && myFragment.isVisible())
            {
                Fragment fragmentBillingInfo = new BillingInfoMonthlyFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_billinfo_container, fragmentBillingInfo, "FRAGMENT_MONTHLY");
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void DateMessageListener(int year, int month, int day)
    {

        BillingInfoDailyFragment fragmentDaily = (BillingInfoDailyFragment) getFragmentManager().findFragmentByTag("FRAGMENT_DAILY");
        fragmentDaily.setRequestedBillDate(year, month, day);
    }
}