package com.flores.easycurrencyconverter.data.model;

import java.io.Serializable;
import java.util.Map;

public class SymbolsAPI implements Serializable {

    private Boolean success;
    private Map<String, String> symbols;
    private ResponseError error;

    public ResponseError getError() {
        return error;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }


    public Map<String, String> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, String> symbols) {
        this.symbols = symbols;
    }
}
