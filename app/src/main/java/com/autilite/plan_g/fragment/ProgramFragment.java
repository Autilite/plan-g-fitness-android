package com.autilite.plan_g.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autilite.plan_g.R;
import com.autilite.plan_g.activity.CreateForm;
import com.autilite.plan_g.activity.EditProgram;
import com.autilite.plan_g.adapter.ProgramAdapter;
import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.program.Program;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramFragment extends Fragment implements ProgramAdapter.IProgramViewHolderClick {
    private static final String TAG = ProgramFragment.class.getName();

    private static final int CREATE_PROGRAM = 1;
    private static final int EDIT_PROGRAM = 2;

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
                Intent intent = new Intent(getContext(), EditProgram.class);
                startActivityForResult(intent, CREATE_PROGRAM);
            }
        });
        workoutDb = new WorkoutDatabase(getActivity());
        programs = workoutDb.getProgramTableList();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ProgramAdapter(getActivity(), programs, this);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_PROGRAM) {
            if (resultCode == Activity.RESULT_OK) {
                Program program = data.getParcelableExtra(CreateForm.EXTRA_RESULT_MODEL);
                programs.add(program);
                mAdapter.notifyItemInserted(programs.size() - 1);
            }
        } else if (requestCode == EDIT_PROGRAM) {
            if (resultCode == Activity.RESULT_OK) {
                Program resultProgram = data.getParcelableExtra(CreateForm.EXTRA_RESULT_MODEL);

                for (int i = 0; i < programs.size(); i++) {
                    Program curProgram = programs.get(i);
                    if (resultProgram.getId() == curProgram.getId()) {
                        programs.set(i, resultProgram);
                        mAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            } else if (resultCode == CreateForm.RESULT_DELETED) {
                long deletedId = data.getLongExtra(CreateForm.EXTRA_RESULT_DELETED_ID, -1);
                if (deletedId == -1) {
                    Log.w(TAG, "The model id was not found in the Intent data.");
                    return;
                }
                for (int i = 0; i < programs.size(); i++) {
                    Program p = programs.get(i);
                    if (p.getId() == deletedId) {
                        programs.remove(i);
                        mAdapter.notifyItemRemoved(i);
                        mAdapter.notifyItemRangeChanged(i, mAdapter.getItemCount());
                    }
                }
            }
        }
    }

    @Override
    public void onProgramSelect(Program program) {
        Intent intent = new Intent(getContext(), EditProgram.class);
        intent.putExtra(EditProgram.EXTRA_BASE_MODEL, program);
        startActivityForResult(intent, EDIT_PROGRAM);

    }
}
