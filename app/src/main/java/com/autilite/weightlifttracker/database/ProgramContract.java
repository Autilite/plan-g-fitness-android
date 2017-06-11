package com.autilite.weightlifttracker.database;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class ProgramContract {
    private ProgramContract() {
    }

    public static class ProgramEntry {
        public static final String TABLE_NAME = "Program";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_WORKOUT = "WorkoutName";
        public static final String COLUMN_NAME_DAY = "Day";
    }
}
