package com.flores.easycurrencyconverter.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.flores.easycurrencyconverter.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RatesService extends IntentService {

    public static final String ACTION_UPDATE_WIDGETS = "com.flores.easycurrencyconverter.widget.action.update_app_widgets";

    public RatesService() {
        super("RatesService");
    }

    /**
     * Starts this service to perform UpdateWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, RatesService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGETS.equals(action)) {
                handleActionUpdateWidgets();
            }
        }
    }

    /**
     * Handle action UpdateWidgets in the provided background thread
     */
    private void handleActionUpdateWidgets() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] widgetsIds = widgetManager.getAppWidgetIds(new ComponentName(this, CurrencyAppWidget.class));
        widgetManager.notifyAppWidgetViewDataChanged(widgetsIds, R.id.lv_widget);
        CurrencyAppWidget.updateWidgets(this, widgetManager, widgetsIds);
    }
}
