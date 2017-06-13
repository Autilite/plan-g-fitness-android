package com.autilite.weightlifttracker.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.fragment.dialog.AbstractCreateDialog;
import com.autilite.weightlifttracker.fragment.dialog.CreateProgramDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramFragment extends Fragment implements AbstractCreateDialog.CreateDialogListener {

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
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateProgramDialog frag = new CreateProgramDialog();
                frag.setTargetFragment(ProgramFragment.this, 0);
                frag.show(getActivity().getSupportFragmentManager(), "CreateProgramDialog");
            }
        });
        return view;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // TODO
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
