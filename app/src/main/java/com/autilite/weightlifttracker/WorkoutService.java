package com.autilite.weightlifttracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.autilite.weightlifttracker.activity.MainActivity;
import com.autilite.weightlifttracker.activity.WorkoutSessionActivity;
import com.autilite.weightlifttracker.program.session.ExerciseSession;

import java.text.SimpleDateFormat;

/**
 * Created by Kelvin on Jul 11, 2017.
 */

public class WorkoutService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private CountDownTimer timer;
    private boolean isTimerRunning = false;
    private NotificationManager mNotificationManager;
    private PendingIntent pendingIntent;

    private ExerciseSession currentExercise;

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

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, WorkoutSessionActivity.class);
        // Add back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
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

    public void startTimer(long milliseconds) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(milliseconds, 500) {
            @Override
            public void onTick(long l) {
                String timer = new SimpleDateFormat("mm:ss").format(l);
                updateExerciseNotification(getString(R.string.time_until_next_set) + ": " + timer);
            }

            @Override
            public void onFinish() {
                // TODO ping the user
                isTimerRunning = false;
                updateExerciseNotification(getString(R.string.start_set));
            }
        };
        timer.start();
        isTimerRunning = true;
    }

    public void setSelectedExercise(ExerciseSession es) {
        currentExercise = es;

        if (!isTimerRunning) {
            updateExerciseNotification(getString(R.string.start_set));
        }
    }

    private void updateExerciseNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(currentExercise == null ? "" : currentExercise.getExercise().getName())
                .setContentText(content)
                .setContentIntent(pendingIntent);

        mNotificationManager.notify(WorkoutSessionActivity.NOTIFY_ID, builder.build());
    }
}
