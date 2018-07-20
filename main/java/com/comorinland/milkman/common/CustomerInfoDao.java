package com.comorinland.milkman.common;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by deepak on 28/2/18.
 */

@Dao
public interface CustomerInfoDao
{
    @Query("SELECT CustomerName FROM CustomerInfo WHERE CustomerID=:CustomerID")
    String getCustomerName(String CustomerID);

    @Query("SELECT * FROM CustomerInfo")
    List<CustomerInfo> getCustomerInfo();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCustomerInfo(CustomerInfo customerInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCustomerInfo(List<CustomerInfo> customerInfos);
}