package com.autilite.weightlifttracker.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autilite.weightlifttracker.IAdapterUpdate;
import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.adapter.ExerciseSessionAdapter;
import com.autilite.weightlifttracker.program.session.ExerciseSession;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutSessionFragment extends Fragment implements IAdapterUpdate{
    private static final String ARG_ID = "ID";
    private static final String ARG_NAME = "NAME";
    private static final String ARG_SESSION = "SESSION";

    private OnFragmentInteractionListener mListener;

    private long id;
    private String name;
    private ArrayList<? extends ExerciseSession> session;

    private RecyclerView mRecyclerView;
    private ExerciseSessionAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private View view;


    public WorkoutSessionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param name Parameter 2.
     * @return A new instance of fragment WorkoutSessionFragment.
     */
    public static WorkoutSessionFragment newInstance(long id, String name,
                                                     ArrayList<? extends ExerciseSession> session ) {
        WorkoutSessionFragment fragment = new WorkoutSessionFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putParcelableArrayList(ARG_SESSION, session);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong(ARG_ID);
            name = getArguments().getString(ARG_NAME);
            session = getArguments().getParcelableArrayList(ARG_SESSION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Return the view if it has already been instantiated
        if (view != null)
            return view;

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recycle_view, container, false);

        // Disable fab
        View fab = view.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ExerciseSessionAdapter(getActivity(), session);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ExerciseSessionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, ExerciseSession session) {
                mListener.onExerciseSelected(session);
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void notifyAdapterDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        void onExerciseSelected(ExerciseSession es);
    }
}
