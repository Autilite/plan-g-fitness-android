package com.autilite.weightlifttracker.activity;

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
import android.widget.Toast;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.ExerciseInfoContract;
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.util.NumberFormat;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditExerciseStat extends CreateForm {

    public static final String EXTRA_EXERCISE = "EXTRA_EXERCISE";

    public EditExerciseStat() {
    }

    @Override
    protected Fragment createContentFragment() {
        if (getIntent().getExtras() != null) {
            Exercise e = getIntent().getParcelableExtra(EXTRA_EXERCISE);
            return EditExerciseStatFragment.newInstance(e);
        } else {
            return EditExerciseStatFragment.newInstance(null);
        }
    }

    @Override
    protected boolean saveForm() {
        boolean isSuccess =  ((EditExerciseStatFragment ) contentFragment).save();
        if (!isSuccess) {
            Toast.makeText(this, R.string.create_exercise_fail, Toast.LENGTH_SHORT).show();
        }
        return isSuccess;
    }

    public static class EditExerciseStatFragment extends Fragment {
        private static final String ARG_EXERCISE_OBJ = "ARG_EXERCISE_OBJ";

        private static final long EXERCISE_NOT_SELECTED = -1;

        private TextView mEditName;
        private EditText mEditNote;
        private EditText mEditSets;
        private EditText mEditReps;
        private EditText mEditRestTime;
        private EditText mEditWeight;
        private EditText mEditAutoIncrement;

        private Exercise exercise;
        private long exerciseId;
        private String exerciseName;

        private WorkoutDatabase db;


        public EditExerciseStatFragment() {
        }

        public static EditExerciseStatFragment newInstance(Exercise exercise) {

            Bundle args = new Bundle();
            args.putParcelable(ARG_EXERCISE_OBJ, exercise);

            EditExerciseStatFragment fragment = new EditExerciseStatFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            db = new WorkoutDatabase(getContext());
            if (getArguments() != null) {
                exercise = getArguments().getParcelable(ARG_EXERCISE_OBJ);
                exerciseId = exercise != null ? exercise.getId() : EXERCISE_NOT_SELECTED;
            } else {
                exerciseId = EXERCISE_NOT_SELECTED;
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
            final Cursor cursor = db.getAllExerciseInfo();
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setCursor(cursor, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cursor.moveToPosition(i);
                            exerciseId = cursor.getLong(cursor.getColumnIndex(ExerciseInfoContract.ExerciseInfoEntry._ID));
                            exerciseName = cursor.getString(cursor.getColumnIndex(ExerciseInfoContract.ExerciseInfoEntry.COLUMN_NAME));
                            cursor.close();

                            mEditName.setText(exerciseName);
                        }
                    }, ExerciseInfoContract.ExerciseInfoEntry.COLUMN_NAME).create();
            dialog.show();
        }

        private void setViewDefault() {
            if (exercise != null) {
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

        private boolean save() {
            if (exerciseId == EXERCISE_NOT_SELECTED) {
                return false;
            }
            String sets = mEditSets.getText().toString();
            String reps = mEditReps.getText().toString();
            String weight = mEditWeight.getText().toString();
            String autoIncrement = mEditAutoIncrement.getText().toString();
            // TODO place EditText watcher on form views
            // For now, assume UI correctly sanitizes the input

            // TODO get default values
            int wSets = NumberFormat.parseInt(sets, 5);
            int wReps = NumberFormat.parseInt(reps, 5);
            double wWeight = NumberFormat.parseDouble(weight, 0);
            double wAutoInc = NumberFormat.parseDouble(autoIncrement, 0);

            // TODO update if exerciseStat already exists
            long exerciseStatId = db.createExerciseStat(exerciseId, wSets, wReps, wWeight, wAutoInc);
            return exerciseStatId != -1;
        }
    }
}
