package com.comorinland.milkman.vendorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.comorinland.milkman.R;


public class VendorMenu extends AppCompatActivity
{
    ListView mListVendorMenu;
    VendorMenuAdapter mVendorMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_menu_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMenuContent);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vendor Menu");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mListVendorMenu = (ListView)findViewById(R.id.lst_vendor_menu_feature);
        mVendorMenuAdapter = new VendorMenuAdapter(this);
        mListVendorMenu.setAdapter(mVendorMenuAdapter);

        mListVendorMenu.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if ((int)mVendorMenuAdapter.getItem(position) == R.drawable.vendor_add_customer)
                {
                    Intent intentAddCustomer = new Intent(VendorMenu.this,VendorCustomerAddition.class);
                    startActivity(intentAddCustomer);
                }

                if ((int)mVendorMenuAdapter.getItem(position) == R.drawable.vendor_change_delivery)
                {
                    Intent intentChangeDelivery = new Intent(VendorMenu.this,VendorChangeMilkDelivery.class);
                    startActivity(intentChangeDelivery);
                }

                if ((int)mVendorMenuAdapter.getItem(position) == R.drawable.vendor_approve_delivery)
                {
                    Intent intentApproveDeliveryScreen = new Intent(VendorMenu.this, VendorApproveDelivery.class);
                    startActivity(intentApproveDeliveryScreen);
                }

                if ((int)mVendorMenuAdapter.getItem(position) == R.drawable.vendor_billing_information)
                {
                    Intent intentBillInfoScreen = new Intent(VendorMenu.this, VendorBillingInfoDisplay.class);
                    startActivity(intentBillInfoScreen);
                }

            }
        });
    }
}
