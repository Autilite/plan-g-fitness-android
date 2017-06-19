package com.autilite.weightlifttracker.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;
import com.autilite.weightlifttracker.fragment.WorkoutSessionFragment;
import com.autilite.weightlifttracker.program.Exercise;
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
    private BottomSheetBehavior<View> bottomSheetBehavior;

    private Exercise mSelectedExercise;
    private TextView mExerciseTextView;
    private TextView mSetTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        programId = intent.getLongExtra(EXTRA_PROGRAM_ID, -1);

        setContentView(R.layout.activity_workout_session);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        workoutDb = new WorkoutProgramDbHelper(this);
        String programName = workoutDb.getProgramName(programId);
        setTitle(programName);

        workouts = workoutDb.getProgramWorkouts(programId);

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        WorkoutPagerAdapter adapter = new WorkoutPagerAdapter(getSupportFragmentManager(), workouts);
        pager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);

        // Setup bottom sheets
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_layout));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mExerciseTextView = (TextView) findViewById(R.id.bottom_sheet_heading);
        mSetTextView = (TextView) findViewById(R.id.bottom_sheet_set);
    }

    @Override
    public void onExerciseSelected(Exercise e) {
        mSelectedExercise = e;
        mExerciseTextView.setText(mSelectedExercise.getName());
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

    @Override
    protected void onDestroy() {
        workoutDb.close();
        super.onDestroy();
    }
}
