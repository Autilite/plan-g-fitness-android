package com.autilite.weightlifttracker.service;

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
import android.support.v4.content.LocalBroadcastManager;

import com.autilite.weightlifttracker.R;
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
    public final static String BROADCAST_COUNTDOWN = "com.autilite.weightlifttracker.service.WorkoutService.broadcast.COUNTDOWN";
    public final static String BROADCAST_UPDATED_SESSION = "com.autilite.weightlifttracker.service.WorkoutService.broadcast.UPDATED_SESSION";
    public final static String EXTRA_BROADCAST_COUNTDOWN = "countdown";

    public final static String ACTION_START_NEW_SESSION = "com.autilite.weightlifttracker.service.WorkoutService.action.START_NEW_SESSION";
    public final static String ACTION_COMPLETE_SET = "com.autilite.weightlifttracker.service.WorkoutService.action.COMPLETE_SET";
    public final static String ACTION_FAIL_SET = "com.autilite.weightlifttracker.service.WorkoutService.action.FAIL_SET";
    public final static String EXTRA_SET_REP = "rep";
    public final static String EXTRA_SET_WEIGHT = "weight";


    private final static int SESSION_NOTIFY_ID = 100;

    private final IBinder mBinder = new LocalBinder();
    private LocalBroadcastManager mLocalBroadcastManager;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private Intent activityIntent;

    private CountDownTimer timer;
    private boolean isTimerRunning = false;


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
        String action = intent.getAction();
        if (action == null || action.equals(ACTION_START_NEW_SESSION)) {
            initializeService(intent);
        } else if (action.equals(ACTION_COMPLETE_SET)) {
            completeSet(intent);
        } else if (action.equals(ACTION_FAIL_SET)) {
            failSet(intent);
        }
        // TODO handle null intent if service is killed and restarted with START_STICKY
        return START_STICKY;
    }

    private void initializeService(Intent intent) {
        programId = intent.getLongExtra(EXTRA_PROGRAM_ID, -1);
        programName = intent.getStringExtra(EXTRA_PROGRAM_NAME);

        // null out/cancel existing variables
        currentExercise = null;
        stopTimer();

        // initializeService the service
        initSession();

        activityIntent = new Intent(this, WorkoutSessionActivity.class);
        activityIntent.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_ID, programId);
        activityIntent.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_NAME, programName);

        initNotificationBuilder();

        startForeground(SESSION_NOTIFY_ID, notificationBuilder.build());
        startActivity(activityIntent);
    }

    private void initNotificationBuilder() {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Set notification info
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.choose_exercise))
                .setContentIntent(pendingIntent);
    }

    private void initSession() {
        workouts = workoutDb.getProgramWorkouts(programId);
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

    private void completeSet(Intent intent) {
        if (currentExercise == null) {
            throw new RuntimeException("There is no current exercise selected");
        }
        // For now, just take the current values from the exercise
        Exercise exercise = currentExercise.getExercise();
        int reps = intent.getIntExtra(EXTRA_SET_REP, exercise.getReps());
        // TODO handle "complete" where the default reps is 0
        // E.g., if the user presses fail followed by complete action in the notification
        double weight = intent.getDoubleExtra(EXTRA_SET_WEIGHT, exercise.getWeight());
        if (currentExercise.completeSet(reps, weight)) {
            exercise.setReps(reps);
            exercise.setWeight(weight);
            Intent updatedSessionIntent = new Intent(BROADCAST_UPDATED_SESSION);
            mLocalBroadcastManager.sendBroadcast(updatedSessionIntent);

            startTimer(exercise.getRestTime() * 1000);
        }
        // TODO Check if the current exercise is done
    }

    private void failSet(Intent intent) {
        // For simplicity, we take the values to be binary complete/fail
        // Either you completely pass or you completely failed
        Intent failIntent = new Intent();
        failIntent.putExtra(EXTRA_SET_REP, 0);
        completeSet(failIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        workoutDb = new WorkoutProgramDbHelper(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTimer();
        mLocalBroadcastManager = null;
        mNotificationManager.cancel(SESSION_NOTIFY_ID);

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
                updateNotificationContent(getString(R.string.time_until_next_set) + ": " + timer);

                Intent broadcastTimerIntent = new Intent(BROADCAST_COUNTDOWN);
                broadcastTimerIntent.putExtra(EXTRA_BROADCAST_COUNTDOWN, l);
                mLocalBroadcastManager.sendBroadcast(broadcastTimerIntent);
            }

            @Override
            public void onFinish() {
                // TODO ping the user
                isTimerRunning = false;
                updateNotificationContent(getString(R.string.start_set));
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

    public void completeSet(int reps, double weight) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SET_REP, reps);
        intent.putExtra(EXTRA_SET_WEIGHT, weight);
        completeSet(intent);
    }

    public void setSelectedExercise(ExerciseSession es) {
        currentExercise = es;
        if (es.getCurrentSet() != ExerciseSession.EXERCISE_COMPLETE) {
            // Reinitialize notification builder
            initNotificationBuilder();

            // Add complete/fail actions to notification
            Intent completeSetIntent = new Intent(this, WorkoutService.class);
            completeSetIntent.setAction(ACTION_COMPLETE_SET);
            PendingIntent pCompleteSetIntent = PendingIntent.getService(this, 0, completeSetIntent, 0);

            NotificationCompat.Action completeAction = new NotificationCompat.Action.Builder(
                    R.drawable.ic_check_black_24dp,
                    getString(R.string.notification_complete_set),
                    pCompleteSetIntent)
                    .build();

            Intent failSetIntent = new Intent(this, WorkoutService.class);
            failSetIntent.setAction(ACTION_FAIL_SET);
            PendingIntent pFailSetIntent = PendingIntent.getService(this, 0, failSetIntent, 0);

            NotificationCompat.Action failAction = new NotificationCompat.Action.Builder(
                    R.drawable.ic_fail_black_24dp,
                    getString(R.string.notification_fail_set),
                    pFailSetIntent)
                    .build();

            notificationBuilder.addAction(completeAction)
                    .addAction(failAction);
        }

        if (!isTimerRunning) {
            updateNotificationContent(getString(R.string.start_set));
        }
    }

    private void updateNotificationContent(String content) {
        notificationBuilder
                .setContentTitle(currentExercise == null ? "" : currentExercise.getExercise().getName())
                .setContentText(content);

        mNotificationManager.notify(SESSION_NOTIFY_ID, notificationBuilder.build());
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public ExerciseSession getCurrentExercise() {
        return currentExercise;
    }

    public ArrayList<? extends ExerciseSession> getSession(Workout w) {
        return sessions.get(w);
    }

}