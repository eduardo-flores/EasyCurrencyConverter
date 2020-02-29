package com.flores.easycurrencyconverter.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.flores.easycurrencyconverter.data.database.RoomDatabase;
import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.Symbols;
import com.flores.easycurrencyconverter.data.model.SymbolsAPI;
import com.flores.easycurrencyconverter.data.webservice.NetworkDataSource;

import java.util.List;

public class Repository {
    private static final String LOG_TAG = Repository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static Repository sInstance;
    private final NetworkDataSource mNetworkDataSource;
    private final RoomDatabase mRoomDatabase;

    private Repository(NetworkDataSource networkDataSource, RoomDatabase roomDatabase) {
        mNetworkDataSource = networkDataSource;
        mRoomDatabase = roomDatabase;

    }

    public synchronized static Repository getInstance(Application application) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                Context context = application.getApplicationContext();
                sInstance = new Repository(
                        NetworkDataSource.getInstance(context)
                        , RoomDatabase.getDatabase(context));
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    public LiveData<Converter> getCurrency() {
        return mNetworkDataSource.getCurrency();
    }

    public void fetchCurrency() {
        mNetworkDataSource.fetchCurrency();
    }

    public LiveData<List<Symbols>> getSymbols() {
        return mRoomDatabase.symbolsDao().getSymbols();
    }

    public void insertSymbols(Symbols symbols) {
        RoomDatabase.databaseWriteExecutor.execute(() -> mRoomDatabase.symbolsDao().insert(symbols));
    }

    public void deleteAllSymbols() {
        RoomDatabase.databaseWriteExecutor.execute(() -> mRoomDatabase.symbolsDao().deleteAll());
    }

    public LiveData<SymbolsAPI> getSymbolsAPI() {
        return mNetworkDataSource.getSymbolsAPI();
    }

    public void fetchSymbolsAPI() {
        mNetworkDataSource.fetchSymbolsAPI();
    }
}