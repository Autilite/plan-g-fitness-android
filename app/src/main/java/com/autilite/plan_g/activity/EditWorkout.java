package com.autilite.plan_g.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.AbstractBaseModelFragment;
import com.autilite.plan_g.fragment.EditWorkoutFragment;
import com.autilite.plan_g.program.Exercise;
import com.autilite.plan_g.program.Workout;

import java.util.List;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditWorkout extends CreateForm implements EditWorkoutFragment.OnFragmentInteractionListener {
    public static final String EXTRA_WORKOUT = "EXTRA_WORKOUT";

    public static final String RESULT_ACTION = "com.autilite.plan_g.activity.EditWorkout.RESULT_ACTION";
    public static final String EXTRA_RESULT_WORKOUT = "RESULT_WORKOUT";

    private Workout workout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected AbstractBaseModelFragment createContentFragment() {
        if (getIntent().getExtras() != null) {
            setTitle(R.string.edit_workout_title);
            formType = Type.EDIT;
            workout = getIntent().getParcelableExtra(EXTRA_WORKOUT);
            return EditWorkoutFragment.newInstance(workout);
        } else {
            setTitle(R.string.create_workout_title);
            formType = Type.CREATE;
            return EditWorkoutFragment.newInstance(null);
        }
    }

    @Override
    protected boolean onDeleteEntryCallback() {
        return false;
    }

    @Override
    public boolean onWorkoutSave(String name, String description, List<Exercise> exercises) {
        if (formType == Type.CREATE) {
            workout = insertNewEntry(name, description, exercises);
        } else {
            workout = editEntry(workout.getId(), name, description, exercises);
        }
        saveSuccessful = workout != null;

        if (!saveSuccessful) {
            Toast.makeText(this, R.string.create_workout_fail, Toast.LENGTH_SHORT).show();
        } else {
            Intent result = new Intent(RESULT_ACTION);
            result.setAction(RESULT_ACTION);
            result.putExtra(EXTRA_RESULT_WORKOUT, workout);
            setResult(Activity.RESULT_OK, result);
        }
        return saveSuccessful;
    }

    private Workout insertNewEntry(String name, String description, List<Exercise> exercises) {
        if (name.equals("")) {
            return null;
        }
        long id = db.createWorkout(name, description);
        if (id == -1) {
            return null;
        }
        Workout workout = new Workout(id, name, description);

        for (Exercise e : exercises) {
            long rowId = db.addExerciseToWorkout(id, e.getId());
            if (rowId != -1) {
                workout.addExercise(e);
            } else {
                String s = String.format(getString(R.string.add_workout_exercise_fail), e.getName());
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }
        }

        return workout;
    }

    private Workout editEntry(long id, String name, String description, List<Exercise> exercises) {
        if (name.equals("")) {
            return null;
        }

        if (db.updateWorkout(id, name, description, exercises)) {
            Workout workout = new Workout(id, name, description);
            workout.setExercises(exercises);
            return workout;
        } else {
            return null;
        }
    }
}
