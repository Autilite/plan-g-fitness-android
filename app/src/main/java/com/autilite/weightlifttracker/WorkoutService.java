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

import com.autilite.weightlifttracker.activity.WorkoutSessionActivity;
import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Workout;
import com.autilite.weightlifttracker.program.session.ExerciseSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.autilite.weightlifttracker.activity.WorkoutSessionActivity.EXTRA_PROGRAM_ID;
import static com.autilite.weightlifttracker.activity.WorkoutSessionActivity.EXTRA_PROGRAM_NAME;

/**
 * Created by Kelvin on Jul 11, 2017.
 */

public class WorkoutService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private CountDownTimer timer;
    private boolean isTimerRunning = false;
    private NotificationManager mNotificationManager;
    private PendingIntent pendingIntent;

    private long programId;
    private String programName;
    private ExerciseSession currentExercise;
    private List<Workout> workouts;
    private WorkoutProgramDbHelper workoutDb;

    private Map<Workout, ArrayList<? extends ExerciseSession>> sessions;

    public class LocalBinder extends Binder {
        public WorkoutService getService() {
            return WorkoutService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        programId = intent.getLongExtra(EXTRA_PROGRAM_ID, -1);
        programName = intent.getStringExtra(EXTRA_PROGRAM_NAME);

        workouts = workoutDb.getProgramWorkouts(programId);
        initSession(workouts);

        Intent activityIntent = new Intent(this, WorkoutSessionActivity.class);
        activityIntent.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_ID, programId);
        activityIntent.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_NAME, programName);

        pendingIntent = PendingIntent.getActivity(this, 0, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Set notification info
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.choose_exercise))
                .setContentIntent(pendingIntent);

        startForeground(WorkoutSessionActivity.NOTIFY_ID, builder.build());

        startActivity(activityIntent);
        return START_STICKY;
    }

    private void initSession(List<Workout> workouts) {
        sessions = new HashMap<>();
        for (Workout w : workouts) {
            List<Exercise> exercises = workoutDb.getAllExerciseInfoList(w.getId());
            ArrayList<ExerciseSession> session = new ArrayList<>();
            for (Exercise e: exercises) {
                ExerciseSession es = new ExerciseSession(e);
                session.add(es);
            }
            sessions.put(w, session);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        workoutDb = new WorkoutProgramDbHelper(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTimer();
        mNotificationManager.cancel(WorkoutSessionActivity.NOTIFY_ID);

        workoutDb.close();
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

    public void stopTimer() {
        if (isTimerRunning) {
            timer.cancel();
            isTimerRunning = false;
        }
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

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public ArrayList<? extends ExerciseSession> getSession(Workout w) {
        return sessions.get(w);
    }

}
