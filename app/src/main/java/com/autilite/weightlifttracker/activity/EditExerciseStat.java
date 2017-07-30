package com.autilite.weightlifttracker.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
    public static final String RESULT_ACTION = "com.autilite.weightlifttracker.activity.EditExerciseStat.RESULT_ACTION";
    public static final String EXTRA_RESULT_EXERCISE = "RESULT_EXERCISE";

    public EditExerciseStat() {
    }

    @Override
    protected Fragment createContentFragment() {
        if (getIntent().getExtras() != null) {
            setTitle(R.string.edit_exercise);
            Exercise e = getIntent().getParcelableExtra(EXTRA_EXERCISE);
            return EditExerciseStatFragment.newInstance(e);
        } else {
            setTitle(R.string.create_exercise);
            return EditExerciseStatFragment.newInstance(null);
        }
    }

    @Override
    protected boolean saveForm() {
        Exercise exercise =  ((EditExerciseStatFragment ) contentFragment).save();
        boolean isSuccess = exercise != null;
        if (!isSuccess) {
            Toast.makeText(this, R.string.create_exercise_fail, Toast.LENGTH_SHORT).show();
        } else {
            Intent result = new Intent(RESULT_ACTION);
            result.setAction(RESULT_ACTION);
            result.putExtra(EXTRA_RESULT_EXERCISE, exercise);
            setResult(Activity.RESULT_OK, result);
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

        private boolean isNewEntry;


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
                if (exercise != null) {
                    exerciseId = exercise.getId();
                    exerciseName = exercise.getName();
                    isNewEntry = false;
                } else {
                    exerciseId = EXERCISE_NOT_SELECTED;
                    isNewEntry = true;
                }
            } else {
                exerciseId = EXERCISE_NOT_SELECTED;
                isNewEntry = true;
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

        /**
         * Returns a newly created <code>Exercise</code> object containing the data that was saved.
         * If the save is unsuccessful, the function will return null.
         *
         * @return  The <code>Exercise</code> object if save successful
         *          <code>null</code> if save failed
         */
        private Exercise save() {
            if (exerciseId == EXERCISE_NOT_SELECTED) {
                return null;
            }
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

            if (isNewEntry) {
                long exerciseStatId = db.createExerciseStat(exerciseId, wSets, wReps, wWeight, wAutoInc, wRestTime);
                if (exerciseStatId != -1) {
                    return new Exercise(exerciseStatId, exerciseName, "", exerciseId, wSets, wReps, wWeight, wAutoInc, wRestTime);
                }
            } else {
                long exerciseStatId = exercise.getId();
                int numRowsUpdate = db.updateExerciseStat(
                        exerciseStatId, exerciseId, wSets, wReps, wWeight, wAutoInc, wRestTime);
                if (numRowsUpdate == 1) {
                    // TODO change Exercise model to include both ExerciseStatId and ExerciseId
                    return new Exercise(exerciseStatId, exerciseName, "", exerciseId, wSets, wReps, wWeight, wAutoInc, wRestTime);
                }
            }
            return null;
        }
    }
}
