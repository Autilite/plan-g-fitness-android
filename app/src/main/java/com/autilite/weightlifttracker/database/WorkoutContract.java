package com.autilite.weightlifttracker.database;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class WorkoutContract {
    private WorkoutContract() {
    }

    public static class WorkoutEntry {
        public static final String TABLE_NAME = "Workout";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_EXERCISE = "ExerciseName";
        public static final String COLUMN_NAME_SET = "Sets";
        public static final String COLUMN_NAME_REP = "Reps";
        public static final String COLUMN_NAME_WEIGHT = "Weight";
    }
}
