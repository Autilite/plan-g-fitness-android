package com.autilite.plan_g.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.AbstractBaseModelFragment;
import com.autilite.plan_g.fragment.EditWorkoutFragment;
import com.autilite.plan_g.program.BaseModel;
import com.autilite.plan_g.program.Exercise;
import com.autilite.plan_g.program.Workout;

import java.util.List;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditWorkout extends CreateForm {
    public static final String EXTRA_WORKOUT = "EXTRA_WORKOUT";

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
    protected BaseModel insertNewEntry(Bundle fields) {
        String name = fields.getString(AbstractBaseModelFragment.FIELD_KEY_NAME);
        String description = fields.getString(AbstractBaseModelFragment.FIELD_KEY_DESCRIPTION);
        List<Exercise> exercises = fields.getParcelableArrayList(EditWorkoutFragment.FIELD_KEY_EXERCISES);

        if (name == null || name.equals("") || exercises == null) {
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

    @Override
    protected BaseModel editEntry(Bundle fields) {
        String name = fields.getString(AbstractBaseModelFragment.FIELD_KEY_NAME);
        String description = fields.getString(AbstractBaseModelFragment.FIELD_KEY_DESCRIPTION);
        List<Exercise> exercises = fields.getParcelableArrayList(EditWorkoutFragment.FIELD_KEY_EXERCISES);

        if (name == null || name.equals("")) {
            return null;
        }

        if (db.updateWorkout(workout.getId(), name, description, exercises)) {
            workout.setName(name);
            workout.setDescription(description);
            workout.setExercises(exercises);
            return workout;
        } else {
            return null;
        }
    }
}
