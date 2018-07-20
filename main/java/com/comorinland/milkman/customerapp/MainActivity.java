package com.comorinland.milkman.customerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.vendorapp.VendorLogin;


public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);


        ImageView imgCustomerScreen = (ImageView) findViewById(R.id.imageviewCustomer);

        imgCustomerScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentRegisterCustomer = new Intent(MainActivity.this, CustomerLogin.class);
                startActivity(intentRegisterCustomer);
            }
        });

        ImageView imgVendorScreen = (ImageView) findViewById(R.id.imageviewVendor);

        imgVendorScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                              Intent intentVendorLogin = new Intent(MainActivity.this, VendorLogin.class);
                              startActivity(intentVendorLogin);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        // No inspection Simplifiable if statement
        if (id == R.id.action_settings)
        {
            DialogFragment dialogFragmentAboutScreen = new DialogAboutScreenFragment();
            dialogFragmentAboutScreen.show(getSupportFragmentManager(), "about screen");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}