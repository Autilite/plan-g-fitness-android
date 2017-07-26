package com.autilite.weightlifttracker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

        EditText mEditName = (EditText) findViewById(R.id.input_name);
        TextInputLayout mTextInputNote = (TextInputLayout) findViewById(R.id.text_input_description);
        mTextInputNote.setHint(getResources().getString(R.string.note));
        EditText mEditNote = (EditText) findViewById(R.id.input_description);

        if (getIntent().getExtras() != null) {
            Exercise e = getIntent().getParcelableExtra(EXTRA_EXERCISE);
            mEditName.setText(e.getName());

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, EditExerciseStatFragment.newInstance(e))
                    .commit();
        }
    }

   public static class EditExerciseStatFragment extends Fragment {
       private static final String ARG_EXERCISE_OBJ = "ARG_EXERCISE_OBJ";

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
           mEditSets = (EditText) view.findViewById(R.id.input_sets);
           mEditReps = (EditText) view.findViewById(R.id.input_reps);
           mEditRestTime = (EditText) view.findViewById(R.id.input_rest_time);
           mEditWeight = (EditText) view.findViewById(R.id.input_weight);
           mEditAutoIncrement = (EditText) view.findViewById(R.id.input_auto_increment);

           if (exercise != null) {
               if (exercise.getSets() > 0)
                   mEditSets.setText(String.valueOf(exercise.getSets()));
               if (exercise.getReps() > 0)
                   mEditReps.setText(String.valueOf(exercise.getReps()));
               if (exercise.getRestTime() > 0)
                   mEditRestTime.setText(String.valueOf(exercise.getRestTime()));
               if (exercise.getWeight() > 0)
                   mEditWeight.setText(String.valueOf(exercise.getWeight()));
               if (exercise.getWeightIncrement() > 0)
                   mEditAutoIncrement.setText(String.valueOf(exercise.getWeightIncrement()));
           }
           return view;
       }
   }
}
