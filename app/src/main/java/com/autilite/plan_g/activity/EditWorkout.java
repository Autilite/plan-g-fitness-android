package com.autilite.plan_g.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.AbstractFormFragment;
import com.autilite.plan_g.fragment.EditWorkoutFragment;
import com.autilite.plan_g.program.BaseModel;
import com.autilite.plan_g.program.Workout;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditWorkout extends CreateForm {
    public static final String EXTRA_WORKOUT = "EXTRA_WORKOUT";

    public static final String RESULT_ACTION = "com.autilite.plan_g.activity.EditWorkout.RESULT_ACTION";
    public static final String EXTRA_RESULT_WORKOUT = "RESULT_WORKOUT";

    @Override
    protected AbstractFormFragment createContentFragment() {
        if (getIntent().getExtras() != null) {
            setTitle(R.string.edit_workout_title);
            Workout workout = getIntent().getParcelableExtra(EXTRA_WORKOUT);
            return EditWorkoutFragment.newInstance(workout);
        } else {
            setTitle(R.string.create_workout_title);
            return EditWorkoutFragment.newInstance(null);
        }
    }

    @Override
    protected boolean saveForm() {
        BaseModel model = contentFragment.save();
        boolean isSuccess = model != null;
        if (!isSuccess) {
            Toast.makeText(this, R.string.create_workout_fail, Toast.LENGTH_SHORT).show();
        } else {
            Intent result = new Intent(RESULT_ACTION);
            result.setAction(RESULT_ACTION);
            result.putExtra(EXTRA_RESULT_WORKOUT, model);
            setResult(Activity.RESULT_OK, result);
        }
        return isSuccess;
    }
}
