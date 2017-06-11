package com.autilite.weightlifttracker.database;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class WorkoutSessionContract {
    private WorkoutSessionContract() {
    }

    public static class WorkoutSessionEntry {
        public static final String TABLE_NAME = "WorkoutSession";
        public static final String COLUMN_NAME_PROGRAM = "ProgramName";
        public static final String COLUMN_NAME_WORKOUT = "WorkoutName";
        public static final String COLUMN_NAME_EXERCISE = "ExerciseName";
        public static final String COLUMN_NAME_DATE = "Date";
        public static final String COLUMN_NAME_SET_NUMBER = "SetNumber";
        public static final String COLUMN_NAME_SUCCESSFUL_REPS = "SuccessfulReps";
        public static final String COLUMN_NAME_WEIGHT = "ExerciseWeight";
    }
}
