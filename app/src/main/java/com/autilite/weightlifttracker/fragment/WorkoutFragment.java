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
public class WorkoutFragment extends Fragment implements WorkoutAdapter.IWorkoutViewHolderClick {
    private static final int CREATE_WORKOUT = 1;
    private static final int EDIT_WORKOUT = 2;

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
                Intent createWorkout = new Intent(getActivity(), EditWorkout.class);
                startActivityForResult(createWorkout, CREATE_WORKOUT);
            }
        });
        workoutDb = new WorkoutDatabase(getActivity());
        workouts = workoutDb.getAllWorkoutsList();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new WorkoutAdapter(getActivity(), workouts, this);
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
        } else if (requestCode == EDIT_WORKOUT) {
            if (resultCode == Activity.RESULT_OK) {
                Workout resultWorkout = data.getParcelableExtra(EditWorkout.EXTRA_RESULT_WORKOUT);

                updateWorkout(resultWorkout);
            }
        }
    }

    private boolean updateWorkout(Workout workout) {
        for (int i = 0; i < workouts.size(); i++) {
            Workout checkWorkout = workouts.get(i);
            if (workout.getId() == checkWorkout.getId()) {
                workouts.set(i, workout);
                mAdapter.notifyItemChanged(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onWorkoutSelect(Workout workout) {
        Intent intent = new Intent(getContext(), EditWorkout.class);
        intent.putExtra(EditWorkout.EXTRA_WORKOUT, workout);
        startActivityForResult(intent, EDIT_WORKOUT);
    }
}
