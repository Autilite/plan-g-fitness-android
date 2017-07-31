package com.autilite.weightlifttracker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.program.BaseModel;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProgramFragment extends AbstractFormFragment {

    private RecyclerView mRecyclerView;
    private AddWorkoutAdapter mAdapter;

    private EditText mEditName;
    private EditText mEditDescription;

    private List<Workout> workouts;

    public EditProgramFragment() {
        // Required empty public constructor
    }

    public static AbstractFormFragment newInstance(Program program) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_MODEL_OBJ, program);

        EditProgramFragment fragment = new EditProgramFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_form, container, false);
        mEditName = (EditText) view.findViewById(R.id.input_name);
        mEditDescription = (EditText) view.findViewById(R.id.input_description);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        workouts = new ArrayList<>();
        mAdapter = new AddWorkoutAdapter(workouts);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    protected BaseModel insertNewEntry() {
        return null;
    }

    @Override
    protected BaseModel editEntry() {
        return null;
    }

    public class AddWorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public AddWorkoutAdapter(List<Workout> workouts) {

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
