package com.flores.easycurrencyconverter.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.flores.easycurrencyconverter.data.Repository;
import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.Rate;
import com.flores.easycurrencyconverter.data.model.Symbol;
import com.flores.easycurrencyconverter.data.model.SymbolsAPI;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    public static final String EUR_CODE = "EUR";
    private final Repository mRepository;
    private final LiveData<Converter> mCurrency;
    private final LiveData<List<Symbol>> mSymbols;
    private final LiveData<List<Rate>> mRates;
    private final LiveData<SymbolsAPI> mSymbolsAPI;

    MainActivityViewModel(Repository repository) {
        mRepository = repository;
        mCurrency = mRepository.getCurrency();
        mSymbols = mRepository.getSymbols();
        mRates = mRepository.getRates();
        mSymbolsAPI = mRepository.getSymbolsAPI();
        mRepository.fetchCurrency();
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

    public void fetchSymbolsAPI() {
        mRepository.fetchSymbolsAPI();
    }

    public LiveData<List<Rate>> getRates() {
        return mRates;
    }

    public void insertRate(Rate rate) {
        mRepository.insertRate(rate);
    }

    public void deleteAllRates() {
        mRepository.deleteAllRates();
    }
}
