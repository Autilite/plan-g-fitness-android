package com.autilite.weightlifttracker.program.session;

import com.autilite.weightlifttracker.program.Exercise;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kelvin on Jun 19, 2017.
 */

public class Session {
    public static final int EXERCISE_COMPLETE = -1;

    private long date;
    private Map<Exercise, ExerciseSession> sessionMap;

    public Session() {
        date = System.currentTimeMillis();
        sessionMap = new HashMap<>();
    }

    public boolean completeSet(Exercise exercise, int successfulReps, float weight) {
        if (exercise == null) {
            return false;
        }
        ExerciseSession es = sessionMap.get(exercise);
        boolean newSession = es == null;
        if (newSession) {
            return putNewExerciseSession(exercise).completeSet(successfulReps, weight);
        } else {
            return es.completeSet(successfulReps, weight);
        }
    }

    public boolean completeSet(Exercise exercise, int set, int successfulReps, float weight) {
        if (exercise == null) {
            return false;
        }
        ExerciseSession es = sessionMap.get(exercise);
        boolean newSession = es == null;
        if (newSession) {
            return putNewExerciseSession(exercise).completeSet(set, successfulReps, weight);
        } else {
            return es.completeSet(set, successfulReps, weight);
        }
    }

    public int getCurrentSet(Exercise exercise) {
        ExerciseSession es = sessionMap.get(exercise);
        if (es == null) {
            return putNewExerciseSession(exercise).getCurrentSet();
        } else {
            return es.getCurrentSet();
        }
    }

    /**
     * Create a new ExerciseSession using e and put it in the SessionMap. Then return the newly
     * created ExerciseSession
     * @param e
     * @return
     */
    private ExerciseSession putNewExerciseSession(Exercise e) {
        ExerciseSession es = new ExerciseSession(e);
        sessionMap.put(e, es);
        return es;
    }

}
