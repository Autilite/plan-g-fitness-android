package com.autilite.weightlifttracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;
import com.autilite.weightlifttracker.fragment.WorkoutSessionFragment;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.session.Session;
import com.autilite.weightlifttracker.program.Workout;

import java.util.List;

/**
 * Created by Kelvin on Jun 16, 2017.
 */

public class WorkoutSessionActivity extends AppCompatActivity implements WorkoutSessionFragment.OnFragmentInteractionListener {

    public static String EXTRA_PROGRAM_ID = "EXTRA_PROGRAM_ID";

    private long programId;
    private WorkoutProgramDbHelper workoutDb;
    private List<Workout> workouts;
    private Session session;

    private ViewPager mPager;
    private WorkoutPagerAdapter mAdapter;
    private FloatingActionButton mFab;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    private Exercise mSelectedExercise;

    private TextView mExerciseTextView;
    private TextView mSetTextView;
    private TextView mTimerTextView;
    private EditText mRepEditText;
    private EditText mWeightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        programId = intent.getLongExtra(EXTRA_PROGRAM_ID, -1);

        setContentView(R.layout.activity_workout_session);

        workoutDb = new WorkoutProgramDbHelper(this);
        workouts = workoutDb.getProgramWorkouts(programId);

        setupToolbar();
        setupPager();
        setupBottomSheets();
        setupFab();

        session = new Session();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String programName = workoutDb.getProgramName(programId);
        setTitle(programName);
    }

    private void setupPager() {
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new WorkoutPagerAdapter(getSupportFragmentManager(), workouts);
        mPager.setAdapter(mAdapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(mPager);
    }

    private void setupBottomSheets() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_layout));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mExerciseTextView = (TextView) findViewById(R.id.bottom_sheet_heading);
        mExerciseTextView.setText(R.string.choose_exercise);
        mSetTextView = (TextView) findViewById(R.id.bottom_sheet_set);
        mTimerTextView = (TextView) findViewById(R.id.bottom_sheet_timer);

        mRepEditText = (EditText) findViewById(R.id.edit_reps);
        mWeightEditText = (EditText) findViewById(R.id.edit_weight);
    }

    private void setupFab() {
        mFab = (FloatingActionButton) findViewById(R.id.session_fab);
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String repsString = mRepEditText.getText().toString();
                String weightString = mWeightEditText.getText().toString();
                int reps;
                float weight;
                try {
                    reps = Integer.parseInt(repsString);
                } catch (NumberFormatException e) {
                    reps = 0;
                }
                try {
                    weight = Float.parseFloat(weightString);
                } catch (NumberFormatException e) {
                    weight = 0;
                }
                if (session.completeSet(mSelectedExercise, reps, weight)) {
                    mSelectedExercise.setReps(reps);
                    mSelectedExercise.setWeight(weight);
                    updateBottomSheetView();
                    // TODO reset timer
                }
            }
        });
    }

    @Override
    public void onExerciseSelected(Exercise e) {
        if (mFab.getVisibility() == View.GONE) {
            mFab.setVisibility(View.VISIBLE);
        }

        if (e != null && !e.equals(mSelectedExercise)) {
            mSelectedExercise = e;
            updateBottomSheetView();
        }
    }

    private void updateBottomSheetView() {
        mExerciseTextView.setText(mSelectedExercise.getName());

        int currentSet = session.getCurrentSet(mSelectedExercise);
        String setString = getResources().getString(R.string.set) + " "
                + currentSet + "/" + mSelectedExercise.getSets();
        String completeSet = getResources().getString(R.string.exercise_complete);

        String s = currentSet != Session.EXERCISE_COMPLETE ? setString : completeSet;
        mSetTextView.setText(s);

        mRepEditText.setText(String.valueOf(mSelectedExercise.getReps()));
        mWeightEditText.setText(String.valueOf(mSelectedExercise.getWeight()));
    }

    @Override
    protected void onDestroy() {
        workoutDb.close();
        super.onDestroy();
    }

    private class WorkoutPagerAdapter extends FragmentPagerAdapter {
        private List<Workout> workouts;

        WorkoutPagerAdapter(FragmentManager fm, List<Workout> workouts) {
            super(fm);
            this.workouts = workouts;
        }

        @Override
        public Fragment getItem(int position) {
            Workout w = workouts.get(position);
            return WorkoutSessionFragment.newInstance(w.getId(), w.getName());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return workouts.get(position).getName();
        }

        @Override
        public int getCount() {
            return workouts.size();
        }
    }
}
