package com.autilite.plan_g.database;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class ProgramWorkoutContract {
    private ProgramWorkoutContract() {
    }

    public static class ProgramWorkoutEntry {
        public static final String TABLE_NAME = "ProgramWorkouts";
        public static final String COLUMN_PROGRAM_ID = "ProgId";
        public static final String COLUMN_WORKOUT_ID = "WorkoutId";
        public static final String COLUMN_NAME_DAY = "Day";
        public static final String COLUMN_DATE_ADDED = "DateAdded";
    }
}
