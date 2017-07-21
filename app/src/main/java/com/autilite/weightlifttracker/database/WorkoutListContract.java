package com.autilite.weightlifttracker.database;

import android.provider.BaseColumns;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class WorkoutListContract {
    private WorkoutListContract() {
    }

    public static class WorkoutListEntry {
        public static final String TABLE_NAME = "WorkoutList";
        public static final String COLUMN_WORKOUT_ID = "WorkoutId";
        public static final String COLUMN_EXERCISE_ID = "ExerciseId";
        public static final String COLUMN_DATE_ADDED = "DateAdded";
    }
}
