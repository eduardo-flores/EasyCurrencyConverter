package com.flores.easycurrencyconverter.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.flores.easycurrencyconverter.data.Repository;
import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.Symbols;
import com.flores.easycurrencyconverter.data.model.SymbolsAPI;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private final Repository mRepository;
    private final LiveData<Converter> mCurrency;
    private final LiveData<List<Symbols>> mSymbols;
    private final LiveData<SymbolsAPI> mSymbolsAPI;

    MainActivityViewModel(Repository repository) {
        mRepository = repository;
        mCurrency = mRepository.getCurrency();
        mSymbols = mRepository.getSymbols();
        mSymbolsAPI = mRepository.getSymbolsAPI();
        mRepository.fetchCurrency();
    }

    public LiveData<Converter> getCurrency() {
        return mCurrency;
    }

    public LiveData<List<Symbols>> getSymbols() {
        return mSymbols;
    }

    public void insertSymbols(Symbols symbols) {
        mRepository.insertSymbols(symbols);
    }

    public LiveData<SymbolsAPI> getSymbolsAPI() {
        return mSymbolsAPI;
    }

    public void fetchSymbolsAPI() {
        mRepository.fetchSymbolsAPI();
    }
}
