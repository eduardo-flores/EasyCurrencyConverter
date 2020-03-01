package com.flores.easycurrencyconverter.data.webservice;

import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.SymbolsAPI;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Webservice {

    @GET("api/latest")
    Call<Converter> getLatest(@Query("symbols") String symbols);

    @GET("api/symbols")
    Call<SymbolsAPI> getSymbols();

}
