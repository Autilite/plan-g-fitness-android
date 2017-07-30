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
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProgramFragment extends Fragment {

    private static final String ARG_PROGRAM_OBJ = "ARG_PROGRAM_OBJ";

    private static final long PROGRAM_NOT_SELECTED = -1;

    private RecyclerView mRecyclerView;
    private AddWorkoutAdapter mAdapter;
    private WorkoutDatabase db;

    private EditText mEditName;
    private EditText mEditDescription;

    private List<Workout> workouts;

    private Program program;
    private long programId;
    private String programName;

    private boolean isNewEntry;

    public EditProgramFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Program program) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_PROGRAM_OBJ, program);

        EditProgramFragment fragment = new EditProgramFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new WorkoutDatabase(getContext());
        if (getArguments() != null) {
            program = getArguments().getParcelable(ARG_PROGRAM_OBJ);
            if (program != null) {
                programId = program.getId();
                programName = program.getName();
                isNewEntry = false;
            } else {
                programId = PROGRAM_NOT_SELECTED;
                isNewEntry = true;
            }
        } else {
            programId = PROGRAM_NOT_SELECTED;
            isNewEntry = true;
        }
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

    public Program save() {
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
