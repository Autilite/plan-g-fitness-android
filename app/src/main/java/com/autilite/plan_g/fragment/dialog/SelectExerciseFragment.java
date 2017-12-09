package com.autilite.plan_g.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.autilite.plan_g.R;
import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.fragment.LinkExerciseEntry;
import com.autilite.plan_g.fragment.NewEntrySelectExercise;
import com.autilite.plan_g.program.BaseExercise;
import com.autilite.plan_g.program.Exercise;

/**
 * Created by kelvin on 11/27/17.
 */

public class SelectExerciseFragment extends DialogFragment implements NewEntrySelectExercise.OnFragmentInteractionListener,
        LinkExerciseEntry.OnFragmentInteractionListener{

    private RadioGroup radioGroup;

    private OnExerciseSelectInteractionListener mListener;
    private static WorkoutDatabase db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new WorkoutDatabase(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_exercise, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().setTitle(R.string.select_exercise);

        radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup_choose_exercise);
        radioGroup.setOnCheckedChangeListener(onCheckedChangedListener);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            switch (i) {
                case R.id.radio_exercise_new:
                    fragmentTransaction.replace(R.id.content_selector, new NewEntrySelectExercise());
                    break;
                case R.id.radio_exercise_link:
                    fragmentTransaction.replace(R.id.content_selector, new LinkExerciseEntry());
                    break;
                default:
                    break;
            }
            fragmentTransaction.commit();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExerciseSelectInteractionListener) {
            mListener = (OnExerciseSelectInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExerciseSelectInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onNewExerciseEntry(BaseExercise exercise) {
        mListener.onNewExerciseEntry(exercise);
    }

    @Override
    public void onLinkExerciseEntry(Exercise exercise) {
        mListener.onLinkExerciseEntry(exercise);
    }

    public interface OnExerciseSelectInteractionListener {
        void onNewExerciseEntry(BaseExercise exercise);
        void onLinkExerciseEntry(Exercise exercise);
    }

}
