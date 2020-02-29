package com.flores.easycurrencyconverter.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.flores.easycurrencyconverter.data.model.Rate;

import java.util.List;

@Dao
public interface RateDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from rates ORDER BY code ASC")
    LiveData<List<Rate>> getRate();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Rate rate);

    @Query("DELETE FROM rates")
    void deleteAll();
}
