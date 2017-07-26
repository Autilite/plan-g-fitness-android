package com.autilite.weightlifttracker.activity;

import android.support.v4.app.Fragment;

import com.autilite.weightlifttracker.fragment.CreateWorkout;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditWorkout extends CreateForm {
    @Override
    protected Fragment createContentFragment() {
        return new CreateWorkout();
    }

    @Override
    protected boolean saveForm() {
        // TODO
        return false;
    }
}
