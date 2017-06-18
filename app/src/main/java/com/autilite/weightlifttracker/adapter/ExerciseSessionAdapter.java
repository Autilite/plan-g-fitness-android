package com.autilite.weightlifttracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.program.Exercise;

import java.util.List;

/**
 * Created by Kelvin on Jun 17, 2017.
 */

public class ExerciseSessionAdapter extends RecyclerView.Adapter<ExerciseSessionAdapter.ExerciseSessionViewHolder> {

    private Context mContext;
    private final List<Exercise> exercises;
    private OnItemClickListener mListener;

    public class ExerciseSessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private TextView sets;
        private TextView btnOptions;

        public ExerciseSessionViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.exercise_name);
            sets = (TextView) itemView.findViewById(R.id.complete_sets);
            btnOptions = (TextView) itemView.findViewById(R.id.exercise_options);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener !=  null) {
                mListener.onItemClick(view, exercises.get(getAdapterPosition()));
            }
        }
    }

    public ExerciseSessionAdapter(Context mContext, List<Exercise> exercises) {
        this.mContext = mContext;
        this.exercises = exercises;
    }

    @Override
    public ExerciseSessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.exercise_session_card, parent, false);
        return new ExerciseSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExerciseSessionViewHolder holder, int position) {
        Exercise e = exercises.get(position);
        holder.name.setText(e.getName());
        String completeSet = "Complete set: /" + e.getSets();
        holder.sets.setText(completeSet);

        holder.btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, Exercise exercise);
    }

}
