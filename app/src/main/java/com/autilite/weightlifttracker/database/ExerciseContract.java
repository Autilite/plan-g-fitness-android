package com.autilite.weightlifttracker.database;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class ExerciseContract {
    private ExerciseContract() {
    }

    public static class ExerciseEntry {
        public static final String TABLE_NAME = "Exercise";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
    }
}
