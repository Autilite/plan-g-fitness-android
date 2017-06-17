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
 * Use the {@link WorkoutSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutSessionFragment extends Fragment {
    private static final String ARG_ID = "ID";
    private static final String ARG_NAME = "NAME";

    private long id;
    private String name;


    public WorkoutSessionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param name Parameter 2.
     * @return A new instance of fragment WorkoutSessionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutSessionFragment newInstance(long id, String name) {
        WorkoutSessionFragment fragment = new WorkoutSessionFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putString(ARG_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong(ARG_ID);
            name = getArguments().getString(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // TODO
        return inflater.inflate(R.layout.fragment_workout_session, container, false);
    }

}
