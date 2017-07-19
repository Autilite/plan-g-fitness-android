package com.autilite.weightlifttracker.database;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class ExerciseSessionContract {
    private ExerciseSessionContract() {
    }

    public static class ExerciseSessionEntry {
        public static final String TABLE_NAME = "ExerciseSession";
        public static final String COLUMN_PROGRAM_SESSION_ID = "ProgramSessionId";
        public static final String COLUMN_WORKOUT_ID = "WorkoutId";
        public static final String COLUMN_EXERCISE_ID = "ExerciseId";
        public static final String COLUMN_SET_NUMBER = "SetNumber";
        public static final String COLUMN_SUCCESSFUL_REPS = "SuccessfulReps";
        public static final String COLUMN_WEIGHT = "ExerciseWeight";
        public static final String COLUMN_IS_SET_SUCCESSFUL = "IsSetSuccessful";
    }
}
