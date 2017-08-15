package com.autilite.weightlifttracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.autilite.weightlifttracker.database.ExerciseContract.BaseExerciseEntry;
import static com.autilite.weightlifttracker.database.ExerciseContract.ExerciseEntry;
import static com.autilite.weightlifttracker.database.ProgramContract.ProgramEntry;
import static com.autilite.weightlifttracker.database.ProgramSessionContract.ExerciseSessionEntry;
import static com.autilite.weightlifttracker.database.ProgramSessionContract.ProgramSessionEntry;
import static com.autilite.weightlifttracker.database.ProgramWorkoutContract.ProgramWorkoutEntry;
import static com.autilite.weightlifttracker.database.WorkoutContract.WorkoutEntry;
import static com.autilite.weightlifttracker.database.WorkoutListContract.WorkoutListEntry;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public class WorkoutProgramDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "WorkoutProgram.db";

    private static final String SQL_CREATE_TABLE_BASE_EXERCISE =
            "CREATE TABLE " + BaseExerciseEntry.TABLE_NAME + " (" +
                    BaseExerciseEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    BaseExerciseEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    BaseExerciseEntry.COLUMN_DESCRIPTION + " TEXT)";

    private static final String SQL_DELETE_TABLE_BASE_EXERCISE =
            "DROP TABLE IF EXISTS " + BaseExerciseEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_EXERCISE =
            "CREATE TABLE " + ExerciseEntry.TABLE_NAME + " (" +
                    ExerciseEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ExerciseEntry.COLUMN_BASE_EXERCISE_ID + " INTEGER NOT NULL," +
                    ExerciseEntry.COLUMN_SET + " INTEGER," +
                    ExerciseEntry.COLUMN_REP + " INTEGER," +
                    ExerciseEntry.COLUMN_WEIGHT + " REAL," +
                    ExerciseEntry.COLUMN_AUTOINC + " REAL," +
                    ExerciseEntry.COLUMN_REST_TIME + " INTEGER," +
                    ExerciseEntry.COLUMN_CREATION + " INTEGER," +
                    "FOREIGN KEY (" + ExerciseEntry.COLUMN_BASE_EXERCISE_ID + ") " +
                    "REFERENCES " + BaseExerciseEntry.TABLE_NAME + " (" + BaseExerciseEntry._ID + ") " +
                    "ON DELETE RESTRICT ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_EXERCISE =
            "DROP TABLE IF EXISTS " + ExerciseEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_WORKOUT =
            "CREATE TABLE " + WorkoutEntry.TABLE_NAME + " (" +
                    WorkoutEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    WorkoutEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    WorkoutEntry.COLUMN_DESCRIPTION + " TEXT," +
                    WorkoutEntry.COLUMN_CREATION + " INTEGER)";

    private static final String SQL_DELETE_TABLE_WORKOUT =
            "DROP TABLE IF EXISTS " + WorkoutEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_WORKOUT_LIST =
            "CREATE TABLE " + WorkoutListEntry.TABLE_NAME + " (" +
                    WorkoutListEntry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL," +
                    WorkoutListEntry.COLUMN_EXERCISE_ID + " INTEGER NOT NULL," +
                    WorkoutListEntry.COLUMN_DATE_ADDED + " INTEGER," +
                    "FOREIGN KEY (" + WorkoutListEntry.COLUMN_WORKOUT_ID + ") " +
                    "REFERENCES " + WorkoutEntry.TABLE_NAME + "(" + WorkoutEntry._ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + WorkoutListEntry.COLUMN_EXERCISE_ID + ") " +
                    "REFERENCES " + ExerciseEntry.TABLE_NAME + "(" + ExerciseEntry._ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_WORKOUT_LIST =
            "DROP TABLE IF EXISTS " + WorkoutListEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM =
            "CREATE TABLE " + ProgramEntry.TABLE_NAME + " (" +
                    ProgramEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ProgramEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    ProgramEntry.COLUMN_DESCRIPTION + " TEXT," +
                    ProgramEntry.COLUMN_NUM_DAYS + " INTEGER NOT NULL," +
                    ProgramEntry.COLUMN_CREATION + " INTEGER )";

    private static final String SQL_DELETE_TABLE_PROGRAM =
            "DROP TABLE IF EXISTS " + ProgramEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM_WORKOUTS =
            "CREATE TABLE " + ProgramWorkoutEntry.TABLE_NAME + " (" +
                    ProgramWorkoutEntry.COLUMN_PROGRAM_ID + " INTEGER NOT NULL," +
                    ProgramWorkoutEntry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL," +
                    ProgramWorkoutEntry.COLUMN_NAME_DAY + " INTEGER NOT NULL," +
                    ProgramWorkoutEntry.COLUMN_DATE_ADDED + " INTEGER," +
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
                    ProgramSessionEntry.COLUMN_TIME_START + " INTEGER NOT NULL," +
                    ProgramSessionEntry.COLUMN_TIME_END + " INTEGER NOT NULL," +
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
            "DROP TABLE IF EXISTS " + ExerciseSessionEntry.TABLE_NAME;

    public WorkoutProgramDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_BASE_EXERCISE);
        insertDefaultExercises(sqLiteDatabase);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT_LIST);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM_WORKOUTS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM_SESSION);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE_SESSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_BASE_EXERCISE);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_EXERCISE);
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
        cv.put(BaseExerciseEntry.COLUMN_NAME, "Squats");
        db.insert(BaseExerciseEntry.TABLE_NAME, null, cv);
        cv.put(BaseExerciseEntry.COLUMN_NAME, "Deadlift");
        db.insert(BaseExerciseEntry.TABLE_NAME, null, cv);
        cv.put(BaseExerciseEntry.COLUMN_NAME, "Overhead Press");
        db.insert(BaseExerciseEntry.TABLE_NAME, null, cv);
    }

}
