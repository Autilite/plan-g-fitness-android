package com.autilite.weightlifttracker.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.activity.WorkoutSessionActivity;
import com.autilite.weightlifttracker.adapter.WorkoutAdapter;
import com.autilite.weightlifttracker.database.WorkoutContract;
import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartProgramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartProgramFragment extends Fragment {
    private static final String ARG_PROGRAM_ID = "ARG_PROGRAM_ID";
    private static final String ARG_PROGRAM_NAME = "ARG_PROGRAM_NAME";

    private long programId;
    private String programName;
    private WorkoutProgramDbHelper workoutDb;
    private List<Workout> workouts;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private WorkoutAdapter mAdapter;


    public StartProgramFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param programId The id of the program
     * @param programName The program name
     * @return A new instance of fragment StartProgramFragment.
     */
    public static StartProgramFragment newInstance(long programId, String programName) {
        StartProgramFragment fragment = new StartProgramFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PROGRAM_ID, programId);
        args.putString(ARG_PROGRAM_NAME, programName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            programId = getArguments().getLong(ARG_PROGRAM_ID);
            programName = getArguments().getString(ARG_PROGRAM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.fragment_recycle_view, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WorkoutSessionActivity.class);
                intent.putExtra(WorkoutSessionActivity.EXTRA_PROGRAM_ID, programId);
                startActivity(intent);
            }
        });

        workoutDb = new WorkoutProgramDbHelper(getActivity());
        workouts = getProgramWorkouts();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new WorkoutAdapter(getActivity(), workouts);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    public List<Workout> getProgramWorkouts() {
        // Get cursor with all workout Ids
        Cursor workoutCursor = workoutDb.getProgramWorkoutTableJoinedWithName(programId);
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
