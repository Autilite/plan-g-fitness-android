package com.autilite.plan_g.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.autilite.plan_g.R;
import com.autilite.plan_g.activity.WorkoutSessionActivity;
import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.program.Exercise;
import com.autilite.plan_g.program.Workout;
import com.autilite.plan_g.program.session.ExerciseSession;
import com.autilite.plan_g.util.PebbleConstants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.getpebble.android.kit.util.PebbleTuple;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.autilite.plan_g.activity.WorkoutSessionActivity.EXTRA_PROGRAM_DAY;
import static com.autilite.plan_g.activity.WorkoutSessionActivity.EXTRA_PROGRAM_ID;
import static com.autilite.plan_g.activity.WorkoutSessionActivity.EXTRA_PROGRAM_NAME;

/**
 * Created by Kelvin on Jul 11, 2017.
 */

public class WorkoutService extends Service {
    private final static String TAG = WorkoutService.class.getName();

    public final static String BROADCAST_COUNTDOWN = "com.autilite.plan_g.service.WorkoutService.broadcast.COUNTDOWN";
    public final static String BROADCAST_UPDATED_SESSION = "com.autilite.plan_g.service.WorkoutService.broadcast.UPDATED_SESSION";
    public final static String EXTRA_BROADCAST_COUNTDOWN = "countdown";

    public final static String ACTION_START_NEW_SESSION = "com.autilite.plan_g.service.WorkoutService.action.START_NEW_SESSION";
    public final static String ACTION_COMPLETE_SET = "com.autilite.plan_g.service.WorkoutService.action.COMPLETE_SET";
    public final static String ACTION_FAIL_SET = "com.autilite.plan_g.service.WorkoutService.action.FAIL_SET";
    public final static String ACTION_SAVE_SESSION = "com.autilite.plan_g.service.WorkoutService.action.SAVE_SESSION";
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
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        wnm = new WorkoutNotificationManager(this, SESSION_NOTIFY_ID, activityIntent);

        startForeground(SESSION_NOTIFY_ID, wnm.getNotification());
        startActivity(activityIntent);

        PebbleKit.startAppOnPebble(this, PebbleConstants.WATCH_APP_UUID);
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

            // Alert pebble the exercise changed
            sendExerciseDataToPebble(currentExercise);

