package com.autilite.plan_g.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.autilite.plan_g.R;
import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.program.BaseExercise;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseExerciseFragment extends Fragment {

    private static final int CREATE_BASE_EXERCISE = 1;

    private WorkoutDatabase db;
    private List<BaseExercise> exercises;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private BaseExerciseAdapter mAdapter;

    public BaseExerciseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycle_view, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View inputView = inflater.inflate(R.layout.input_dialog_2, null);

                final EditText nameEditText = (EditText) inputView.findViewById(R.id.edit1);
                nameEditText.setHint(R.string.name);

                final EditText descEditText = (EditText) inputView.findViewById(R.id.edit2);
                descEditText.setHint(R.string.description);

                AlertDialog.Builder createDialog = new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.create_exercise))
                        .setView(inputView)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String name = nameEditText.getText().toString();
                                String description = descEditText.getText().toString();
                                createBaseExercise(name, description);
                            }
                        });
                createDialog.create().show();
            }
        });
        db = new WorkoutDatabase(getActivity());

        exercises = db.getBaseExerciseList();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new BaseExerciseAdapter(getContext(), exercises);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    private void createBaseExercise(String name, String description) {
        long id = db.createBaseExercise(name, description);
        BaseExercise e = db.getBaseExercise(id);
        if (e != null) {
            exercises.add(e);
            mAdapter.notifyItemInserted(exercises.size() - 1);
        }
    }

    private class BaseExerciseAdapter extends RecyclerView.Adapter {
        private final Context context;
        private List<BaseExercise> exercises;

        public class BaseExerciseViewHolder extends RecyclerView.ViewHolder {
            private TextView text1;

            // Reuse workout_card for display purposes
            public BaseExerciseViewHolder(View itemView) {
                super(itemView);
                text1 = (TextView) itemView;
            }
        }

        public BaseExerciseAdapter(Context context, List<BaseExercise> exercises) {
            this.context = context;
            this.exercises = exercises;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new BaseExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            BaseExercise e = exercises.get(position);
            ((BaseExerciseViewHolder) holder).text1.setText(e.getName());
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }
    }
}
