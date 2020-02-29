package com.flores.easycurrencyconverter.data.webservice;

import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.SymbolsAPI;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Webservice {

    @GET("api/latest")
    Call<Converter> getLatest();

    @GET("api/symbols")
    Call<SymbolsAPI> getSymbols();

}
