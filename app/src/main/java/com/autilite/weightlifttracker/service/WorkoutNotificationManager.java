package com.autilite.weightlifttracker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.autilite.weightlifttracker.R;

/**
 * Created by Kelvin on Aug 14, 2017.
 */

public class WorkoutNotificationManager {
    private final int notificationId;

    private final Context context;
    private final Intent activityIntent;
    private NotificationManager mNotificationManager;

    private NotificationCompat.Builder notificationBuilder;

    public WorkoutNotificationManager(Context context, int notificationId, Intent activityIntent) {
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        this.context = context;
        this.activityIntent = activityIntent;
        this.notificationId = notificationId;

        initNotificationBuilder();
    }

    public void cancel() {
        mNotificationManager.cancel(notificationId);
    }

    public Notification getNotification() {
        return notificationBuilder.build();
    }

    private void initNotificationBuilder() {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Set notification info
        notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(context.getString(R.string.choose_exercise))
                .setContentIntent(pendingIntent);
    }

    public void notifyExerciseComplete() {
        // The purpose of re-initializing the notification is to remove the
        // complete and fail actions
        initNotificationBuilder();
        mNotificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void notifySetComplete() {
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notifyStartSet(null);

        // Reset the notification defaults to none
        notificationBuilder.setDefaults(0);
    }

    public void notifyStartSet(String title) {
        updateNotificationContent(title, context.getString(R.string.start_set));
    }

    public void setOnGoingExercise(PendingIntent complete, PendingIntent fail) {
        // Reinitialize notification builder
        initNotificationBuilder();

        NotificationCompat.Action completeAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_check_black_24dp,
                context.getString(R.string.notification_complete_set),
                complete)
                .build();

        NotificationCompat.Action failAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_fail_black_24dp,
                context.getString(R.string.notification_fail_set),
                fail)
                .build();

        notificationBuilder.addAction(completeAction).addAction(failAction);
    }

    public void updateNotificationContent(String title, String content) {
        if (title != null)
            notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(content);
        mNotificationManager.notify(notificationId, notificationBuilder.build());
    }
}
