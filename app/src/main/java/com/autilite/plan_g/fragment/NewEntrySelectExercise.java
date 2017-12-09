package com.autilite.plan_g.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.autilite.plan_g.database.ExerciseContract;
import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.program.BaseExercise;

public class NewEntrySelectExercise extends Fragment {
    private OnFragmentInteractionListener mListener;
    private WorkoutDatabase db;

    public NewEntrySelectExercise() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new WorkoutDatabase(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ListView list = new ListView(getContext());

        final Cursor cursor = db.getBaseExerciseTable();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1,
                cursor,
                new String[] {ExerciseContract.BaseExerciseEntry.COLUMN_NAME},
                new int[] { android.R.id.text1},
                0);
        list.setAdapter(adapter);
        layout.addView(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                cursor.moveToPosition(position);
                long baseExerciseId = cursor.getLong(cursor.getColumnIndex(ExerciseContract.BaseExerciseEntry._ID));
                String name = cursor.getString(cursor.getColumnIndex(ExerciseContract.BaseExerciseEntry.COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(ExerciseContract.BaseExerciseEntry.COLUMN_DESCRIPTION));

                BaseExercise baseExercise = new BaseExercise(baseExerciseId, name, description);
                mListener.onNewExerciseEntry(baseExercise);
            }
        });

        return layout;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(getParentFragment().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onNewExerciseEntry(BaseExercise exercise);
    }
}
