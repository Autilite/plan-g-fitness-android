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
    public static final long NO_PROGRAM_SELECTED = -1;
    private static final String SELECTED_PROGRAM = "SELECTED_PROGRAM";

    private long selectedProgram;


    public StartProgramFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param selectedProgram The program to display
     * @return A new instance of fragment StartProgramFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartProgramFragment newInstance(long selectedProgram) {
        StartProgramFragment fragment = new StartProgramFragment();
        Bundle args = new Bundle();
        args.putLong(SELECTED_PROGRAM, selectedProgram);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedProgram = getArguments().getLong(SELECTED_PROGRAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.fragment_start_program, container, false);

        // Display selected program
        TextView textView = new TextView(view.getContext());
        textView.setText(String.valueOf(selectedProgram));
        view.addView(textView);
        return view;
    }

}
