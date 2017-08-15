package com.autilite.weightlifttracker.database;

import android.provider.BaseColumns;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class ProgramSessionContract {
    private ProgramSessionContract() {
    }

    public static class ProgramSessionEntry implements BaseColumns {
        public static final String TABLE_NAME = "ProgramSession";
        public static final String COLUMN_PROGRAM_ID = "ProgId";
        public static final String COLUMN_PROGRAM_DAY = "ProgDay";
        public static final String COLUMN_TIME_START = "TimeStart";
        public static final String COLUMN_TIME_END = "TimeEnd";
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
