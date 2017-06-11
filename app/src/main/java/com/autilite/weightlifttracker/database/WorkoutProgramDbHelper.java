package com.autilite.weightlifttracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public class WorkoutProgramDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WorkoutProgram.db";

    private static final String SQL_CREATE_TABLE_EXERCISE =
            "CREATE TABLE " + ExerciseContract.ExerciseEntry.TABLE_NAME + " (" +
                    ExerciseContract.ExerciseEntry.COLUMN_NAME_NAME + " TEXT NOT NULL PRIMARY KEY," +
                    ExerciseContract.ExerciseEntry.COLUMN_NAME_DESCRIPTION + " TEXT)";

    private static final String SQL_DELETE_TABLE_EXERCISES =
            "DROP TABLE IF EXISTS " + ExerciseContract.ExerciseEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_WORKOUT =
            "CREATE TABLE " + WorkoutContract.WorkoutEntry.TABLE_NAME + " (" +
                    WorkoutContract.WorkoutEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    WorkoutContract.WorkoutEntry.COLUMN_NAME_EXERCISE + " TEXT NOT NULL," +
                    WorkoutContract.WorkoutEntry.COLUMN_NAME_SET + " INTEGER NOT NULL," +
                    WorkoutContract.WorkoutEntry.COLUMN_NAME_REP + " INTEGER NOT NULL,"  +
                    WorkoutContract.WorkoutEntry.COLUMN_NAME_WEIGHT + " REAL,"  +
                    "PRIMARY KEY (" + WorkoutContract.WorkoutEntry.COLUMN_NAME_NAME +
                    "," + WorkoutContract.WorkoutEntry.COLUMN_NAME_EXERCISE + ")," +
                    "FOREIGN KEY (" + WorkoutContract.WorkoutEntry.COLUMN_NAME_EXERCISE +
                    ") REFERENCES " + ExerciseContract.ExerciseEntry.TABLE_NAME +
                    "(" + ExerciseContract.ExerciseEntry.COLUMN_NAME_NAME + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_WORKOUT =
            "DROP TABLE IF EXISTS " + WorkoutContract.WorkoutEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM =
            "CREATE TABLE " + ProgramContract.ProgramEntry.TABLE_NAME + " (" +
                    ProgramContract.ProgramEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    ProgramContract.ProgramEntry.COLUMN_NAME_WORKOUT + " TEXT NOT NULL," +
                    ProgramContract.ProgramEntry.COLUMN_NAME_DAY + " INTEGER NOT NULL," +
                    "PRIMARY KEY (" + ProgramContract.ProgramEntry.COLUMN_NAME_NAME +
                    "," + ProgramContract.ProgramEntry.COLUMN_NAME_DAY + ")," +
                    "FOREIGN KEY (" + ProgramContract.ProgramEntry.COLUMN_NAME_WORKOUT +
                    ") REFERENCES " + WorkoutContract.WorkoutEntry.TABLE_NAME +
                    "(" + WorkoutContract.WorkoutEntry.COLUMN_NAME_NAME + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_PROGRAM =
            "DROP TABLE IF EXISTS " + ProgramContract.ProgramEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_WORKOUT_SESSION =
            "CREATE TABLE " + WorkoutSessionContract.WorkoutSessionEntry.TABLE_NAME+ " (" +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_PROGRAM + " TEXT NOT NULL," +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_WORKOUT + " TEXT NOT NULL," +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_EXERCISE + " TEXT NOT NULL," +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_DATE + " TEXT NOT NULL," +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_SET_NUMBER + " INTEGER NOT NULL," +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_SUCCESSFUL_REPS + " INTEGER NOT NULL," +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_WEIGHT + " REAL," +
                    "PRIMARY KEY ("+ WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_PROGRAM+", " +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_WORKOUT + "," +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_EXERCISE + "," +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_DATE + "," +
                    WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_SET_NUMBER + ")," +
                    "FOREIGN KEY (" + WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_PROGRAM +")" +
                    " REFERENCES " + ProgramContract.ProgramEntry.TABLE_NAME +
                    " ("+ ProgramContract.ProgramEntry.COLUMN_NAME_NAME+") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_WORKOUT +")" +
                    " REFERENCES " + WorkoutContract.WorkoutEntry.TABLE_NAME +
                    " ("+ WorkoutContract.WorkoutEntry.COLUMN_NAME_NAME+") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + WorkoutSessionContract.WorkoutSessionEntry.COLUMN_NAME_EXERCISE +")" +
                    " REFERENCES " + ExerciseContract.ExerciseEntry.TABLE_NAME +
                    " ("+ ExerciseContract.ExerciseEntry.COLUMN_NAME_NAME+") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_WORKOUT_SESSION =
            "DROP TABLE IF EXISTS " + WorkoutSessionContract.WorkoutSessionEntry.TABLE_NAME;

    public WorkoutProgramDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT_SESSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
