package com.flores.easycurrencyconverter.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.flores.easycurrencyconverter.R;
import com.flores.easycurrencyconverter.provider.CurrencyContract;
import com.flores.easycurrencyconverter.util.ResourcesUtil;


public class ListWidgetService extends RemoteViewsService {

    private static final String LOG_TAG = ListWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(LOG_TAG, "onGetViewFactory");

        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    private class ListRemoteViewsFactory implements RemoteViewsFactory {
        private final String LOG_TAG = "ListRemoteViewsFactory";
        private final Context mContext;
        private Cursor mCursor;

        ListRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        public void onCreate() {
        }


        @Override
        public void onDataSetChanged() {
            if (mCursor != null) mCursor.close();

            mCursor = getContentResolver().query(CurrencyContract.RateEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    CurrencyContract.RateEntry._ID);
            Log.d(LOG_TAG, "onDataSetChanged: mCursor " + mCursor);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor != null ? mCursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            Log.d(LOG_TAG, "getViewAt: " + i);

            if (mCursor == null || mCursor.getCount() == 0) {
                return null;
            }
            mCursor.moveToPosition(i);
            int currencyNameIndex = mCursor.getColumnIndex(CurrencyContract.RateEntry.COLUMN_CURRENCY_NAME);
            int currencyValueIndex = mCursor.getColumnIndex(CurrencyContract.RateEntry.COLUMN_CURRENCY_VALUE);

            String currencyName = mCursor.getString(currencyNameIndex);
            String currencyValue = String.format("%.2f", mCursor.getDouble(currencyValueIndex));

            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.currency_app_widget_item);

            remoteViews.setImageViewResource(R.id.iv_widget_flag, ResourcesUtil.getResourceByName(R.drawable.class, currencyName.toLowerCase()));
            remoteViews.setTextViewText(R.id.tv_widget_currency_name, currencyName);
            remoteViews.setTextViewText(R.id.tv_widget_currency_value, currencyValue);

            Intent fillInIntent = new Intent();
            remoteViews.setOnClickFillInIntent(R.id.view_widget_card, fillInIntent);

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}

