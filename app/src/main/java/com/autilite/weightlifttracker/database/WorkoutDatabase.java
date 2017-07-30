package com.autilite.weightlifttracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.exception.SQLiteInsertException;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;
import com.autilite.weightlifttracker.program.session.ExerciseSession;
import com.autilite.weightlifttracker.program.session.SetSession;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.autilite.weightlifttracker.database.ExerciseInfoContract.ExerciseInfoEntry;
import static com.autilite.weightlifttracker.database.ExerciseStatContract.ExerciseStatEntry;
import static com.autilite.weightlifttracker.database.WorkoutContract.WorkoutEntry;
import static com.autilite.weightlifttracker.database.WorkoutListContract.WorkoutListEntry;
import static com.autilite.weightlifttracker.database.ProgramContract.ProgramEntry;
import static com.autilite.weightlifttracker.database.ProgramWorkoutContract.ProgramWorkoutEntry;
import static com.autilite.weightlifttracker.database.ProgramSessionContract.ProgramSessionEntry;
import static com.autilite.weightlifttracker.database.ExerciseSessionContract.ExerciseSessionEntry;

/**
 * Created by Kelvin on Jul 19, 2017.
 */

public class WorkoutDatabase {
    private WorkoutProgramDbHelper workoutDb;
    private SQLiteDatabase db;

    public WorkoutDatabase(Context context) {
        workoutDb = new WorkoutProgramDbHelper(context);
        db = workoutDb.getWritableDatabase();
    }

    public void close() {
        workoutDb.close();
    }

    public long createProgram(String programName, int numDays) {
        ContentValues cv = new ContentValues();
        cv.put(ProgramEntry.COLUMN_NAME, programName);
        // TODO cv.put(ProgramEntry.COLUMN_DESCRIPTION, description);
        cv.put(ProgramEntry.COLUMN_NUM_DAYS, numDays);
        cv.put(ProgramEntry.COLUMN_CREATION, System.currentTimeMillis());
        return db.insert(ProgramEntry.TABLE_NAME, null, cv);
    }

    public long createWorkout(String workoutName, String description) {
        ContentValues cv = new ContentValues();
        cv.put(WorkoutEntry.COLUMN_NAME, workoutName);
        cv.put(WorkoutEntry.COLUMN_DESCRIPTION, description);
        cv.put(WorkoutEntry.COLUMN_CREATION, System.currentTimeMillis());
        return db.insert(WorkoutEntry.TABLE_NAME, null, cv);
    }

    public long createExerciseStat(long exerciseId,  int sets, int reps, double weight, double autoInc, int restTime) {
        ContentValues cv = new ContentValues();
        cv.put(ExerciseStatEntry.COLUMN_EXERCISE_ID, exerciseId);
        cv.put(ExerciseStatEntry.COLUMN_SET, sets);
        cv.put(ExerciseStatEntry.COLUMN_REP, reps);
        cv.put(ExerciseStatEntry.COLUMN_WEIGHT, weight);
        cv.put(ExerciseStatEntry.COLUMN_AUTOINC, autoInc);
        cv.put(ExerciseStatEntry.COLUMN_REST_TIME, restTime);
        cv.put(ExerciseStatEntry.COLUMN_CREATION, System.currentTimeMillis());
        return db.insert(ExerciseStatEntry.TABLE_NAME, null, cv);
    }

    public long createExerciseStat(long exerciseId,  int sets, int reps, double weight, double autoInc) {
        ContentValues cv = new ContentValues();
        cv.put(ExerciseStatEntry.COLUMN_EXERCISE_ID, exerciseId);
        cv.put(ExerciseStatEntry.COLUMN_SET, sets);
        cv.put(ExerciseStatEntry.COLUMN_REP, reps);
        cv.put(ExerciseStatEntry.COLUMN_WEIGHT, weight);
        cv.put(ExerciseStatEntry.COLUMN_AUTOINC, autoInc);
        cv.put(ExerciseStatEntry.COLUMN_CREATION, System.currentTimeMillis());
        return db.insert(ExerciseStatEntry.TABLE_NAME, null, cv);
    }

    public long createExerciseStat(long exerciseId, int sets, int reps, double weight) {
        ContentValues cv = new ContentValues();
        cv.put(ExerciseStatEntry.COLUMN_EXERCISE_ID, exerciseId);
        cv.put(ExerciseStatEntry.COLUMN_SET, sets);
        cv.put(ExerciseStatEntry.COLUMN_REP, reps);
        cv.put(ExerciseStatEntry.COLUMN_WEIGHT, weight);
        cv.put(ExerciseStatEntry.COLUMN_CREATION, System.currentTimeMillis());
        return db.insert(ExerciseStatEntry.TABLE_NAME, null, cv);
    }

