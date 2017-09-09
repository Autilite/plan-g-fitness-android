package com.autilite.plan_g.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private Workout workout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected AbstractBaseModelFragment getCreateModelInstance() {
        setTitle(R.string.create_workout_title);
        return EditWorkoutFragment.newInstance(null);
    }

    @Override
    protected AbstractBaseModelFragment getEditModelInstance(@NonNull BaseModel model) {
        setTitle(R.string.edit_workout_title);
        workout = (Workout) model;
        return EditWorkoutFragment.newInstance(workout);
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
