package com.example.thinkpad.t46bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.thinkpad.t46bakingapp.R;
import com.example.thinkpad.t46bakingapp.view.RecipeActivity;

/**
 * Created by thinkpad on 2018/1/29.
 */

/**
 * 现在可以放置一个2x2的小组件在home了，点击会进入MainActivity.
 * */

public class BakingAppWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId:appWidgetIds){
            Intent intent = new Intent(context, RecipeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widget,pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }
}