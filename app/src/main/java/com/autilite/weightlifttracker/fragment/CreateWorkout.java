package com.autilite.weightlifttracker.fragment;


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.ExerciseInfoContract;
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

        mAdapter = new AddExerciseAdapter();
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public class AddExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_VIEW = 0;
        private static final int CONTENT_VIEW = 1;

        private static final int HEADER_SIZE = 1;

        private List<Exercise> exercises;

        public AddExerciseAdapter() {
            exercises = new ArrayList<>();
            exercises.add(null);
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
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position < HEADER_SIZE) {
                return HEADER_VIEW;
            }
            return CONTENT_VIEW;
        }

        @Override
        public int getItemCount() {
            return exercises.size() + HEADER_SIZE;
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
                        final long[] exerciseId = new long[1];
                        final String[] name = new String[1];
                        final String[] description = new String[1];
                        if (exercise == null) {
                            final Cursor cursor = db.getAllExerciseInfo();
                            AlertDialog dialog = new AlertDialog.Builder(getContext())
                                    .setCursor(cursor, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            cursor.moveToPosition(i);
                                            exerciseId[0] = cursor.getLong(cursor.getColumnIndex(ExerciseInfoContract.ExerciseInfoEntry._ID));
                                            name[0] = cursor.getString(cursor.getColumnIndex(ExerciseInfoContract.ExerciseInfoEntry.COLUMN_NAME));
                                            description[0] = cursor.getString(cursor.getColumnIndex(ExerciseInfoContract.ExerciseInfoEntry.COLUMN_DESCRIPTION));
                                            cursor.close();
                                            // TODO stub
                                            setName(name[0]);
                                        }
                                    }, ExerciseInfoContract.ExerciseInfoEntry.COLUMN_NAME).create();
                            dialog.show();
                        }

                        // Start activity to change exercise info
                        // Check if there's any null
                    }
                });
            }

            private void addNullIfNone() {
                boolean _hasNull = false;
                for (Exercise e : exercises) {
                    if (e == null) {
                        _hasNull = true;
                        break;
                    }
                }
                if (!_hasNull) {
                    exercises.add(null);
                    notifyDataSetChanged();
                }
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
                if (exercise == null) {
                    setName(getString(R.string.choose_exercise));
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
