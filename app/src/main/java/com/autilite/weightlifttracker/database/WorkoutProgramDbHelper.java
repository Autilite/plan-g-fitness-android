package com.autilite.weightlifttracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.autilite.weightlifttracker.database.ExerciseInfoContract.ExerciseInfoEntry;
import static com.autilite.weightlifttracker.database.ExerciseSessionContract.ExerciseSessionEntry;
import static com.autilite.weightlifttracker.database.ExerciseStatContract.ExerciseStatEntry;
import static com.autilite.weightlifttracker.database.ProgramContract.ProgramEntry;
import static com.autilite.weightlifttracker.database.ProgramSessionContract.ProgramSessionEntry;
import static com.autilite.weightlifttracker.database.ProgramWorkoutContract.ProgramWorkoutEntry;
import static com.autilite.weightlifttracker.database.WorkoutContract.WorkoutEntry;
import static com.autilite.weightlifttracker.database.WorkoutListContract.WorkoutListEntry;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public class WorkoutProgramDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "WorkoutProgram.db";

    private static final String SQL_CREATE_TABLE_EXERCISE_INFO =
            "CREATE TABLE " + ExerciseInfoEntry.TABLE_NAME + " (" +
                    ExerciseInfoEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ExerciseInfoEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    ExerciseInfoEntry.COLUMN_DESCRIPTION + " TEXT)";

    private static final String SQL_DELETE_TABLE_EXERCISE_INFO =
            "DROP TABLE IF EXISTS " + ExerciseInfoEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_EXERCISE_STATS =
            "CREATE TABLE " + ExerciseStatEntry.TABLE_NAME + " (" +
                    ExerciseStatEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ExerciseStatEntry.COLUMN_EXERCISE_ID + " INTEGER NOT NULL," +
                    ExerciseStatEntry.COLUMN_SET + " INTEGER," +
                    ExerciseStatEntry.COLUMN_REP + " INTEGER," +
                    ExerciseStatEntry.COLUMN_WEIGHT + " REAL," +
                    ExerciseStatEntry.COLUMN_AUTOINC + " REAL," +
                    "FOREIGN KEY (" + ExerciseStatEntry.COLUMN_EXERCISE_ID + ") " +
                    "REFERENCES " + ExerciseInfoEntry.TABLE_NAME + " (" + ExerciseInfoEntry._ID + ") " +
                    "ON DELETE RESTRICT ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_EXERCISE_STATS =
            "DROP TABLE IF EXISTS " + ExerciseStatEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_WORKOUT =
            "CREATE TABLE " + WorkoutEntry.TABLE_NAME + " (" +
                    WorkoutEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    WorkoutEntry.COLUMN_NAME + " TEXT NOT NULL)";

    private static final String SQL_DELETE_TABLE_WORKOUT =
            "DROP TABLE IF EXISTS " + WorkoutEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_WORKOUT_LIST =
            "CREATE TABLE " + WorkoutListEntry.TABLE_NAME + " (" +
                    WorkoutListEntry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL," +
                    WorkoutListEntry.COLUMN_EXERCISE_ID + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + WorkoutListEntry.COLUMN_WORKOUT_ID + ") " +
                    "REFERENCES " + WorkoutEntry.TABLE_NAME + "(" + WorkoutEntry._ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + WorkoutListEntry.COLUMN_EXERCISE_ID + ") " +
                    "REFERENCES " + ExerciseStatEntry.TABLE_NAME + "(" + ExerciseStatEntry._ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_WORKOUT_LIST =
            "DROP TABLE IF EXISTS " + WorkoutListEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM =
            "CREATE TABLE " + ProgramEntry.TABLE_NAME + " (" +
                    ProgramEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ProgramEntry.COLUMN_NAME + " TEXT NOT NULL)";

    private static final String SQL_DELETE_TABLE_PROGRAM =
            "DROP TABLE IF EXISTS " + ProgramEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM_WORKOUTS =
            "CREATE TABLE " + ProgramWorkoutEntry.TABLE_NAME + " (" +
                    ProgramWorkoutEntry.COLUMN_PROGRAM_ID + " INTEGER NOT NULL," +
                    ProgramWorkoutEntry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL," +
                    ProgramWorkoutEntry.COLUMN_NAME_DAY + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + ProgramWorkoutEntry.COLUMN_PROGRAM_ID + " )" +
                    "REFERENCES " + ProgramEntry.TABLE_NAME + "(" + ProgramEntry._ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + ProgramWorkoutEntry.COLUMN_WORKOUT_ID + ") " +
                    "REFERENCES " + WorkoutEntry.TABLE_NAME + " (" + WorkoutEntry._ID + ")" +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_PROGRAM_WORKOUTS =
            "DROP TABLE IF EXISTS " + ProgramWorkoutContract.ProgramWorkoutEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM_SESSION =
            "CREATE TABLE " + ProgramSessionEntry.TABLE_NAME+ " (" +
                    ProgramSessionEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ProgramSessionEntry.COLUMN_PROGRAM_ID + " INTEGER NOT NULL," +
                    ProgramSessionEntry.COLUMN_PROGRAM_DAY + " INTEGER NOT NULL," +
                    ProgramSessionEntry.COLUMN_DATE + " INTEGER NOT NULL," +
                    "FOREIGN KEY (" + ProgramSessionEntry.COLUMN_PROGRAM_ID + ") " +
                    "REFERENCES " + ProgramEntry.TABLE_NAME + " (" +
                    ProgramEntry._ID +") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_PROGRAM_SESSION =
            "DROP TABLE IF EXISTS " + ProgramSessionEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_EXERCISE_SESSION =
            "CREATE TABLE " + ExerciseSessionEntry.TABLE_NAME+ " (" +
                    ExerciseSessionEntry.COLUMN_PROGRAM_SESSION_ID + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_EXERCISE_ID + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_SET_NUMBER + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_SUCCESSFUL_REPS + " INTEGER NOT NULL," +
                    ExerciseSessionEntry.COLUMN_WEIGHT + " REAL," +
                    ExerciseSessionEntry.COLUMN_IS_SET_SUCCESSFUL + " INTEGER NOT NULL DEFAULT 0," +
                    "PRIMARY KEY ("+ ExerciseSessionEntry.COLUMN_PROGRAM_SESSION_ID +", " +
                    ExerciseSessionEntry.COLUMN_WORKOUT_ID + "," +
                    ExerciseSessionEntry.COLUMN_EXERCISE_ID + "," +
                    ExerciseSessionEntry.COLUMN_SET_NUMBER + ")," +
                    "FOREIGN KEY (" + ExerciseSessionEntry.COLUMN_PROGRAM_SESSION_ID + ") " +
                    "REFERENCES " + ProgramSessionEntry.TABLE_NAME + " (" +
                    ProgramSessionEntry._ID +") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + ExerciseSessionEntry.COLUMN_WORKOUT_ID +") " +
                    "REFERENCES " + WorkoutListEntry.TABLE_NAME +
                    " (" + WorkoutListEntry.COLUMN_WORKOUT_ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + ExerciseSessionEntry.COLUMN_EXERCISE_ID +")" +
                    " REFERENCES " + WorkoutListEntry.TABLE_NAME +
                    " ("+ WorkoutListEntry.COLUMN_EXERCISE_ID +") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_EXERCISE_SESSION =
            "DROP TABLE IF EXISTS " + ExerciseSessionContract.ExerciseSessionEntry.TABLE_NAME;

    public WorkoutProgramDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE_INFO);
        insertDefaultExercises(sqLiteDatabase);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE_STATS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT_LIST);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM_WORKOUTS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM_SESSION);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE_SESSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_EXERCISE_INFO);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_EXERCISE_STATS);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_WORKOUT);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_WORKOUT_LIST);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_PROGRAM);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_PROGRAM_WORKOUTS);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_PROGRAM_SESSION);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_EXERCISE_SESSION);
        onCreate(sqLiteDatabase);
    }

    private void insertDefaultExercises(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put(ExerciseInfoEntry.COLUMN_NAME, "Squats");
        db.insert(ExerciseInfoEntry.TABLE_NAME, null, cv);
        cv.put(ExerciseInfoEntry.COLUMN_NAME, "Deadlift");
        db.insert(ExerciseInfoEntry.TABLE_NAME, null, cv);
        cv.put(ExerciseInfoEntry.COLUMN_NAME, "Overhead Press");
        db.insert(ExerciseInfoEntry.TABLE_NAME, null, cv);
    }

}
