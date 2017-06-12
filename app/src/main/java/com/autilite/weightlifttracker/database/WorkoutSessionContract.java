package com.autilite.weightlifttracker.database;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class WorkoutSessionContract {
    private WorkoutSessionContract() {
    }

    public static class WorkoutSessionEntry {
        public static final String TABLE_NAME = "WorkoutSession";
        public static final String COLUMN_PROGRAM_ID = "ProgId";
        public static final String COLUMN_WORKOUT_ID = "WorkoutId";
        public static final String COLUMN_EXERCISE_ID = "ExerciseId";
        public static final String COLUMN_DATE = "SessionDate";
        public static final String COLUMN_SET_NUMBER = "SetNumber";
        public static final String COLUMN_SUCCESSFUL_REPS = "SuccessfulReps";
        public static final String COLUMN_IS_SET_SUCCESSFUL = "IsSetSuccessful";
        public static final String COLUMN_WEIGHT = "ExerciseWeight";
    }
}
