package com.autilite.weightlifttracker.service;

import android.app.Notification;
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
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Workout;
import com.autilite.weightlifttracker.program.session.ExerciseSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.autilite.weightlifttracker.activity.WorkoutSessionActivity.EXTRA_PROGRAM_DAY;
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
    public final static String ACTION_SAVE_SESSION = "com.autilite.weightlifttracker.service.WorkoutService.action.SAVE_SESSION";
    public final static String EXTRA_SET_REP = "rep";
    public final static String EXTRA_SET_WEIGHT = "weight";

    private final static int SESSION_NOTIFY_ID = 100;

    private final IBinder mBinder = new LocalBinder();
    private LocalBroadcastManager mLocalBroadcastManager;
    private WorkoutNotificationManager wnm;

    private CountDownTimer timer;
    private boolean isTimerRunning = false;

    private long startTime;

    private long programId;
    private String programName;
    private int programDay;
    private ExerciseSession currentExercise;
    private List<Workout> workouts;
    private WorkoutDatabase workoutDb;
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
        } else if (action.equals(ACTION_SAVE_SESSION)) {
            saveSession(intent);
        }
        // TODO handle null intent if service is killed and restarted with START_STICKY
        return START_STICKY;
    }

    private void initializeService(Intent intent) {
        programId = intent.getLongExtra(EXTRA_PROGRAM_ID, -1);
        programName = intent.getStringExtra(EXTRA_PROGRAM_NAME);
        programDay = intent.getIntExtra(EXTRA_PROGRAM_DAY, -1);

        // null out/cancel existing variables
        currentExercise = null;
        stopTimer();

        // initializeService the service
        initSession();

        Intent activityIntent = new Intent(this, WorkoutSessionActivity.class);
        activityIntent.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_ID, programId);
        activityIntent.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_NAME, programName);
        activityIntent.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_DAY, programDay);

        wnm = new WorkoutNotificationManager(this, SESSION_NOTIFY_ID, activityIntent);

        startForeground(SESSION_NOTIFY_ID, wnm.getNotification());
        startActivity(activityIntent);
    }

    private void initSession() {
        startTime = System.currentTimeMillis();
        workouts = workoutDb.getProgramWorkouts(programId, programDay);
        sessions = new HashMap<>();
        for (Workout w : workouts) {
            List<Exercise> exercises = workoutDb.getAllExerciseList(w.getId());
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
        // Take default values from the Exercise object, not the current session
        // This is so the default intent for complete is to... complete.
        // The currentRep and currentWeight can store values that are 'fail' such as 0
        Exercise exercise = currentExercise.getExercise();
        int reps = intent.getIntExtra(EXTRA_SET_REP, exercise.getReps());
        double weight = intent.getDoubleExtra(EXTRA_SET_WEIGHT, exercise.getWeight());

        if (currentExercise.completeSet(reps, weight)) {
            currentExercise.setCurrentRep(reps);
            currentExercise.setCurrentWeight(weight);
            Intent updatedSessionIntent = new Intent(BROADCAST_UPDATED_SESSION);
            mLocalBroadcastManager.sendBroadcast(updatedSessionIntent);

            startTimer(exercise.getRestTime() * 1000);
        }

        if (currentExercise.isSessionDone()) {
            wnm.notifyExerciseComplete();
        }
    }

    private void failSet(Intent intent) {
        // For simplicity, we take the values to be binary complete/fail
        // Either you completely pass or you completely failed
        Intent failIntent = new Intent();
        failIntent.putExtra(EXTRA_SET_REP, 0);
        completeSet(failIntent);
    }

    private void saveSession(Intent intent) {
        // TODO handle empty session
        long timeEnd = System.currentTimeMillis();
        workoutDb.addSession(programId, programDay, startTime, timeEnd, sessions);
        workoutDb.setPreviousProgDay(this, programId, programDay);
        // TODO update exercise with any changes from this session
        // e.g., exercise auto increment if the exercise session was successful
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        workoutDb = new WorkoutDatabase(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTimer();
        mLocalBroadcastManager = null;
        wnm.cancel();

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
                isTimerRunning = false;
                wnm.notifySetComplete();
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
        if (!currentExercise.isSessionDone()) {
            // Add complete/fail actions to notification
            Intent completeSetIntent = new Intent(this, WorkoutService.class);
            completeSetIntent.setAction(ACTION_COMPLETE_SET);
            PendingIntent pCompleteSetIntent = PendingIntent.getService(this, 0, completeSetIntent, 0);

            Intent failSetIntent = new Intent(this, WorkoutService.class);
            failSetIntent.setAction(ACTION_FAIL_SET);
            PendingIntent pFailSetIntent = PendingIntent.getService(this, 0, failSetIntent, 0);

            wnm.setOnGoingExercise(pCompleteSetIntent, pFailSetIntent);
        }

        if (!isTimerRunning) {
            wnm.notifyStartSet(currentExercise.getExercise().getName());
        }
    }

    private void updateNotificationContent(String content) {
        String title = "";
        if (currentExercise != null) {
            Exercise e = currentExercise.getExercise();
            int currentSet = currentExercise.getCurrentSet();

            String setString = currentSet + "/" + e.getSets();
            String completeSet = getResources().getString(R.string.exercise_complete);
            String s = currentExercise.isSessionDone() ? completeSet : setString;

            title = e.getName() + " " + s;
        }
        wnm.updateNotificationContent(title, content);
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
