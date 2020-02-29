package com.flores.easycurrencyconverter.data.database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.flores.easycurrencyconverter.data.model.Rate;
import com.flores.easycurrencyconverter.data.model.Symbol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Symbol.class, Rate.class}, version = 1, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {

    public abstract SymbolDao symbolsDao();

    public abstract RateDao rateDao();

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile RoomDatabase INSTANCE;

    public static RoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDatabase.class, "database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
