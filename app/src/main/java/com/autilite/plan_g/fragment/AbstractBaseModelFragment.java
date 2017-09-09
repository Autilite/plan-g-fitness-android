package com.autilite.plan_g.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.program.BaseModel;

/**
 * Created by Kelvin on Jul 30, 2017.
 */

public abstract class AbstractBaseModelFragment extends Fragment {
    protected static final String ARG_MODEL_OBJ = "ARG_MODEL_OBJ";

    protected BaseModel model;

    protected WorkoutDatabase db;

    public AbstractBaseModelFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new WorkoutDatabase(getContext());
        if (getArguments() != null) {
            model = getArguments().getParcelable(ARG_MODEL_OBJ);
            // Set keyboard to not auto-open when editing an existing form
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    /**
     * This method is called to send the model data to any observers
     */
    public abstract void passData();
}

