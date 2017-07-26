package com.autilite.weightlifttracker.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.activity.EditExerciseStat;
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.program.Exercise;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateWorkout extends Fragment {

    private RecyclerView mRecyclerView;
    private AddExerciseAdapter mAdapter;
    private WorkoutDatabase db;

    private List<Exercise> exercises;

    public CreateWorkout() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new WorkoutDatabase(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_workout, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        exercises = new ArrayList<>();
        mAdapter = new AddExerciseAdapter(exercises);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public class AddExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_VIEW = 0;
        private static final int CONTENT_VIEW = 1;
        private static final int FOOTER_VIEW = 2;

        private static final int HEADER_SIZE = 1;
        private static final int FOOTER_SIZE = 1;

        private List<Exercise> exercises;

        public AddExerciseAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == HEADER_VIEW) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false);
                return new TitleViewHolder(view);
            }
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_exercise, parent, false);
            return new ExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == HEADER_VIEW) {
                TextView text = ((TitleViewHolder) holder).textView;
                text.setText(getString(R.string.exercises));
            } else if (holder.getItemViewType() == CONTENT_VIEW) {
                Exercise e = exercises.get(position - HEADER_SIZE);

                ExerciseViewHolder vh = (ExerciseViewHolder) holder;
                vh.setExercise(e);
                vh.updateView();
            } else if (holder.getItemViewType() == FOOTER_VIEW) {
                ExerciseViewHolder vh = (ExerciseViewHolder) holder;
                vh.updateView();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position < HEADER_SIZE) {
                return HEADER_VIEW;
            } else if (position >= HEADER_SIZE + exercises.size()) {
                return FOOTER_VIEW;
            } else
                return CONTENT_VIEW;
        }

        @Override
        public int getItemCount() {
            return exercises.size() + HEADER_SIZE + FOOTER_SIZE;
        }

        public class TitleViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public TitleViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.header);
            }
        }

        public class ExerciseViewHolder extends RecyclerView.ViewHolder {

            private TextView exerciseName;
            private TextView exerciseDetail;
            private Exercise exercise;

            public ExerciseViewHolder(View itemView) {
                super(itemView);
                exerciseName = (TextView) itemView.findViewById(R.id.exercise_name);
                exerciseDetail = (TextView) itemView.findViewById(R.id.exercise_details);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getItemViewType() == FOOTER_VIEW) {
                            // Start activity to create new exercise
                            Intent intent = new Intent(getActivity(), EditExerciseStat.class);
                            startActivity(intent);
                        } else {
                            // Start activity to edit the existing exercise
                            Intent intent = new Intent(getActivity(), EditExerciseStat.class);
                            intent.putExtra(EditExerciseStat.EXTRA_EXERCISE, exercise);
                            startActivity(intent);
                        }
                    }
                });
            }

            public void setName(String name) {
                exerciseName.setText(name);
            }

            private void setDetails(String details) {
                exerciseDetail.setText(details);
            }

            private void setStatGone() {
                exerciseDetail.setVisibility(View.GONE);
            }

            private void setStatVisible() {
                exerciseDetail.setVisibility(View.VISIBLE);
            }

            public void setExercise(Exercise exercise) {
                this.exercise = exercise;
            }

            public void updateView() {
                if (getItemViewType() == FOOTER_VIEW) {
                    setName(getString(R.string.add_exercise));
                    setStatGone();
                } else {
                    setStatVisible();
                    setName(exercise.getName());
                    setDetails(exercise.getSets() + "x" + exercise.getReps());
                }
            }
        }

    }
}
