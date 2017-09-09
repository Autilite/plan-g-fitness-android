package com.autilite.plan_g.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.EditExerciseStatFragment;
import com.autilite.plan_g.program.BaseExercise;
import com.autilite.plan_g.program.Exercise;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditExerciseStat extends CreateForm implements EditExerciseStatFragment.OnFragmentInteractionListener {

    public static final String EXTRA_EXERCISE = "EXTRA_EXERCISE";
    public static final String RESULT_ACTION = "com.autilite.plan_g.activity.EditExerciseStat.RESULT_ACTION";
    public static final String EXTRA_RESULT_EXERCISE = "RESULT_EXERCISE";

    private Exercise exercise;

    public EditExerciseStat() {
    }

    @Override
    protected Fragment createContentFragment() {
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
    protected boolean saveForm() {
        EditExerciseStatFragment f = (EditExerciseStatFragment) contentFragment;
        f.passData();
        return saveSuccessful;
    }

    @Override
    public boolean onExerciseSave(BaseExercise baseExercise, String note, int sets, int reps, double weight, double autoIncr, int restTime) {
        if (formType == Type.CREATE) {
            exercise = insertNewEntry(baseExercise, sets, reps, weight, autoIncr, restTime);
        } else {
            exercise = editEntry(baseExercise, sets, reps, weight, autoIncr, restTime);
        }
        saveSuccessful = exercise != null;

        if (!saveSuccessful) {
            Toast.makeText(this, R.string.create_exercise_fail, Toast.LENGTH_SHORT).show();
        } else {
            Intent result = new Intent(RESULT_ACTION);
            result.setAction(RESULT_ACTION);
            result.putExtra(EXTRA_RESULT_EXERCISE, exercise);
            setResult(Activity.RESULT_OK, result);
        }
        return saveSuccessful;
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
