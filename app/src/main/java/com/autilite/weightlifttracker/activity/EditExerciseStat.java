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

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.ExerciseInfoContract;
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.program.Exercise;

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
        // TODO
        return false;
    }

    public static class EditExerciseStatFragment extends Fragment {
       private static final String ARG_EXERCISE_OBJ = "ARG_EXERCISE_OBJ";

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
   }
}
