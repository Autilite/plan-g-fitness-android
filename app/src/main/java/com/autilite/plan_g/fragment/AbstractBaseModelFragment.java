package com.autilite.plan_g.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.program.BaseModel;

/**
 * Created by Kelvin on Jul 30, 2017.
 */

public abstract class AbstractBaseModelFragment extends Fragment {
    public static final String FIELD_KEY_NAME = "FIELD_KEY_NAME";
    public static final String FIELD_KEY_DESCRIPTION = "FIELD_KEY_DESCRIPTION";

    protected static final String ARG_MODEL_OBJ = "ARG_MODEL_OBJ";

    private OnFragmentInteractionListener mListener;

    protected BaseModel model;
    protected Bundle fields;

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
    public BaseModel getSavedModel(){
        fields = saveData();
        if (mListener != null && fields != null) {
            return mListener.onSave(fields);
        }
        return null;
    }

    /**
     * The function called to save the form data
     *
     * @return  bundle with all the form parameters
     *          null if not all fields are prepared
     */
    protected abstract Bundle saveData();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        BaseModel onSave(Bundle fields);
    }

}

