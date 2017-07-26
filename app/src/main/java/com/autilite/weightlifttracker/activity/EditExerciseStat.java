package com.autilite.weightlifttracker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.program.Exercise;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditExerciseStat extends CreateForm {

    public static final String EXTRA_EXERCISE = "EXTRA_EXERCISE";

    public EditExerciseStat() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            Exercise e = getIntent().getParcelableExtra(EXTRA_EXERCISE);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, EditExerciseStatFragment.newInstance(e))
                    .commit();
        }
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
           if (getArguments() != null) {
               exercise = getArguments().getParcelable(ARG_EXERCISE_OBJ);
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
           }
           return view;
       }
   }
}
