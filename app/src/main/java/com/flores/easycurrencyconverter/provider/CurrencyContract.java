package com.flores.easycurrencyconverter.provider;


import android.net.Uri;
import android.provider.BaseColumns;

public class CurrencyContract {

    // The authority, which is how your code knows which Content Provider to access
    static final String AUTHORITY = "com.flores.easycurrencyconverter.provider.quick_currency_provider_app";
    // Define the possible paths for accessing data in this contract
    // This is the path for the "ingredients" directory
    static final String PATH_RATES = "rates";
    // The base content URI = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class RateEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATES).build();
        public static final String COLUMN_CURRENCY_NAME = "code";
        public static final String COLUMN_CURRENCY_VALUE = "value";
        static final String TABLE_NAME = "rates";
    }
}
