package com.flores.easycurrencyconverter.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "symbols")
public class Symbol {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "name")
    private String name;

    public Symbol(@NonNull String code, String name) {
        this.code = code;
        this.name = name;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
