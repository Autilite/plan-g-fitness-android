package com.autilite.plan_g.activity;

import android.os.Bundle;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.AbstractBaseModelFragment;
import com.autilite.plan_g.fragment.EditExerciseStatFragment;
import com.autilite.plan_g.program.BaseExercise;
import com.autilite.plan_g.program.BaseModel;
import com.autilite.plan_g.program.Exercise;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditExerciseStat extends CreateForm implements EditExerciseStatFragment.OnFragmentInteractionListener {
    public static final String EXTRA_EXERCISE = "EXTRA_EXERCISE";

    private Exercise exercise;

    public EditExerciseStat() {
    }

    @Override
    protected AbstractBaseModelFragment createContentFragment() {
        if (getIntent().getExtras() != null) {
            setTitle(R.string.edit_exercise);
            formType = Type.EDIT;
            exercise = getIntent().getParcelableExtra(EXTRA_EXERCISE);
            return EditExerciseStatFragment.newInstance(exercise);
        } else {
            setTitle(R.string.create_exercise);
            formType = Type.CREATE;
            return EditExerciseStatFragment.newInstance(null);
        }
    }

    @Override
    protected boolean onDeleteEntryCallback() {
        return false;
    }

    @Override
    public BaseModel onSave(Bundle fields) {
        BaseExercise baseExercise = fields.getParcelable(EditExerciseStatFragment.FIELD_KEY_BASE_EXERCISE);
        String name = fields.getString(EditExerciseStatFragment.FIELD_KEY_NAME);
        String note = fields.getString(EditExerciseStatFragment.FIELD_KEY_DESCRIPTION);
        int sets = fields.getInt(EditExerciseStatFragment.FIELD_KEY_SETS);
        int reps = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REPS);
        double weight = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_WEIGHT);
        double autoIncr = fields.getDouble(EditExerciseStatFragment.FIELD_KEY_AUTO_INCR);
        int restTime = fields.getInt(EditExerciseStatFragment.FIELD_KEY_REST_TIMER);

        if (formType == Type.CREATE) {
            exercise = insertNewEntry(baseExercise, sets, reps, weight, autoIncr, restTime);
        } else {
            exercise = editEntry(baseExercise, sets, reps, weight, autoIncr, restTime);
        }
        return exercise;
    }

    private Exercise insertNewEntry(BaseExercise baseExercise, int sets, int reps, double weight, double autoIncr, int restTime) {
        long id = db.createExercise(baseExercise.getId(), sets, reps, weight, autoIncr, restTime);
        if (id != -1) {
            return new Exercise(id, baseExercise.getName(), "", baseExercise.getId(), sets, reps, weight, autoIncr, restTime);
        }
        return null;
    }

    private Exercise editEntry(BaseExercise baseExercise, int sets, int reps, double weight, double autoIncr, int restTime) {
        int numRowsUpdate = db.updateExercise(
                exercise.getId(), baseExercise.getId(), sets, reps, weight, autoIncr, restTime);
        if (numRowsUpdate == 1) {
            return new Exercise(exercise.getId(), baseExercise.getName(), "", baseExercise.getId(), sets, reps, weight, autoIncr, restTime);
        }
        return null;
    }

}
