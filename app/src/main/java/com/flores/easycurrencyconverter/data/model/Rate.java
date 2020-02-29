package com.flores.easycurrencyconverter.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rates")
public class Rate {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "value")
    private Double value;

    @ColumnInfo(name = "base")
    private boolean base;

    public Rate(@NonNull String code, Double value, boolean base) {
        this.code = code;
        this.value = value;
        this.base = base;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }
}
