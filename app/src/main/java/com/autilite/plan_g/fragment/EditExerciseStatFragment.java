package com.autilite.plan_g.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.autilite.plan_g.R;
import com.autilite.plan_g.database.ExerciseContract;
import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.program.BaseExercise;
import com.autilite.plan_g.program.Exercise;
import com.autilite.plan_g.util.NumberFormat;

/**
 * Created by Kelvin on Sep 8, 2017.
 */

public class EditExerciseStatFragment extends Fragment {
    private static final String ARG_MODEL_OBJ = "ARG_MODEL_OBJ";

    private OnFragmentInteractionListener mListener;

    private TextView mEditName;
    private EditText mEditNote;
    private EditText mEditSets;
    private EditText mEditReps;
    private EditText mEditRestTime;
    private EditText mEditWeight;
    private EditText mEditAutoIncrement;

    private BaseExercise baseExercise;

    private Exercise model;
    private WorkoutDatabase db;


    public EditExerciseStatFragment() {
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
        db = new WorkoutDatabase(getContext());
        if (getArguments() != null) {
            model = getArguments().getParcelable(ARG_MODEL_OBJ);
            if (model != null) {
                long baseExerciseId = model.getBaseExerciseId();
                baseExercise = db.getBaseExercise(baseExerciseId);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_exercise, container, false);

        mEditName = (TextView) view.findViewById(R.id.exercise_name);
        mEditNote = (EditText) view.findViewById(R.id.add_note);
        mEditSets = (EditText) view.findViewById(R.id.input_sets);
        mEditReps = (EditText) view.findViewById(R.id.input_reps);
        mEditRestTime = (EditText) view.findViewById(R.id.input_rest_time);
        mEditWeight = (EditText) view.findViewById(R.id.input_weight);
        mEditAutoIncrement = (EditText) view.findViewById(R.id.input_auto_increment);

        setViewDefault();

        mEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectExercise();
            }
        });

        return view;
    }

    private void selectExercise() {
        final Cursor cursor = db.getBaseExerciseTable();
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setCursor(cursor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cursor.moveToPosition(i);
                        long baseExerciseId = cursor.getLong(cursor.getColumnIndex(ExerciseContract.BaseExerciseEntry._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ExerciseContract.BaseExerciseEntry.COLUMN_NAME));
                        String description = cursor.getString(cursor.getColumnIndex(ExerciseContract.BaseExerciseEntry.COLUMN_DESCRIPTION));
                        cursor.close();

                        baseExercise = new BaseExercise(baseExerciseId, name, description);

                        mEditName.setText(name);
                    }
                }, ExerciseContract.BaseExerciseEntry.COLUMN_NAME).create();
        dialog.show();
    }

    private void setViewDefault() {
        if (model != null) {
            Exercise exercise = (Exercise) model;
            int sets = exercise.getSets();
            int reps = exercise.getReps();
            int restTime = exercise.getRestTime();
            double weight = exercise.getWeight();
            double weightIncrement = exercise.getWeightIncrement();

            mEditName.setText(exercise.getName());
            mEditSets.setText(sets >= 0 ? String.valueOf(sets) : "");
            mEditReps.setText(reps >= 0 ? String.valueOf(reps) : "");
            mEditRestTime.setText(restTime >= 0 ? String.valueOf(restTime) : "");
            mEditWeight.setText(weight >= 0 ? String.valueOf(weight) : "");
            mEditAutoIncrement.setText(weightIncrement >= 0 ? String.valueOf(weightIncrement) : "");
        } else {
            mEditName.setText(R.string.choose_exercise);
        }
    }

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

    public void passData() {
        if (mListener != null) {
            String sets = mEditSets.getText().toString();
            String reps = mEditReps.getText().toString();
            String weight = mEditWeight.getText().toString();
            String autoIncrement = mEditAutoIncrement.getText().toString();
            String restTime = mEditRestTime.getText().toString();
            // TODO place EditText watcher on form views
            // For now, assume UI correctly sanitizes the input

            // TODO get default values
            int wSets = NumberFormat.parseInt(sets, 5);
            int wReps = NumberFormat.parseInt(reps, 5);
            double wWeight = NumberFormat.parseDouble(weight, 0);
            double wAutoInc = NumberFormat.parseDouble(autoIncrement, 0);
            int wRestTime = NumberFormat.parseInt(restTime, 90);

            mListener.onExerciseSave(baseExercise, "", wSets, wReps, wWeight, wAutoInc, wRestTime);
        }
    }

    public interface OnFragmentInteractionListener {
        boolean onExerciseSave(BaseExercise baseExercise, String note, int sets, int reps, double weight, double autoIncr, int restTime);
    }
}
