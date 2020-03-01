package com.flores.easycurrencyconverter.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.flores.easycurrencyconverter.data.Repository;
import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.Rate;
import com.flores.easycurrencyconverter.data.model.Symbol;
import com.flores.easycurrencyconverter.data.model.SymbolsAPI;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private static final String LOG_TAG = MainActivityViewModel.class.getSimpleName();
    public static final String EUR_CODE = "EUR";
    private final Repository mRepository;
    private final LiveData<Converter> mCurrency;
    private final LiveData<List<Symbol>> mSymbols;
    private final LiveData<List<Rate>> mRates;
    private final LiveData<SymbolsAPI> mSymbolsAPI;
    private LiveData<Rate> mBaseRate;

    MainActivityViewModel(Repository repository) {
        mRepository = repository;
        mCurrency = mRepository.getCurrency();
        mSymbols = mRepository.getSymbols();
        mRates = mRepository.getRates();
        mBaseRate = mRepository.getBaseRate();
        mSymbolsAPI = mRepository.getSymbolsAPI();
    }

    public LiveData<Converter> getCurrency() {
        return mCurrency;
    }

    public LiveData<List<Symbol>> getSymbols() {
        return mSymbols;
    }

    public void insertSymbol(Symbol symbol) {
        mRepository.insertSymbol(symbol);
    }

    public LiveData<SymbolsAPI> getSymbolsAPI() {
        return mSymbolsAPI;
    }

    public void deleteAllSymbols() {
        mRepository.deleteAllSymbols();
    }

    public void fetchCurrency(List<String> symbols) {
        mRepository.fetchCurrency(symbols);
    }

    public void fetchSymbolsAPI() {
        mRepository.fetchSymbolsAPI();
    }

    public LiveData<List<Rate>> getRates() {
        return mRates;
    }

    public LiveData<Rate> getBaseRate() {
        return mBaseRate;
    }

    public void insertRate(Rate rate) {
        Log.d(LOG_TAG, "insertRate " + rate);
        mRepository.insertRate(rate);
    }

    public void deleteAllRates() {
        mRepository.deleteAllRates();
    }

    public LiveData<Boolean> isFavorite() {
        return mRepository.isFavorite();
    }

    public void setFavorite() {
        mRepository.setFavorite();
    }

    public Rate getDefaultRate() {
        return new Rate(EUR_CODE, 1D, true, false);
    }

    public void updateRates(List<Rate> rates) {
        mRepository.updateRates(rates);
    }
}
