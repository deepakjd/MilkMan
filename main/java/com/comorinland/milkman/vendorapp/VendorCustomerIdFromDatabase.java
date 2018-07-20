package com.comorinland.milkman.vendorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.CustomerDatabaseAsync;
import com.comorinland.milkman.common.CustomerInfo;
import com.comorinland.milkman.common.DataHandler;

import java.util.ArrayList;
import java.util.List;

public class VendorCustomerIdFromDatabase extends AppCompatActivity implements DataHandler
{
    public static final int CUSTOMER_ID_NOT_AVAILABLE = 0;
    public static final int CUSTOMER_ID_NOT_SELECTED = 1;
    public static final int CUSTOMER_ID_SELECTED = 2;

    ArrayList<String> arrCustomerId = new ArrayList<String>();
    ArrayList<String> arrCustomerName = new ArrayList<String>();
    ArrayList<String> arrSortedCustomerId = new ArrayList<String>();
    ArrayList<String> arrSortedCustomerName = new ArrayList<>();

    ListView mListView = null;
    ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_customer_id_from_database);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarVendorCustomerId);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final EditText edtTextCustomerID = (EditText)findViewById(R.id.searchCustomerId);
        final EditText edtTextCustomerName = (EditText)findViewById(R.id.searchCustomerName);

        edtTextCustomerID.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                // Do something after Text Change
                if (s.length() >= 1)
                {
                    edtTextCustomerID.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_add,0);
                    arrSortedCustomerId.clear();

                    for(String str : arrCustomerId){
                        if(str.startsWith(s.toString()))
                        {
                            arrSortedCustomerId.add(str);
                        }
                    }
                    itemsAdapter.notifyDataSetChanged();
                }
                else
                {
                    edtTextCustomerID.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // Do something before Text Change
            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // Do something while Text Change
            }

        });

        edtTextCustomerID.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    if(event.getRawX() >= edtTextCustomerID.getRight() - edtTextCustomerID.getTotalPaddingRight())
                    {
                        // your action for drawable click event
                        edtTextCustomerID.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        edtTextCustomerID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bHasFocus)
            {
                if (bHasFocus)
                {
                    arrSortedCustomerId.clear();
                    arrSortedCustomerId.addAll(arrCustomerId);
                    itemsAdapter = new ArrayAdapter<String>(VendorCustomerIdFromDatabase.this, R.layout.list_element, arrSortedCustomerId);
                    mListView.setAdapter(itemsAdapter);
                }
            }
        });

        edtTextCustomerName.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                // Do something after Text Change
                if (s.length() >= 1)
                {
                    edtTextCustomerName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_add,0);
                    arrSortedCustomerName.clear();
                    for(String str : arrCustomerName){
                        if(str.startsWith(s.toString()))
                        {
                            arrSortedCustomerName.add(str);
                        }
                    }
                    itemsAdapter.notifyDataSetChanged();
                }
                else
                {
                    edtTextCustomerName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // Do something before Text Change
            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // Do something while Text Change
            }
        });

        edtTextCustomerName.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    if(event.getRawX() >= edtTextCustomerName.getRight() - edtTextCustomerName.getTotalPaddingRight())
                    {
                        // your action for drawable click event
                        edtTextCustomerName.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        edtTextCustomerName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bHasFocus)
            {
                if (bHasFocus)
                {
                    arrSortedCustomerName.addAll(arrCustomerName);
                    itemsAdapter = new ArrayAdapter<String>(VendorCustomerIdFromDatabase.this, R.layout.list_element, arrSortedCustomerName);
                    mListView.setAdapter(itemsAdapter);
                }
            }
        });

        mListView = (ListView)findViewById(R.id.lst_customer_info);

        new CustomerDatabaseAsync(this,getApplicationContext()).execute();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Intent intent = new Intent();
                String strCustomerIdPlusName = (String)adapterView.getItemAtPosition(position).toString();
                String strNameID[] = strCustomerIdPlusName.split("-");
                if (Character.isLetter(strNameID[0].charAt(0)))
                {
                    intent.putExtra("CustomerID",strNameID[1]);
                }
                else
                {
                    intent.putExtra("CustomerID", strNameID[0]);
                }
                setResult(CUSTOMER_ID_SELECTED,intent);
                finish();
            }
        });
    }

    public void SaveCustomerInfo(List<CustomerInfo> lstCustomerInfo)
    {
        for(int i = 0; i < lstCustomerInfo.size();i++)
        {
            arrCustomerId.add(lstCustomerInfo.get(i).CustomerID + '-' + lstCustomerInfo.get(i).CustomerName);
            arrCustomerName.add(lstCustomerInfo.get(i).CustomerName + '-' + lstCustomerInfo.get(i).CustomerID);
        }

        arrSortedCustomerId.clear();
        arrSortedCustomerId.addAll(arrCustomerId);

        itemsAdapter = new ArrayAdapter<String>(this, R.layout.list_element, arrSortedCustomerId);
        itemsAdapter.notifyDataSetChanged();

        mListView.setAdapter(itemsAdapter);
    }
}