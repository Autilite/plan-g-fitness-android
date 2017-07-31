package com.autilite.weightlifttracker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.program.BaseModel;

/**
 * Created by Kelvin on Jul 30, 2017.
 */

public abstract class AbstractFormFragment extends Fragment {
    protected static final String ARG_MODEL_OBJ = "ARG_MODEL_OBJ";

    private static final long MODEL_NOT_SELECTED = -1;

    protected BaseModel model;

    /**
     * The id of the Model instance. If the function of this fragment is to create a new Model
     * object, then the value of id will be <code>MODEL_NOT_SELECTED</code>.
     */
    protected long id;

    /**
     * The name of the Model instance
     */
    protected String name;

    protected WorkoutDatabase db;

    private Type formType;

    public enum Type {
        CREATE,
        EDIT
    }

    public AbstractFormFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new WorkoutDatabase(getContext());
        if (getArguments() != null) {
            model = getArguments().getParcelable(ARG_MODEL_OBJ);
            if (model != null) {
                id = model.getId();
                name = model.getName();
                formType = Type.EDIT;
            } else {
                id = MODEL_NOT_SELECTED;
                formType = Type.CREATE;
            }
        } else {
            id = MODEL_NOT_SELECTED;
            formType = Type.CREATE;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public BaseModel save() {
        switch (formType) {
            case CREATE:
                return insertNewEntry();
            case EDIT:
                if (id == MODEL_NOT_SELECTED) {
                    throw new RuntimeException("This edit form has no selected Model");
                }
                return editEntry();
            default:
                throw new RuntimeException("This form has no type");
        }
    }

    protected abstract BaseModel insertNewEntry();

    protected abstract BaseModel editEntry();

    public Type getFormType() {
        return formType;
    }

}
