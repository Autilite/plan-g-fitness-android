package com.autilite.weightlifttracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;
import com.autilite.weightlifttracker.program.Workout;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutFragment extends Fragment implements CreateWorkoutDialog.CreateWorkoutListener {
    private List<Workout> workouts;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public WorkoutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateWorkoutDialog frag = new CreateWorkoutDialog();
                frag.setTargetFragment(WorkoutFragment.this, 0);
                frag.show(getActivity().getSupportFragmentManager(), "CreateWorkoutDialog");
            }
        });
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.workout_recycler_view);
//
//        mLayoutManager = new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(mLayoutManager);

        // TODO
//        mAdapter = new WorkoutAdapter(getActivity(), workouts);
//        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TableLayout table = (TableLayout) dialog.getDialog().findViewById(R.id.workout_create_table);
        EditText nameEditText = (EditText) dialog.getDialog().findViewById(R.id.workout_create_name);
        String workoutName = nameEditText.getText().toString();

        // Create workout
        WorkoutProgramDbHelper workoutDb = new WorkoutProgramDbHelper(getActivity());
        if (workoutName.equals("")) {
            // TODO prevent UI from closing if empty workout name
            Toast.makeText(getActivity(), "Workout could not be created", Toast.LENGTH_LONG).show();
            return;
        }
        long workoutId = workoutDb.createWorkout(workoutName);
        if (workoutId == -1) {
            Toast.makeText(getActivity(), "Workout could not be created", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(getActivity(), "New workout \"" + workoutName + "\" created", Toast.LENGTH_LONG).show();
        }

        // Ignore first row since that is the headings
        for (int i = 1; i < table.getChildCount(); i++) {
            View c = table.getChildAt(i);
            if (c instanceof TableRow) {
                // Get the exercise stats
                TableRow row = (TableRow) c;
                Button exBtn = ((Button) row.findViewById(R.id.workout_create_exercise_chooser));
                Object exercise = exBtn.getTag();
                Editable sets = ((EditText) row.findViewById(R.id.workout_create_sets)).getText();
                Editable reps = ((EditText) row.findViewById(R.id.workout_create_reps)).getText();
                Editable weight = ((EditText) row.findViewById(R.id.workout_create_weight)).getText();

                if (exercise == null)
                    continue;
                long exerciseId = Long.valueOf(String.valueOf(exercise));

                if (sets == null || reps == null || weight == null) {
                    // TODO set default values
                    continue;
                }
                int wSets = Integer.parseInt(sets.toString());
                int wReps = Integer.parseInt(reps.toString());
                float wWeight = Float.parseFloat(weight.toString());

                // Create ExerciseStat
                long exerciseStatId = workoutDb.createExerciseStat(exerciseId, wSets, wReps, wWeight);
                if (exerciseStatId == -1) {
                    Toast.makeText(getActivity(), "Could not create exercise " + exBtn.getText().toString(), Toast.LENGTH_LONG).show();
                    continue;
                }
                if (!workoutDb.addExerciseToWorkout(workoutId, exerciseId)){
                    Toast.makeText(getActivity(), "Could not add " + exBtn.getText().toString() +
                            " to " + workoutName, Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

}
