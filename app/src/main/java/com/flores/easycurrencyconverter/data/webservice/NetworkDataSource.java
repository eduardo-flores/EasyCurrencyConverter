package com.flores.easycurrencyconverter.data.webservice;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.SymbolsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Provides an API for doing all operations with the server data
 */
public class NetworkDataSource {
    private static final String LOG_TAG = NetworkDataSource.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NetworkDataSource sInstance;
//    private final AppWidgetManager appWidgetManager;
//    private final int[] appWidgetIds;

    private final MutableLiveData<Converter> mCurrencyList;
    private final MutableLiveData<SymbolsAPI> mSymbolsAPI;

    private NetworkDataSource(Context context) {
//        appWidgetManager = AppWidgetManager.getInstance(context);
//        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingAppWidget.class));
        mCurrencyList = new MutableLiveData<>();
        mSymbolsAPI = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static NetworkDataSource getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDataSource(context);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<Converter> getCurrency() {
        return mCurrencyList;
    }

    public Converter getCurrencyWidget() {
        return mCurrencyList.getValue();
    }

    public void fetchCurrency(List<String> symbols) {
        Log.d(LOG_TAG, "fetchCurrency started");
        Call<Converter> call = WebserviceClient.getWebservice().getLatest(symbols);
        call.enqueue(new Callback<Converter>() {
            @Override
            public void onResponse(@NonNull Call<Converter> call, @NonNull Response<Converter> response) {
                Log.d(LOG_TAG, "response code = " + response.code());
                if (response.isSuccessful()) {
                    mCurrencyList.postValue(response.body());
                    //Trigger data update to handle the widgets and force a data refresh
//                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_recipe_widget);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Converter> call, @NonNull Throwable t) {
                Log.d(LOG_TAG, t.getMessage() != null ? t.getMessage() : "onFailure");
            }

        });
    }

    public LiveData<SymbolsAPI> getSymbolsAPI() {
        return mSymbolsAPI;
    }

    public void fetchSymbolsAPI() {
        Log.d(LOG_TAG, "fetchSymbolsAPI started");
        Call<SymbolsAPI> call = WebserviceClient.getWebservice().getSymbols();
        call.enqueue(new Callback<SymbolsAPI>() {
            @Override
            public void onResponse(@NonNull Call<SymbolsAPI> call, @NonNull Response<SymbolsAPI> response) {
                Log.d(LOG_TAG, "response code = " + response.code());
                if (response.isSuccessful()) {
                    mSymbolsAPI.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SymbolsAPI> call, @NonNull Throwable t) {
                Log.d(LOG_TAG, t.getMessage() != null ? t.getMessage() : "onFailure");
            }

        });
    }
}