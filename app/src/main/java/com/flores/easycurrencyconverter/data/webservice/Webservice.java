package com.flores.easycurrencyconverter.data.webservice;

import com.flores.easycurrencyconverter.data.model.Converter;
import com.flores.easycurrencyconverter.data.model.SymbolsAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Webservice {

    @GET("api/latest")
    Call<Converter> getLatest(@Query("symbols") List<String> symbols);

    @GET("api/symbols")
    Call<SymbolsAPI> getSymbols();

}
