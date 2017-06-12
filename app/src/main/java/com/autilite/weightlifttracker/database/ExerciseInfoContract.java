package com.autilite.weightlifttracker.database;

import android.provider.BaseColumns;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class ExerciseInfoContract {
    private ExerciseInfoContract() {
    }

    public static class ExerciseInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "ExerciseInfo";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_DESCRIPTION = "Description";
    }
}
