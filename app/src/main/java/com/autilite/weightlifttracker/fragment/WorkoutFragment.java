package com.autilite.weightlifttracker.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.activity.CreateForm;
import com.autilite.weightlifttracker.activity.EditWorkout;
import com.autilite.weightlifttracker.adapter.WorkoutAdapter;
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.fragment.dialog.AbstractCreateDialog;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Workout;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutFragment extends Fragment implements AbstractCreateDialog.CreateDialogListener {
    private static final int CREATE_WORKOUT = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private WorkoutDatabase workoutDb;
    private List<Workout> workouts;

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
        View view = inflater.inflate(R.layout.fragment_recycle_view, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CreateWorkoutDialog frag = new CreateWorkoutDialog();
//                frag.setTargetFragment(WorkoutFragment.this, 0);
//                frag.show(getActivity().getSupportFragmentManager(), "CreateWorkoutDialog");
                Intent createWorkout = new Intent(getActivity(), EditWorkout.class);
                startActivityForResult(createWorkout, CREATE_WORKOUT);
            }
        });
        workoutDb = new WorkoutDatabase(getActivity());
        workouts = workoutDb.getAllWorkoutsList();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new WorkoutAdapter(getActivity(), workouts);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_WORKOUT) {
            if (resultCode == Activity.RESULT_OK) {
                Workout workout = data.getParcelableExtra(EditWorkout.EXTRA_RESULT_WORKOUT);
                workouts.add(workout);
                mAdapter.notifyItemInserted(workouts.size() - 1);
            }
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TableLayout table = (TableLayout) dialog.getDialog().findViewById(R.id.entry_create_table);
        EditText nameEditText = (EditText) dialog.getDialog().findViewById(R.id.heading_create_name_editview);
        String workoutName = nameEditText.getText().toString();
        String description = "";

        // Create workout
        if (workoutName.equals("")) {
            // TODO prevent UI from closing if empty workout name
            Toast.makeText(getActivity(), "Workout could not be created", Toast.LENGTH_LONG).show();
            return;
        }
        long workoutId = workoutDb.createWorkout(workoutName, description);
        if (workoutId == -1) {
            Toast.makeText(getActivity(), "Workout could not be created", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(getActivity(), "New workout \"" + workoutName + "\" created", Toast.LENGTH_LONG).show();
        }
        Workout workout = new Workout(workoutId, workoutName, description);

        // Ignore first row since that is the headings
        for (int i = 1; i < table.getChildCount(); i++) {
            View c = table.getChildAt(i);
            if (c instanceof TableRow) {
                // Get the exercise stats
                TableRow row = (TableRow) c;
                Button exBtn = ((Button) row.findViewById(R.id.workout_create_exercise_chooser));
                Object exercise = exBtn.getTag();
                String sets = ((EditText) row.findViewById(R.id.workout_create_sets)).getText().toString();
                String reps = ((EditText) row.findViewById(R.id.workout_create_reps)).getText().toString();
                String weight = ((EditText) row.findViewById(R.id.workout_create_weight)).getText().toString();

                if (exercise == null)
                    continue;
                long exerciseId = Long.valueOf(String.valueOf(exercise));

                if (sets.equals("") || reps.equals("") || weight.equals("")) {
                    // TODO set default values
                    continue;
                }
                int wSets = Integer.parseInt(sets);
                int wReps = Integer.parseInt(reps);
                float wWeight = Float.parseFloat(weight);

                // Create ExerciseStat
                long exerciseStatId = workoutDb.createExerciseStat(exerciseId, wSets, wReps, wWeight);
                if (exerciseStatId == -1) {
                    Toast.makeText(getActivity(), "Could not create exercise " + exBtn.getText().toString(), Toast.LENGTH_LONG).show();
                    continue;
                }
                long id = workoutDb.addExerciseToWorkout(workoutId, exerciseStatId);
                System.out.println("the id is " + id);
                if (id == -1){
                    Toast.makeText(getActivity(), "Could not add " + exBtn.getText().toString() +
                            " to " + workoutName, Toast.LENGTH_LONG).show();
                    continue;
                }
                Exercise e = new Exercise(exerciseStatId, exBtn.getText().toString(), "", exerciseId, wSets, wReps, wWeight, 0, 90);
                workout.addExercise(e);
            }
        }
        workouts.add(workout);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

}
