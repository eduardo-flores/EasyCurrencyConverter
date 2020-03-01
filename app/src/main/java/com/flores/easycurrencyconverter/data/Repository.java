package com.flores.easycurrencyconverter.data;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.flores.easycurrencyconverter.R;
import com.flores.easycurrencyconverter.data.database.RoomDatabase;
import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.Rate;
import com.flores.easycurrencyconverter.data.model.Symbol;
import com.flores.easycurrencyconverter.data.model.SymbolsAPI;
import com.flores.easycurrencyconverter.data.webservice.NetworkDataSource;
import com.flores.easycurrencyconverter.widget.CurrencyAppWidget;

import java.util.List;

public class Repository {
    private static final String LOG_TAG = Repository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static Repository sInstance;
    private final NetworkDataSource mNetworkDataSource;
    private final RoomDatabase mRoomDatabase;
    private final AppWidgetManager mAppWidgetManager;
    private final int[] mAppWidgetIds;


    private Repository(NetworkDataSource networkDataSource, RoomDatabase roomDatabase, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mNetworkDataSource = networkDataSource;
        mRoomDatabase = roomDatabase;
        mAppWidgetManager = appWidgetManager;
        mAppWidgetIds = appWidgetIds;
    }

    public synchronized static Repository getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Repository(
                        NetworkDataSource.getInstance(context)
                        , RoomDatabase.getDatabase(context)
                        , AppWidgetManager.getInstance(context)
                        , AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, CurrencyAppWidget.class)));
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    public LiveData<Converter> getCurrency() {
        return mNetworkDataSource.getCurrency();
    }

    public void fetchCurrency(List<String> symbols) {
        mNetworkDataSource.fetchCurrency(symbols);
    }

    public LiveData<List<Symbol>> getSymbols() {
        return mRoomDatabase.symbolsDao().getSymbols();
    }

    public void insertSymbol(Symbol symbol) {
        RoomDatabase.databaseWriteExecutor.execute(() -> mRoomDatabase.symbolsDao().insert(symbol));
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

    public LiveData<List<Rate>> getRates() {
        return mRoomDatabase.rateDao().getRates();
    }

    public LiveData<Rate> getBaseRate() {
        return mRoomDatabase.rateDao().getBaseRate();
    }

    public LiveData<Boolean> isFavorite() {
        return mRoomDatabase.rateDao().isFavorite();
    }

    public void insertRate(Rate rate) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            mRoomDatabase.rateDao().insert(rate);

            //Trigger data update to handle the widgets and force a data refresh
            mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetIds, R.id.lv_widget);
        });
    }

    public void deleteAllRates() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            mRoomDatabase.rateDao().deleteAll();

            //Trigger data update to handle the widgets and force a data refresh
            mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetIds, R.id.lv_widget);
        });
    }

    public void setFavorite() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            Boolean favorite = mRoomDatabase.rateDao().isFav();
            mRoomDatabase.rateDao().updateFavorite(favorite == null ? 0 : favorite ? 0 : 1);

            //Trigger data update to handle the widgets and force a data refresh
            mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetIds, R.id.lv_widget);
        });
    }

    public void updateRates(List<Rate> rates) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            mRoomDatabase.rateDao().update(rates);

            //Trigger data update to handle the widgets and force a data refresh
            mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetIds, R.id.lv_widget);
        });
    }
}