package com.autilite.plan_g.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.AbstractBaseModelFragment;
import com.autilite.plan_g.fragment.EditExerciseStatFragment;
import com.autilite.plan_g.program.BaseExercise;
import com.autilite.plan_g.program.BaseModel;
import com.autilite.plan_g.program.Exercise;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditExerciseStat extends CreateForm {
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
        return db.deleteExercise(model.getId());
    }

    @Override
    protected BaseModel insertNewEntry(Bundle fields) {
        BaseExercise baseExercise = fields.getParcelable(EditExerciseStatFragment.FIELD_KEY_BASE_EXERCISE);
        String name = fields.getString(EditExerciseStatFragment.FIELD_KEY_NAME);
        String note = fields.getString(EditExerciseStatFragment.FIELD_KEY_DESCRIPTION);
        int sets = fields.getInt(EditExerciseStatFragment.FIELD_KEY_SETS);
        int reps = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS);
        double weight = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT);
        double autoIncr = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_AUTO_INCR);
        int restTime = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REST_TIMER);

        if (baseExercise == null) {
            return null;
        }

        long id = db.createExercise(baseExercise.getId(), sets, reps, weight, autoIncr, restTime);
        if (id != -1) {
            return new Exercise(id, name, note, baseExercise.getId(), sets, reps, weight, autoIncr, restTime);
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
        double weight = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT);
        double autoIncr = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_AUTO_INCR);
        int restTime = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REST_TIMER);

        if (baseExercise == null) {
            return null;
        }

        int numRowsUpdate = db.updateExercise(
                exercise.getId(), baseExercise.getId(), sets, reps, weight, autoIncr, restTime);
        if (numRowsUpdate == 1) {
            exercise.setBaseExerciseId(baseExercise.getId());
            exercise.setName(name);
            exercise.setDescription(note);
            exercise.setSets(sets);
            exercise.setReps(reps);
            exercise.setWeight(weight);
            exercise.setWeightIncrement(autoIncr);
            exercise.setRestTime(restTime);
            return exercise;
        }
        return null;
    }

}
