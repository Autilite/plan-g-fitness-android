package com.autilite.weightlifttracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Workout;
import com.autilite.weightlifttracker.widget.ExtendableListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelvin on Jun 15, 2017.
 */

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private final IWorkoutViewHolderClick listener;
    private Context mContext;
    private List<Workout> workouts;

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private TextView workoutName;
        private ExtendableListView exercises;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            workoutName = (TextView) itemView.findViewById(R.id.workout_name);
            exercises = (ExtendableListView) itemView.findViewById(R.id.workout_exercises);
        }
    }

    public WorkoutAdapter(Context context, List<Workout> workouts, IWorkoutViewHolderClick listener) {
        mContext = context;
        this.workouts = workouts;
        this.listener = listener;
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.workout_card, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkoutViewHolder holder, int position) {
        final Workout workout = workouts.get(position);

        // Add workout name
        holder.workoutName.setText(workout.getName());

        // just show the exercise name for now
        List<String> exercises = new ArrayList<>();
        for (Exercise e : workout.getExercises()) {
            exercises.add(e.getName());
        }
        ArrayAdapter<String> eAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, exercises);
        holder.exercises.setAdapter(eAdapter);

        holder.itemView.setOnClickListener(listener == null ? null : new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkoutSelect(workout);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public interface IWorkoutViewHolderClick {
        void onWorkoutSelect(Workout workout);
    }
}
