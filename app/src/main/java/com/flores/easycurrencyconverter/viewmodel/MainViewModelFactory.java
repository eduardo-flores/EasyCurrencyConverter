package com.flores.easycurrencyconverter.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.flores.easycurrencyconverter.data.Repository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Repository mRepository;

    public MainViewModelFactory(Repository repository) {
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}