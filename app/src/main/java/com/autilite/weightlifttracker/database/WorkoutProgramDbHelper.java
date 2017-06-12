package com.autilite.weightlifttracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.autilite.weightlifttracker.program.Workout;

import java.util.List;

import static com.autilite.weightlifttracker.database.ExerciseInfoContract.ExerciseInfoEntry;
import static com.autilite.weightlifttracker.database.ExerciseStatContract.ExerciseStatEntry;
import static com.autilite.weightlifttracker.database.WorkoutContract.WorkoutEntry;
import static com.autilite.weightlifttracker.database.WorkoutListContract.WorkoutListEntry;
import static com.autilite.weightlifttracker.database.ProgramContract.ProgramEntry;
import static com.autilite.weightlifttracker.database.ProgramWorkoutContract.ProgramWorkoutEntry;
import static com.autilite.weightlifttracker.database.WorkoutSessionContract.WorkoutSessionEntry;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public class WorkoutProgramDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
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
            "DROP TABLE IF EXISTS " + ExerciseStatContract.ExerciseStatEntry.TABLE_NAME;

    private static final String SQL_CREATE_TABLE_PROGRAM =
            "CREATE TABLE " + ProgramEntry.TABLE_NAME + " (" +
                    ProgramEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    ProgramEntry.COLUMN_NAME + " TEXT NOT NULL)";

    private static final String SQL_DELETE_TABLE_PROGRAM =
            "DROP TABLE IF EXISTS " + WorkoutContract.WorkoutEntry.TABLE_NAME;

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

    private static final String SQL_CREATE_TABLE_WORKOUT_SESSION =
            "CREATE TABLE " + WorkoutSessionEntry.TABLE_NAME+ " (" +
                    WorkoutSessionEntry.COLUMN_PROGRAM_ID + " INTEGER NOT NULL," +
                    WorkoutSessionEntry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL," +
                    WorkoutSessionEntry.COLUMN_EXERCISE_ID + " INTEGER NOT NULL," +
                    WorkoutSessionEntry.COLUMN_DATE + " TEXT NOT NULL," +
                    WorkoutSessionEntry.COLUMN_SET_NUMBER + " INTEGER NOT NULL," +
                    WorkoutSessionEntry.COLUMN_SUCCESSFUL_REPS + " INTEGER NOT NULL," +
                    WorkoutSessionEntry.COLUMN_IS_SET_SUCCESSFUL + " INTEGER NOT NULL DEFAULT 0," +
                    WorkoutSessionEntry.COLUMN_WEIGHT + " REAL," +
                    "PRIMARY KEY ("+ WorkoutSessionEntry.COLUMN_PROGRAM_ID +", " +
                    WorkoutSessionEntry.COLUMN_WORKOUT_ID + "," +
                    WorkoutSessionEntry.COLUMN_EXERCISE_ID + "," +
                    WorkoutSessionEntry.COLUMN_DATE + "," +
                    WorkoutSessionEntry.COLUMN_SET_NUMBER + ")," +
                    "FOREIGN KEY (" + WorkoutSessionEntry.COLUMN_PROGRAM_ID + ") " +
                    "REFERENCES " + ProgramWorkoutEntry.TABLE_NAME + " (" +
                    ProgramWorkoutEntry.COLUMN_PROGRAM_ID +") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + WorkoutSessionEntry.COLUMN_WORKOUT_ID +") " +
                    "REFERENCES " + ProgramWorkoutEntry.TABLE_NAME +
                    " (" + ProgramWorkoutEntry.COLUMN_WORKOUT_ID + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "FOREIGN KEY (" + WorkoutSessionEntry.COLUMN_EXERCISE_ID +")" +
                    " REFERENCES " + ExerciseStatEntry.TABLE_NAME +
                    " ("+ ExerciseStatEntry._ID +") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE)";

    private static final String SQL_DELETE_TABLE_WORKOUT_SESSION =
            "DROP TABLE IF EXISTS " + WorkoutSessionEntry.TABLE_NAME;

    public WorkoutProgramDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE_INFO);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_EXERCISE_STATS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT_LIST);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PROGRAM_WORKOUTS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_WORKOUT_SESSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public boolean insertWorkout(String workoutName, String exerciseName, int sets, int reps, float weight) {
        SQLiteDatabase db = getWritableDatabase();

        // Check if workout exists
        // If it doesn't already exist, then make it
        // Insert the exercise

//        ContentValues cv = new ContentValues();
//        cv.put(ExerciseStatContract.ExerciseStatEntry.COLUMN_WORKOUT_NAME, workoutName);
//        cv.put(COLUMN_EXERCISE_ID, exerciseName);
//        cv.put(ExerciseStatContract.ExerciseStatEntry.COLUMN_SET, sets);
//        cv.put(ExerciseStatContract.ExerciseStatEntry.COLUMN_REP, reps);
//        if (weight > 0) {
//            cv.put(ExerciseStatContract.ExerciseStatEntry.COLUMN_WEIGHT, weight);
//        }
//
//        long rowId = db.insert(ExerciseStatContract.ExerciseStatEntry.TABLE_NAME, null, cv);
//        return rowId != -1;
        return false;
    }

    public List<Workout> getWorkouts(){
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELELCT * FROM " + ExerciseStatContract.ExerciseStatEntry.TABLE_NAME, null);
//        while (cursor.moveToNext()){
//            Workout workout = new Workout(name);
//            String name = cursor.getString(
//                    cursor.getColumnIndexOrThrow(ExerciseStatContract.ExerciseStatEntry.COLUMN_WORKOUT_NAME));
//
//            cursor.moveToNext();
//        }
        return null;
    }
}
