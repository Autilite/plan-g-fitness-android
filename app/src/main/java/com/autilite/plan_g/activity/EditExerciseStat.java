package com.autilite.plan_g.activity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.autilite.plan_g.R;
import com.autilite.plan_g.database.ExerciseContract;
import com.autilite.plan_g.fragment.AbstractBaseModelFragment;
import com.autilite.plan_g.fragment.EditExerciseStatFragment;
import com.autilite.plan_g.program.BaseExercise;
import com.autilite.plan_g.program.BaseModel;
import com.autilite.plan_g.program.Exercise;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditExerciseStat extends CreateForm implements EditExerciseStatFragment.OnEditExerciseFragmentInteractionListener {
    private static final String TAG = EditExerciseStat.class.getName();
    private Exercise exercise;

    public EditExerciseStat() {
    }

    @Override
    protected AbstractBaseModelFragment getCreateModelInstance() {
        setTitle(R.string.create_exercise);
        return EditExerciseStatFragment.newInstance(null);
    }

    @Override
    protected AbstractBaseModelFragment getEditModelInstance(@NonNull BaseModel model) {
        setTitle(R.string.edit_exercise);
        exercise = (Exercise) model;
        return EditExerciseStatFragment.newInstance(exercise);
    }

    @Override
    protected boolean onDeleteEntry(@NonNull BaseModel model) {
        // TODO only delete exercise from the workout list
        return db.deleteExercise(model.getId());
    }

    @Override
    protected BaseModel insertNewEntry(Bundle fields) {
        BaseExercise baseExercise = fields.getParcelable(EditExerciseStatFragment.FIELD_KEY_BASE_EXERCISE);
        String name = fields.getString(EditExerciseStatFragment.FIELD_KEY_NAME);
        String note = fields.getString(EditExerciseStatFragment.FIELD_KEY_DESCRIPTION);
        int sets = fields.getInt(EditExerciseStatFragment.FIELD_KEY_SETS);
        int reps = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS);
        int repsMin = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_MIN);
        int repsMax = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_MAX);
        int repsIncr = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_INCR);
        double weight = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT);
        double weightIncr = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT_INCR);
        int restTime = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REST_TIMER);

        if (baseExercise == null) {
            return null;
        }

        long id = db.createExercise(baseExercise.getId(), sets, reps, repsMin, repsMax, repsIncr, weight, weightIncr, restTime);
        if (id != -1) {
            return new Exercise(id, name, note, baseExercise.getId(), sets, reps, repsMin, repsMax, repsIncr, weight, weightIncr, restTime);
        }
        return null;
    }

    @Override
    protected BaseModel editEntry(Bundle fields) {
        BaseExercise baseExercise = fields.getParcelable(EditExerciseStatFragment.FIELD_KEY_BASE_EXERCISE);
        String name = fields.getString(EditExerciseStatFragment.FIELD_KEY_NAME);
        String note = fields.getString(EditExerciseStatFragment.FIELD_KEY_DESCRIPTION);
        int sets = fields.getInt(EditExerciseStatFragment.FIELD_KEY_SETS);
        int reps = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS);
        int repsMin = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_MIN);
        int repsMax = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_MAX);
        int repsIncr = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS_INCR);
        double weight = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT);
        double weightIncr = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT_INCR);
        int restTime = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REST_TIMER);

        if (baseExercise == null) {
            return null;
        }

        try {
            int numRowsUpdate = db.updateExercise(
                    exercise.getId(), baseExercise.getId(), sets, reps, repsMin, repsMax, repsIncr, weight, weightIncr, restTime);
            if (numRowsUpdate == 1) {
                exercise.setBaseExerciseId(baseExercise.getId());
                exercise.setName(name);
                exercise.setDescription(note);
                exercise.setSets(sets);
                exercise.setRepRange(reps, repsMin, repsMax);
                exercise.setRepsIncrement(repsIncr);
                exercise.setWeight(weight);
                exercise.setWeightIncrement(weightIncr);
                exercise.setRestTime(restTime);
                return exercise;
            }
        } catch (SQLiteConstraintException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalArgumentException e) {
            // The database was successfully able to update the exercise but updating the model was
            // unsuccessful. This is likely due to an inconsistency of constraints.
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    public void onChooseExercise() {
        selectBaseExercise();
    }

    private void selectBaseExercise() {
        final Cursor cursor = db.getBaseExerciseTable();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCursor(cursor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cursor.moveToPosition(i);
                        long baseExerciseId = cursor.getLong(cursor.getColumnIndex(ExerciseContract.BaseExerciseEntry._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ExerciseContract.BaseExerciseEntry.COLUMN_NAME));
                        String description = cursor.getString(cursor.getColumnIndex(ExerciseContract.BaseExerciseEntry.COLUMN_DESCRIPTION));
                        cursor.close();

                        BaseExercise baseExercise = new BaseExercise(baseExerciseId, name, description);

                        EditExerciseStatFragment exerciseStatFragment = (EditExerciseStatFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                        if (exerciseStatFragment != null) {
                            exerciseStatFragment.updateExerciseView(baseExercise);
                        } else {
                            Log.w(TAG, EditExerciseStatFragment.class.getName() + " could not be found.");
                        }
                    }
                }, ExerciseContract.BaseExerciseEntry.COLUMN_NAME).create();
        dialog.show();
    }
}