    public int updateExerciseStat(long exerciseStatId, long exerciseId,  int sets, int reps, double weight, double autoInc, int restTime) {
        ContentValues cv = new ContentValues();
        cv.put(ExerciseStatEntry._ID, exerciseStatId);
        cv.put(ExerciseStatEntry.COLUMN_EXERCISE_ID, exerciseId);
        cv.put(ExerciseStatEntry.COLUMN_SET, sets);
        cv.put(ExerciseStatEntry.COLUMN_REP, reps);
        cv.put(ExerciseStatEntry.COLUMN_WEIGHT, weight);
        cv.put(ExerciseStatEntry.COLUMN_AUTOINC, autoInc);
        cv.put(ExerciseStatEntry.COLUMN_REST_TIME, restTime);
        cv.put(ExerciseStatEntry.COLUMN_CREATION, System.currentTimeMillis());
        String whereClause = ExerciseStatEntry._ID + "=" + exerciseStatId;
        return db.update(ExerciseStatEntry.TABLE_NAME, cv, whereClause, null);
    }

    public long addExerciseToWorkout(long workoutId, long exerciseStatId) {
        ContentValues cv = new ContentValues();
        cv.put(WorkoutListEntry.COLUMN_WORKOUT_ID, workoutId);
        cv.put(WorkoutListEntry.COLUMN_EXERCISE_ID, exerciseStatId);
        cv.put(WorkoutListEntry.COLUMN_DATE_ADDED, System.currentTimeMillis());
        return db.insert(WorkoutListEntry.TABLE_NAME, null, cv);
    }

    public boolean addWorkoutToProgram(long programId, long workoutId, int day) {
        ContentValues cv = new ContentValues();
        cv.put(ProgramWorkoutEntry.COLUMN_PROGRAM_ID, programId);
        cv.put(ProgramWorkoutEntry.COLUMN_WORKOUT_ID, workoutId);
        cv.put(ProgramWorkoutEntry.COLUMN_NAME_DAY, day);
        cv.put(ProgramWorkoutEntry.COLUMN_DATE_ADDED, System.currentTimeMillis());
        return db.insert(ProgramWorkoutEntry.TABLE_NAME, null, cv) != -1;
    }

