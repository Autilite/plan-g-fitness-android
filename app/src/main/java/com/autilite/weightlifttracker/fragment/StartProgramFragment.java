package com.autilite.weightlifttracker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartProgramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartProgramFragment extends Fragment {
    private static final String ARG_PROGRAM_ID = "ARG_PROGRAM_ID";
    private static final String ARG_PROGRAM_NAME = "ARG_PROGRAM_NAME";

    private long programId;
    private String programName;


    public StartProgramFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param programId The id of the program
     * @param programName The program name
     * @return A new instance of fragment StartProgramFragment.
     */
    public static StartProgramFragment newInstance(long programId, String programName) {
        StartProgramFragment fragment = new StartProgramFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PROGRAM_ID, programId);
        args.putString(ARG_PROGRAM_NAME, programName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            programId = getArguments().getLong(ARG_PROGRAM_ID);
            programName = getArguments().getString(ARG_PROGRAM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.fragment_start_program, container, false);

        // Inflate view with program data
        TextView textView = new TextView(view.getContext());
        textView.setText(String.valueOf(programId) + " " + programName);
        view.addView(textView);

        return view;
    }

}
