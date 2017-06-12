package com.autilite.weightlifttracker.database;

import android.provider.BaseColumns;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class WorkoutContract {
    private WorkoutContract() {
    }

    public static class WorkoutEntry implements BaseColumns {
        public static final String TABLE_NAME = "Workouts";
        public static final String COLUMN_NAME = "Name";
    }
}
