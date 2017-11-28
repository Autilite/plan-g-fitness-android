package com.autilite.plan_g.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.autilite.plan_g.R;
import com.autilite.plan_g.program.BaseExercise;
import com.autilite.plan_g.program.Exercise;
import com.autilite.plan_g.util.NumberFormat;

/**
 * Created by Kelvin on Sep 8, 2017.
 */

public class EditExerciseStatFragment extends AbstractBaseModelFragment {
    public static final String FIELD_KEY_BASE_EXERCISE = "FIELD_KEY_BASE_EXERCISE";
    public static final String FIELD_KEY_SETS = "FIELD_KEY_SETS";
    public static final String FIELD_KEY_REPS = "FIELD_KEY_REPS";
    public static final String FIELD_KEY_REPS_MIN = "FIELD_KEY_REPS_MIN";
    public static final String FIELD_KEY_REPS_MAX = "FIELD_KEY_REPS_MAX";
    public static final String FIELD_KEY_REPS_INCR = "FIELD_KEY_REPS_INCR";
    public static final String FIELD_KEY_WEIGHT = "FIELD_KEY_WEIGHT";
    public static final String FIELD_KEY_WEIGHT_INCR = "FIELD_KEY_WEIGHT_INCR";
    public static final String FIELD_KEY_REST_TIMER = "FIELD_KEY_REST_TIMER";

    private OnEditExerciseFragmentInteractionListener mListener;

    private TextView mEditName;
    private EditText mEditNote;
    private EditText mEditSets;
    private EditText mEditReps;
    private EditText mEditRepsMin;
    private EditText mEditRepsMax;
    private EditText mEditRepsIncr;
    private EditText mEditRestTime;
    private EditText mEditWeight;
    private EditText mEditWeightIncr;

    private BaseExercise baseExercise;

    public EditExerciseStatFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditExerciseFragmentInteractionListener) {
            mListener = (OnEditExerciseFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditExerciseFragmentInteractionListener");
        }
    }

    public static EditExerciseStatFragment newInstance(Exercise exercise) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_MODEL_OBJ, exercise);

        EditExerciseStatFragment fragment = new EditExerciseStatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (model != null) {
            long baseExerciseId = ((Exercise) model).getBaseExerciseId();
            baseExercise = db.getBaseExercise(baseExerciseId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_exercise, container, false);

        mEditName = (TextView) view.findViewById(R.id.exercise_name);
        mEditNote = (EditText) view.findViewById(R.id.add_note);
        mEditSets = (EditText) view.findViewById(R.id.input_sets);
        mEditReps = (EditText) view.findViewById(R.id.input_reps);
        mEditRepsMin = (EditText) view.findViewById(R.id.input_reps_min);
        mEditRepsMax = (EditText) view.findViewById(R.id.input_reps_max);
        mEditRepsIncr = (EditText) view.findViewById(R.id.input_reps_increment);
        mEditRestTime = (EditText) view.findViewById(R.id.input_rest_time);
        mEditWeight = (EditText) view.findViewById(R.id.input_weight);
        mEditWeightIncr = (EditText) view.findViewById(R.id.input_weight_increment);

        setViewDefault();

        mEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onChooseExercise();
            }
        });

        return view;
    }

    public void updateExerciseView(BaseExercise baseExercise) {
        this.baseExercise = baseExercise;
        mEditName.setText(baseExercise.getName());
    }

    private void setViewDefault() {
        if (model != null) {
            Exercise exercise = (Exercise) model;
            int sets = exercise.getSets();
            int reps = exercise.getReps();
            int repsMin = exercise.getRepsMin();
            int repsMax = exercise.getRepsMax();
            int repsIncr = exercise.getRepsIncrement();
            int restTime = exercise.getRestTime();
            double weight = exercise.getWeight();
            double weightIncrement = exercise.getWeightIncrement();

            mEditName.setText(exercise.getName());
            mEditSets.setText(sets >= 0 ? String.valueOf(sets) : "");
            mEditReps.setText(reps >= 0 ? String.valueOf(reps) : "");
            mEditRepsMin.setText(repsMin >= 0 ? String.valueOf(repsMin) : "");
            mEditRepsMax.setText(repsMax >= 0 ? String.valueOf(repsMax) : "");
            mEditRepsIncr.setText(repsIncr >= 0 ? String.valueOf(repsIncr) : "");
            mEditRestTime.setText(restTime >= 0 ? String.valueOf(restTime) : "");
            mEditWeight.setText(weight >= 0 ? String.valueOf(weight) : "");
            mEditWeightIncr.setText(weightIncrement >= 0 ? String.valueOf(weightIncrement) : "");
        } else {
            mEditName.setText(R.string.choose_exercise);
        }
    }

    @Override
    protected Bundle getBundledFormData() {
        if (baseExercise == null) {
            return null;
        }
        String note = mEditNote.getText().toString();

        String sSets = mEditSets.getText().toString();
        String sReps = mEditReps.getText().toString();
        String sRepsMin = mEditRepsMin.getText().toString();
        String sRepsMax = mEditRepsMax.getText().toString();
        String sRepsIncr = mEditRepsIncr.getText().toString();
        String sWeight = mEditWeight.getText().toString();
        String sWeightIncr = mEditWeightIncr.getText().toString();
        String sRestTime = mEditRestTime.getText().toString();
        // TODO place EditText watcher on form views
        // For now, assume UI correctly sanitizes the input

        // TODO get default values from config settings
        int sets = NumberFormat.parseInt(sSets, 5);
        int repsMin = NumberFormat.parseInt(sRepsMin, 0);
        int repsMax = NumberFormat.parseInt(sRepsMax, 0);
        int repsIncr = NumberFormat.parseInt(sRepsIncr, 0);
        // Default reps to be the lowest value in the range
        int reps = NumberFormat.parseInt(sReps, repsMin);
        double weight = NumberFormat.parseDouble(sWeight, 0);
        double weightIncr = NumberFormat.parseDouble(sWeightIncr, 0);
        int restTime = NumberFormat.parseInt(sRestTime, 90);

        Bundle bundle = new Bundle();
        bundle.putParcelable(FIELD_KEY_BASE_EXERCISE, baseExercise);
        bundle.putString(FIELD_KEY_NAME, baseExercise.getName());
        bundle.putString(FIELD_KEY_DESCRIPTION, note);
        bundle.putInt(FIELD_KEY_SETS, sets);
        bundle.putInt(FIELD_KEY_REPS, reps);
        bundle.putInt(FIELD_KEY_REPS_MIN, repsMin);
        bundle.putInt(FIELD_KEY_REPS_MAX, repsMax);
        bundle.putInt(FIELD_KEY_REPS_INCR, repsIncr);
        bundle.putDouble(FIELD_KEY_WEIGHT, weight);
        bundle.putDouble(FIELD_KEY_WEIGHT_INCR, weightIncr);
        bundle.putInt(FIELD_KEY_REST_TIMER, restTime);
        return bundle;
    }

    public interface OnEditExerciseFragmentInteractionListener {
        void onChooseExercise();
    }
}
