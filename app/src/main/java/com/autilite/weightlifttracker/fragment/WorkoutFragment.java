package com.autilite.weightlifttracker.fragment;

import android.database.Cursor;
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
import com.autilite.weightlifttracker.adapter.WorkoutAdapter;
import com.autilite.weightlifttracker.database.WorkoutContract;
import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;
import com.autilite.weightlifttracker.fragment.dialog.AbstractCreateDialog;
import com.autilite.weightlifttracker.fragment.dialog.CreateWorkoutDialog;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutFragment extends Fragment implements AbstractCreateDialog.CreateDialogListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private WorkoutProgramDbHelper workoutDb;
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
                CreateWorkoutDialog frag = new CreateWorkoutDialog();
                frag.setTargetFragment(WorkoutFragment.this, 0);
                frag.show(getActivity().getSupportFragmentManager(), "CreateWorkoutDialog");
            }
        });
        workoutDb = new WorkoutProgramDbHelper(getActivity());
        workouts = getAllWorkouts();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new WorkoutAdapter(getActivity(), workouts);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TableLayout table = (TableLayout) dialog.getDialog().findViewById(R.id.entry_create_table);
        EditText nameEditText = (EditText) dialog.getDialog().findViewById(R.id.heading_create_name_editview);
        String workoutName = nameEditText.getText().toString();

        // Create workout
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
        Workout workout = new Workout(workoutId, workoutName);

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
                if (!workoutDb.addExerciseToWorkout(workoutId, exerciseId)){
                    Toast.makeText(getActivity(), "Could not add " + exBtn.getText().toString() +
                            " to " + workoutName, Toast.LENGTH_LONG).show();
                }
                Exercise e = new Exercise(exBtn.getText().toString(), wSets, wReps, wWeight);
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

    public List<Workout> getAllWorkouts() {
        // Get cursor with all workout Ids
        Cursor workoutCursor = workoutDb.getAllWorkouts();
        List<Workout> workouts = new ArrayList<>();

        // Go through each of the workoutId
        while (workoutCursor.moveToNext()) {
            long workoutId = workoutCursor.getLong(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry._ID));
            String workoutName = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NAME));
            Workout w = new Workout(workoutId, workoutName);

            // Get list of exercise for workoutId
            Cursor eStat = workoutDb.getAllExerciseStatForWorkout(workoutId);
            while (eStat.moveToNext()) {
                long exerciseId = eStat.getLong(0);
                String exerciseName = eStat.getString(1);
                int set = eStat.getInt(2);
                int rep = eStat.getInt(3);
                float weight = eStat.getFloat(4);
                Exercise e = new Exercise(exerciseName, set, rep, weight);
                w.addExercise(e);
            }
            workouts.add(w);
            eStat.close();

        }
        workoutCursor.close();
        return workouts;
    }

}
