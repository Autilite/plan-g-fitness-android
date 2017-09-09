package com.autilite.plan_g.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.autilite.plan_g.R;
import com.autilite.plan_g.database.ExerciseContract;
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
    public static final String FIELD_KEY_WEIGHT = "FIELD_KEY_WEIGHT";
    public static final String FIELD_KEY_AUTO_INCR = "FIELD_KEY_AUTO_INCR";
    public static final String FIELD_KEY_REST_TIMER = "FIELD_KEY_REST_TIMER";

    private TextView mEditName;
    private EditText mEditNote;
    private EditText mEditSets;
    private EditText mEditReps;
    private EditText mEditRestTime;
    private EditText mEditWeight;
    private EditText mEditAutoIncrement;

    private BaseExercise baseExercise;

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
    protected Bundle saveData() {
        if (baseExercise == null) {
            return null;
        }
        String note = mEditNote.getText().toString();

        String sSets = mEditSets.getText().toString();
        String sReps = mEditReps.getText().toString();
        String sWeight = mEditWeight.getText().toString();
        String sAutoIncr = mEditAutoIncrement.getText().toString();
        String sRestTime = mEditRestTime.getText().toString();
        // TODO place EditText watcher on form views
        // For now, assume UI correctly sanitizes the input

        // TODO get default values
        int sets = NumberFormat.parseInt(sSets, 5);
        int reps = NumberFormat.parseInt(sReps, 5);
        double weight = NumberFormat.parseDouble(sWeight, 0);
        double autoIncr = NumberFormat.parseDouble(sAutoIncr, 0);
        int restTime = NumberFormat.parseInt(sRestTime, 90);

        Bundle bundle = new Bundle();
        bundle.putParcelable(FIELD_KEY_BASE_EXERCISE, baseExercise);
        bundle.putString(FIELD_KEY_NAME, baseExercise.getName());
        bundle.putString(FIELD_KEY_DESCRIPTION, note);
        bundle.putInt(FIELD_KEY_SETS, sets);
        bundle.putInt(FIELD_KEY_REPS, reps);
        bundle.putDouble(FIELD_KEY_WEIGHT, weight);
        bundle.putDouble(FIELD_KEY_AUTO_INCR, autoIncr);
        bundle.putInt(FIELD_KEY_REST_TIMER, restTime);
        return bundle;
    }
}
