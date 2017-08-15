package com.autilite.weightlifttracker.database;

import android.provider.BaseColumns;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class ExerciseContract {
    private ExerciseContract() {
    }

    public static class BaseExerciseEntry implements BaseColumns {
        public static final String TABLE_NAME = "BaseExercise";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_DESCRIPTION = "Description";
    }

    public static class ExerciseEntry implements BaseColumns {
        public static final String TABLE_NAME = "Exercise";
        public static final String COLUMN_BASE_EXERCISE_ID = "BaseExerciseId";
        public static final String COLUMN_SET = "Sets";
        public static final String COLUMN_REP = "Reps";
        public static final String COLUMN_WEIGHT = "Weight";
        public static final String COLUMN_AUTOINC = "AutoIncr";
        public static final String COLUMN_REST_TIME = "RestTime";
        public static final String COLUMN_CREATION = "CreationTime";
    }
}
