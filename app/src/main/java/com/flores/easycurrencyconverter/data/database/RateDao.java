package com.flores.easycurrencyconverter.data.database;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.flores.easycurrencyconverter.data.model.Rate;

import java.util.List;

@Dao
public interface RateDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from rates where base = 0 ORDER BY code ASC")
    LiveData<List<Rate>> getRates();

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from rates where base = 1 LIMIT 1")
    LiveData<Rate> getBaseRate();

    @Query("SELECT count(favorite) from rates where favorite = 1 LIMIT 1")
    LiveData<Boolean> isFavorite();

    @Query("SELECT count(favorite) from rates where favorite = 1 LIMIT 1")
    Boolean isFav();

    @Query("UPDATE rates SET favorite = :newFavorite ")
    void updateFavorite(int newFavorite);

    @Update
    void update(List<Rate> rates);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Rate rate);

    @Query("DELETE FROM rates")
    void deleteAll();

    /**
     * Select rates for content provider
     *
     * @return A {@link Cursor} of rates to share with content provider .
     */
    @Query("SELECT * FROM rates where favorite = 1")
    Cursor getRatesProvider();
}