    public boolean addSession(long progId, int progDay, long timeStart, long timeEnd, Map<Workout, ArrayList<? extends ExerciseSession>> session) {
        try {
            db.beginTransaction();
            // Add the program session
            ContentValues cv = new ContentValues();
            cv.put(ProgramSessionEntry.COLUMN_PROGRAM_ID, progId);
            cv.put(ProgramSessionEntry.COLUMN_PROGRAM_DAY, progDay);
            cv.put(ProgramSessionEntry.COLUMN_TIME_START, timeStart);
            cv.put(ProgramSessionEntry.COLUMN_TIME_END, timeEnd);
            long sessionId = db.insertOrThrow(ProgramSessionEntry.TABLE_NAME, null, cv);
            // Ensure this program insert is successful
            if (sessionId == -1) {
                throw new SQLiteInsertException("Could not insert ProgramSession entry");
            }

            // Iterate each entry of the session map
            for (Map.Entry<Workout, ArrayList<? extends ExerciseSession>> entry : session.entrySet()) {
                Workout w = entry.getKey();
                ArrayList<? extends ExerciseSession> sessions = entry.getValue();
                for (ExerciseSession es : sessions) {
                    for (SetSession set : es.getSetSessions()) {
                        if (!es.isSetComplete(set)) {
                            continue;
                        }
                        cv.clear();
                        cv.put(ExerciseSessionEntry.COLUMN_PROGRAM_SESSION_ID, sessionId);
                        cv.put(ExerciseSessionEntry.COLUMN_WORKOUT_ID, w.getId());
                        cv.put(ExerciseSessionEntry.COLUMN_EXERCISE_ID, es.getExercise().getId());
                        cv.put(ExerciseSessionEntry.COLUMN_SET_NUMBER, set.getSetNumber());
                        cv.put(ExerciseSessionEntry.COLUMN_SUCCESSFUL_REPS, set.getReps());
                        cv.put(ExerciseSessionEntry.COLUMN_WEIGHT, set.getWeight());
                        cv.put(ExerciseSessionEntry.COLUMN_IS_SET_SUCCESSFUL, es.isSetSuccessful(set));

                        long esId = db.insertOrThrow(ExerciseSessionEntry.TABLE_NAME, null, cv);
                        if (esId == -1) {
                            throw new SQLiteInsertException("Could not insert ProgramSession entry");
                        }
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }

    public Cursor getAllExerciseInfo() {
        String sqlGetAllExerciseInfo = "select * from " + ExerciseInfoEntry.TABLE_NAME;
        return db.rawQuery(sqlGetAllExerciseInfo, null);
    }

    /**
     * Returns a cursor for all rows in the Workouts table
     * @return Cursor
     */
    public Cursor getAllWorkouts() {
        String sql = "select * from " + WorkoutEntry.TABLE_NAME;
        return db.rawQuery(sql, null);
    }

    /**
     * Returns a cursor for all rows in the Programs table
     * @return Cursor
     */
    public Cursor getAllPrograms() {
        String sql = "select * from " + ProgramEntry.TABLE_NAME;
        return db.rawQuery(sql, null);
    }

    /**
     * Selects the table with ExerciseStat.ID, ExerciseInfo.Name, Set, Rep, Weight for the
     * workout
     * @param workoutId The workout id to query the exercises
     * @return
     */
    public Cursor getAllExerciseStatForWorkout(long workoutId) {
        String sql = "SELECT " + ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry._ID + ", " +
                ExerciseInfoEntry.TABLE_NAME + "." + ExerciseInfoEntry._ID + ", " +
                ExerciseInfoEntry.TABLE_NAME + "." + ExerciseInfoEntry.COLUMN_NAME + ", " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_SET + ", " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_REP + ", " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_WEIGHT + ", " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_AUTOINC + ", " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_REST_TIME + ", " +
                WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID + " " +
                "FROM " + WorkoutEntry.TABLE_NAME + " " +
                "INNER JOIN  " + WorkoutListEntry.TABLE_NAME + " ON " +
                WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID +
                " = " + WorkoutListEntry.TABLE_NAME + "." + WorkoutListEntry.COLUMN_WORKOUT_ID + " " +
                "INNER JOIN " + ExerciseStatEntry.TABLE_NAME + " ON " +
                WorkoutListEntry.TABLE_NAME+ "." + WorkoutListEntry.COLUMN_EXERCISE_ID +
                " = " + ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry._ID + " " +
                "INNER JOIN " + ExerciseInfoEntry.TABLE_NAME + " ON " +
                ExerciseStatEntry.TABLE_NAME + "." + ExerciseStatEntry.COLUMN_EXERCISE_ID +
                " = " + ExerciseInfoEntry.TABLE_NAME + "." + ExerciseInfoEntry._ID + " " +
                "WHERE " + WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID + "=" + workoutId +
                ";";
        return db.rawQuery(sql, null);
    }

    /**
     * Returns the {@link ProgramWorkoutEntry} table joined with {@link WorkoutEntry} for progId
     *
     * @param progId The id to select
     * @return
     */
    public Cursor getProgramWorkoutTableJoinedWithName(long progId) {
        String sql = "SELECT * FROM " + ProgramWorkoutEntry.TABLE_NAME + " " +
                "INNER JOIN " + WorkoutEntry.TABLE_NAME + " ON " +
                ProgramWorkoutEntry.TABLE_NAME + "." + ProgramWorkoutEntry.COLUMN_WORKOUT_ID +
                " = " + WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID + " " +
                "WHERE " + ProgramWorkoutEntry.TABLE_NAME + "." + ProgramWorkoutEntry.COLUMN_PROGRAM_ID +
                " = " + progId +
                ";";
        return db.rawQuery(sql, null);
    }

    /**
     * Returns the {@link ProgramWorkoutEntry} table joined with {@link WorkoutEntry} for progId
     *
     * @param progId The id to select
     * @return
     */
    public Cursor getProgramWorkoutTableJoinedWithName(long progId, int day) {
        String sql = "SELECT * FROM " + ProgramWorkoutEntry.TABLE_NAME + " " +
                "INNER JOIN " + WorkoutEntry.TABLE_NAME + " ON " +
                ProgramWorkoutEntry.TABLE_NAME + "." + ProgramWorkoutEntry.COLUMN_WORKOUT_ID +
                " = " + WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID + " " +
                "WHERE " + ProgramWorkoutEntry.TABLE_NAME + "." + ProgramWorkoutEntry.COLUMN_PROGRAM_ID +
                " = " + progId + " " +
                "AND " + ProgramWorkoutEntry.TABLE_NAME +  "." + ProgramWorkoutEntry.COLUMN_NAME_DAY +
                " = " + day +
                ";";
        return db.rawQuery(sql, null);
    }

    public List<Program> getAllProgramsList() {
        List<Program> listOfPrograms = new ArrayList<>();
        // Get Cursor of all program IDs
        Cursor programs = getAllPrograms();

        // For each programID, grab all its workouts
        while(programs.moveToNext()) {
            // Get program info
            long progId = programs.getLong(programs.getColumnIndex(ProgramContract.ProgramEntry._ID));
            String progName = programs.getString(programs.getColumnIndex(ProgramContract.ProgramEntry.COLUMN_NAME));
            Program p = new Program(progId, progName, "");

            // Grab workouts associated with the program
            Cursor programWorkouts = getProgramWorkoutTableJoinedWithName(progId);
            while(programWorkouts.moveToNext()) {
                // TODO add robust way of getting column index
                long workoutId = programWorkouts.getLong(programWorkouts.getColumnIndex(
                        ProgramWorkoutContract.ProgramWorkoutEntry.COLUMN_WORKOUT_ID));
                String workoutName = programWorkouts.getString(programWorkouts.getColumnIndex(
                        WorkoutContract.WorkoutEntry.COLUMN_NAME));
                String workoutDescription = programWorkouts.getString(programWorkouts.getColumnIndex(
                        WorkoutEntry.COLUMN_DESCRIPTION));
                Workout w = new Workout(workoutId, workoutName, workoutDescription);
                p.addWorkout(w);
            }
            listOfPrograms.add(p);
            programWorkouts.close();
        }
        programs.close();

        return listOfPrograms;
    }

    public List<Workout> getProgramWorkouts(long programId) {
        // Get cursor with all workout Ids
        Cursor workoutCursor = getProgramWorkoutTableJoinedWithName(programId);
        List<Workout> workouts = new ArrayList<>();

        // Go through each of the workoutId
        while (workoutCursor.moveToNext()) {
            long workoutId = workoutCursor.getLong(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry._ID));
            String workoutName = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NAME));
            String workoutDescription = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutEntry.COLUMN_DESCRIPTION));
            Workout w = new Workout(workoutId, workoutName, workoutDescription);

            // Get list of exercise for workoutId
            Cursor eStat = getAllExerciseStatForWorkout(workoutId);
            while (eStat.moveToNext()) {
                long id = eStat.getLong(0);
                long baseExerciseId = eStat.getLong(1);
                String exerciseName = eStat.getString(2);
                int set = eStat.getInt(3);
                int rep = eStat.getInt(4);
                double weight = eStat.getDouble(5);
                double autoIncr = eStat.getDouble(6);
                int restTime = eStat.getInt(7);
                Exercise e = new Exercise(id, exerciseName, "", baseExerciseId, set, rep, weight, autoIncr, restTime);
                w.addExercise(e);
            }
            workouts.add(w);
            eStat.close();

        }
        workoutCursor.close();
        return workouts;
    }

    public List<Workout> getProgramWorkouts(long programId, int programDay) {
        // Get cursor with all workout Ids
        Cursor workoutCursor = getProgramWorkoutTableJoinedWithName(programId, programDay);
        List<Workout> workouts = new ArrayList<>();

        // Go through each of the workoutId
        while (workoutCursor.moveToNext()) {
            long workoutId = workoutCursor.getLong(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry._ID));
            String workoutName = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NAME));
            String workoutDescription = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutEntry.COLUMN_DESCRIPTION));
            Workout w = new Workout(workoutId, workoutName, workoutDescription);

            // Get list of exercise for workoutId
            Cursor eStat = getAllExerciseStatForWorkout(workoutId);
            while (eStat.moveToNext()) {
                long id = eStat.getLong(0);
                long baseExerciseId = eStat.getLong(1);
                String exerciseName = eStat.getString(2);
                int set = eStat.getInt(3);
                int rep = eStat.getInt(4);
                double weight = eStat.getDouble(5);
                double autoIncr = eStat.getDouble(6);
                int restTime = eStat.getInt(7);
                Exercise e = new Exercise(id, exerciseName, "", baseExerciseId,set, rep, weight, autoIncr, restTime);
                w.addExercise(e);
            }
            workouts.add(w);
            eStat.close();

        }
        workoutCursor.close();
        return workouts;
    }

    public List<Workout> getAllWorkoutsList() {
        // Get cursor with all workout Ids
        Cursor workoutCursor = getAllWorkouts();
        List<Workout> workouts = new ArrayList<>();

        // Go through each of the workoutId
        while (workoutCursor.moveToNext()) {
            long workoutId = workoutCursor.getLong(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry._ID));
            String workoutName = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NAME));
            String workoutDescription = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutEntry.COLUMN_DESCRIPTION));
            Workout w = new Workout(workoutId, workoutName, workoutDescription);

            // Get list of exercise for workoutId
            Cursor eStat = getAllExerciseStatForWorkout(workoutId);
            while (eStat.moveToNext()) {
                long id = eStat.getLong(0);
                long baseExerciseId = eStat.getLong(1);
                String exerciseName = eStat.getString(2);
                int set = eStat.getInt(3);
                int rep = eStat.getInt(4);
                double weight = eStat.getDouble(4);
                double autoIncr = eStat.getDouble(6);
                int restTime = eStat.getInt(7);
                Exercise e = new Exercise(id, exerciseName, "", baseExerciseId, set, rep, weight, autoIncr, restTime);
                w.addExercise(e);
            }
            workouts.add(w);
            eStat.close();

        }
        workoutCursor.close();
        return workouts;
    }

    public List<Exercise> getAllExerciseInfoList (long workoutId) {
        List<Exercise> list = new LinkedList<>();

        // Get list of exercise for workoutId
        Cursor eStat = getAllExerciseStatForWorkout(workoutId);
        while (eStat.moveToNext()) {
            long id = eStat.getLong(0);
            long baseExerciseId = eStat.getLong(1);
            String exerciseName = eStat.getString(2);
            int set = eStat.getInt(3);
            int rep = eStat.getInt(4);
            double weight = eStat.getDouble(4);
            double autoIncr = eStat.getDouble(6);
            int restTime = eStat.getInt(7);
            Exercise e = new Exercise(id, exerciseName, "", baseExerciseId, set, rep, weight, autoIncr, restTime);
            list.add(e);
        }
        eStat.close();
        return list;
    }

    /**
     * Returns the name associated with progId or null if it doesn't exist
     *
     * @param progId The id used to query the database
     * @return The program name or null
     */
    public String getProgramName(long progId) {
        String sql = "SELECT " + ProgramEntry.COLUMN_NAME + " FROM " + ProgramEntry.TABLE_NAME +
                " WHERE " + ProgramEntry._ID + "=" + progId;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToNext();
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
    }

    /**
     * Returns the number of programs day for the program with id <code>progId</code> or -1
     * if it doesn't exist
     *
     * @param progId The id used to query the database
     * @return The number of days or -1
     */
    public int getProgramNumDays(long progId) {
        String sql = "SELECT " + ProgramEntry.COLUMN_NUM_DAYS + " FROM " + ProgramEntry.TABLE_NAME +
                " WHERE " + ProgramEntry._ID + "=" + progId;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return -1;
        } else {
            cursor.moveToNext();
            int numDays = cursor.getInt(0);
            cursor.close();
            return numDays;
        }
    }

    public boolean checkIfWorkoutExist(long workoutId) {
        boolean exist;
        String sql = "SELECT * FROM " + WorkoutEntry.TABLE_NAME +
                " WHERE " + WorkoutEntry._ID  + " = " + workoutId;
        Cursor cursor = db.rawQuery(sql, null);

        // workoutId exist if it can find a result in the Workout table
        exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    /**
     * Get the program day for the program with id <code>programId</code>
     *
     * @param context
     * @param programId
     * @return
     */
    public int getProgramDay(Context context, long programId) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.program_preference_file_key), Context.MODE_PRIVATE);
        // Since this function take the subsequent program day, we set the default return value
        // to be 0 so it can be incremented to 1
        int previousDay = sharedPrefs.getInt(getProgramDayPrefKey(context, programId), 0);
        int numDays = getProgramNumDays(programId);

        // The next day is the current day + 1 unless if the last program day is the last day in the
        // program. In which case, we look back to day 1
        return (previousDay % numDays) + 1;
    }

    /**
     * Set the program's last completed Program Day to <code>programDay</code>
     *
     * @param context
     * @param programId
     */
    public void setPreviousProgDay(Context context, long programId, int programDay) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                context.getString(R.string.program_preference_file_key),
                Context.MODE_PRIVATE).edit();
        editor.putInt(getProgramDayPrefKey(context, programId), programDay);
        editor.apply();
    }

    private String getProgramDayPrefKey(Context context, long programId) {
        return context.getString(R.string.last_program_day) + "_" + programId;
    }
}
