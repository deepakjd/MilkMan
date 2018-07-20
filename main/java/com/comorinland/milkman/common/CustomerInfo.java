package com.comorinland.milkman.common;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by deepak on 28/2/18.
 */
@Entity
public class CustomerInfo
{
    @PrimaryKey @NonNull
    public final String CustomerID;
    public final String CustomerName;

    public CustomerInfo(String CustomerID, String CustomerName)
    {
        this.CustomerID = CustomerID;
        this.CustomerName = CustomerName;
    }
}