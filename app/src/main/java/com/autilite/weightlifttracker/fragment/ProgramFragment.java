package com.autilite.weightlifttracker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
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
import com.autilite.weightlifttracker.activity.EditProgram;
import com.autilite.weightlifttracker.adapter.ProgramAdapter;
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.fragment.dialog.AbstractCreateDialog;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramFragment extends Fragment implements AbstractCreateDialog.CreateDialogListener {

    private static final int CREATE_PROGRAM = 1;
    private WorkoutDatabase workoutDb;
    private List<Program> programs;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ProgramAdapter mAdapter;

    public ProgramFragment() {
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
//                CreateProgramDialog frag = new CreateProgramDialog();
//                frag.setTargetFragment(ProgramFragment.this, 0);
//                frag.show(getActivity().getSupportFragmentManager(), "CreateProgramDialog");
                Intent intent = new Intent(getContext(), EditProgram.class);
                startActivityForResult(intent, CREATE_PROGRAM);
            }
        });
        workoutDb = new WorkoutDatabase(getActivity());
        programs = workoutDb.getAllProgramsList();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ProgramAdapter(getActivity(), programs);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TableLayout table = (TableLayout) dialog.getDialog().findViewById(R.id.entry_create_table);
        EditText nameEditText = (EditText) dialog.getDialog().findViewById(R.id.heading_create_name_editview);
        String programName = nameEditText.getText().toString();

        // 1) Create the program
        if (programName.equals("")) {
            Toast.makeText(getActivity(), "Program could not be created", Toast.LENGTH_LONG).show();
            return;
        }
        // The number of days fixed at 3 is a bug. Since I am going to be re-designing the
        // CreateProgram form, we will leave this stub here rather than try and fix it
        long programId = workoutDb.createProgram(programName, 3);
        if (programId == -1) {
            Toast.makeText(getActivity(), "Program could not be created", Toast.LENGTH_LONG).show();
            return;
        }
        Program program = new Program(programId, programName, "");

        // 2) Retrieve the values from the table
        // Ignore first row because that's the header
        for (int i = 1; i < table.getChildCount(); i++) {
            TableRow c = (TableRow) table.getChildAt(i);
            // Workout Button
            Button workoutBtn = (Button) c.findViewById(R.id.program_create_workout_chooser);
            String workoutName = workoutBtn.getText().toString();
            // WorkoutId is stored in tag
            Object buttonTag = workoutBtn.getTag();
            if (buttonTag == null) {
                continue;
            }
            long workoutId = Long.parseLong(buttonTag.toString());

            // Day spinner
            AppCompatSpinner daySpinner = (AppCompatSpinner) c.findViewById(R.id.program_create_day);
            int day = Integer.parseInt(daySpinner.getSelectedItem().toString());

            // 3) Validate and insert into table
            if (!workoutDb.addWorkoutToProgram(programId, workoutId, day)){
                Toast.makeText(getActivity(), "Could not add " + workoutName +
                        " to " + programName, Toast.LENGTH_LONG).show();
            }

            Toast.makeText(getActivity(), "Workout " + workoutName + " added to " + programName,
                    Toast.LENGTH_LONG).show();

            Workout workout = new Workout(workoutId, workoutName, "");
            program.addWorkout(workout);
        }

        // Update the UI
        programs.add(program);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

}