            long restTime = exercise.getRestTime() * 1000;
            sendTimerDataToPebble(restTime);
            startTimer(restTime);
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
        progressiveOverload();
    }

    private void progressiveOverload() {
        for (Map.Entry<Workout, ArrayList<? extends ExerciseSession>> entry : sessions.entrySet()) {
            ArrayList<? extends  ExerciseSession> esList = entry.getValue();

            // Check success for each exercise
            for (ExerciseSession es: esList){
                if (es.isSessionSuccessful()) {
                    progressiveOverloadHelper(es.getExercise());
                }
            }

        }
    }

    /**
     * Helper function for increasing the Exercise's volume and weight based off its given increment
     * values
     *
     * @param exercise
     */
    private void progressiveOverloadHelper(Exercise exercise) {
        // For progressive overload, we follow the algorithm:
        // 1) Volume:
        // If the current rep is at its max, then lower volume to the min and increment the weight
        // Otherwise, increase the volume (reps). If this increase causes the rep to go over the max,
        // then we set the volume to the max.
        // 2) If the user did not set volume for progressive over, then just increment the weight
        int repIncrement = exercise.getRepsIncrement();
        int repCurrent = exercise.getReps();
        if (repIncrement > 0) {
            if (repCurrent == exercise.getRepsMax()) {
                exercise.setReps(exercise.getRepsMin());
                exercise.setWeight(exercise.getWeight() + exercise.getWeightIncrement());
            } else {
                int newRepCount = repCurrent + repIncrement;
                if (newRepCount >= exercise.getRepsMax()) {
                    exercise.setReps(exercise.getRepsMax());
                } else {
                    exercise.setReps(newRepCount);
                }
            }
        } else if (exercise.getWeightIncrement() > 0){
            exercise.setWeight(exercise.getWeight() + exercise.getWeightIncrement());
        } else {
            return;
        }
        workoutDb.updateExercise(exercise.getId(), exercise.getReps(), exercise.getWeight());
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        workoutDb = new WorkoutDatabase(this);

        PebbleKit.registerPebbleConnectedReceiver(this, pebbleConnectedHandler);
        PebbleKit.registerPebbleDisconnectedReceiver(this, pebbleDisconnectedHandler);
        PebbleKit.registerReceivedDataHandler(this, pebbleReceiverDataHandler);
    }

    private BroadcastReceiver pebbleConnectedHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PebbleKit.startAppOnPebble(WorkoutService.this, PebbleConstants.WATCH_APP_UUID);
            sendExerciseDataToPebble(currentExercise);
        }
    };

    private BroadcastReceiver pebbleDisconnectedHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    private PebbleKit.PebbleDataReceiver pebbleReceiverDataHandler =
            new PebbleKit.PebbleDataReceiver(PebbleConstants.WATCH_APP_UUID) {
                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                    // Always send ack
                    PebbleKit.sendAckToPebble(context, transactionId);

                    if (data.contains(PebbleConstants.APP_KEY_REQUEST_DATA)) {
                        Log.i(TAG, "Received exercise data request from pebble");
                        sendExerciseDataToPebble(currentExercise);
                    } else if (data.contains(PebbleConstants.APP_KEY_REQUEST_CHANGE_PREVIOUS_EXERCISE)) {
                        Log.i(TAG, "Received request to change to the previous exercise from pebble");
                        setSelectedExercisePrevious();
                    } else if (data.contains(PebbleConstants.APP_KEY_REQUEST_CHANGE_NEXT_EXERCISE)) {
                        Log.i(TAG, "Received request to change to the next exercise from pebble");
                        setSelectedExerciseNext();
                    } else if (data.contains(PebbleConstants.APP_KEY_EXERCISE_ID)
                            && data.contains(PebbleConstants.APP_KEY_EXERCISE_SET)
                            && data.contains(PebbleConstants.APP_KEY_EXERCISE_REPS)
                            && data.contains(PebbleConstants.APP_KEY_EXERCISE_WEIGHT)){
                        Long idBox = data.getUnsignedIntegerAsLong(PebbleConstants.APP_KEY_EXERCISE_ID);
                        Long setBox = data.getInteger(PebbleConstants.APP_KEY_EXERCISE_SET);
                        Long repBox = data.getInteger(PebbleConstants.APP_KEY_EXERCISE_REPS);
                        Long weightBox = data.getInteger(PebbleConstants.APP_KEY_EXERCISE_WEIGHT);
                        int set = setBox.intValue();
                        int rep = repBox.intValue();
                        double weight = weightBox.intValue() / 10;

                        completeSet(rep, weight);
                    } else {
                        StringBuilder builder = new StringBuilder("Received unhandled key(s): ");
                        for (PebbleTuple t : data) {
                            builder.append(t.key).append(" ");
                        }
                        Log.w(TAG, builder.toString());
                    }
                }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTimer();
        mLocalBroadcastManager = null;
        wnm.cancel();

        workoutDb.close();
        PebbleKit.closeAppOnPebble(this, PebbleConstants.WATCH_APP_UUID);
        unregisterReceiver(pebbleConnectedHandler);
        unregisterReceiver(pebbleDisconnectedHandler);
        unregisterReceiver(pebbleReceiverDataHandler);
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
        // Update the current exercise
        currentExercise = es;

        // Update the notification
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

        // Update the pebble watch app
        sendExerciseDataToPebble(es);
    }

    public void setSelectedExercisePrevious() {
        // Search through the Session map and check each ExerciseSession list
        for (Map.Entry<Workout, ArrayList<? extends ExerciseSession>> entry : sessions.entrySet()) {
            ArrayList<? extends  ExerciseSession> esList = entry.getValue();

            for (int i = 0; i < esList.size(); i++) {
                if (esList.get(i).equals(currentExercise)) {
                    // Get the previous exercise and set it to the current selection
                    try {
                        setSelectedExercise(esList.get(i-1));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        // currentExercise is the first entry of this workout
                        // We can either do nothing or select the last exercise of the previous workout
                        // At the moment, we will simply do nothing.
                    }
                }
            }
        }
    }

    public void setSelectedExerciseNext() {
        // Search through the Session map and check each ExerciseSession list
        for (Map.Entry<Workout, ArrayList<? extends ExerciseSession>> entry : sessions.entrySet()) {
            ArrayList<? extends  ExerciseSession> esList = entry.getValue();

            for (int i = 0; i < esList.size(); i++) {
                if (esList.get(i).equals(currentExercise)) {
                    // Get the next exercise and set it to the current selection
                    try {
                        setSelectedExercise(esList.get(i+1));
                        return;
                    } catch (IndexOutOfBoundsException e){
                        // currentExercise is the last entry of this workout
                        // We can either do nothing or select the first exercise of the next workout
                        // At the moment, we will simply do nothing.
                    }
                }
            }
        }
    }

    /**
     * Sends the Exercise Session to the pebble watch. If the watch isn't connected or if the
     * <code>ExerciseSession</code> is null, then nothing is sent
     * @param es
     */
    private void sendExerciseDataToPebble(ExerciseSession es) {
        if (PebbleKit.isWatchConnected(this) && es != null) {
            PebbleDictionary dictionary = new PebbleDictionary();
            dictionary.addUint32(PebbleConstants.APP_KEY_EXERCISE_ID, (int) es.getExercise().getId());
            dictionary.addString(PebbleConstants.APP_KEY_EXERCISE_NAME, es.getExercise().getName());
            dictionary.addInt32(PebbleConstants.APP_KEY_EXERCISE_SET, es.getCurrentSet());
            dictionary.addInt32(PebbleConstants.APP_KEY_EXERCISE_REPS, es.getCurrentRep());
            int sentWeight = (int)  (es.getCurrentWeight() * 10);
            dictionary.addInt32(PebbleConstants.APP_KEY_EXERCISE_WEIGHT, sentWeight);
            PebbleKit.sendDataToPebble(this, PebbleConstants.WATCH_APP_UUID, dictionary);
        }
    }

    private void sendTimerDataToPebble(long milliseconds) {
        if (PebbleKit.isWatchConnected(this) && milliseconds > 0) {
            PebbleDictionary dictionary = new PebbleDictionary();
            dictionary.addUint32(PebbleConstants.APP_KEY_EXERCISE_REST_TIMER, (int) milliseconds);
            PebbleKit.sendDataToPebble(this, PebbleConstants.WATCH_APP_UUID, dictionary);
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
