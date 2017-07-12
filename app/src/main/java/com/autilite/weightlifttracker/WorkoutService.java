package com.autilite.weightlifttracker;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.autilite.weightlifttracker.activity.MainActivity;
import com.autilite.weightlifttracker.activity.WorkoutSessionActivity;

import java.text.SimpleDateFormat;

/**
 * Created by Kelvin on Jul 11, 2017.
 */

public class WorkoutService extends Service {
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public WorkoutService getService() {
            return WorkoutService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent notificationIntent = new Intent(this, WorkoutSessionActivity.class);
        // Add back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Set notification info
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.choose_exercise))
                .setContentIntent(pendingIntent);

        startForeground(WorkoutSessionActivity.NOTIFY_ID, builder.build());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
