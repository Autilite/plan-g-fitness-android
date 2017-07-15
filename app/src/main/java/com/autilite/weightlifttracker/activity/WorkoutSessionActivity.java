package com.autilite.weightlifttracker.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.WorkoutService;
import com.autilite.weightlifttracker.fragment.WorkoutSessionFragment;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.session.ExerciseSession;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelvin on Jun 16, 2017.
 */

public class WorkoutSessionActivity extends AppCompatActivity implements WorkoutSessionFragment.OnFragmentInteractionListener {

    public static String EXTRA_PROGRAM_ID = "EXTRA_PROGRAM_ID";
    public static String EXTRA_PROGRAM_NAME = "EXTRA_PROGRAM_NAME";

    private long programId;
    private String programName;

    private ViewPager mPager;
    private WorkoutPagerAdapter mAdapter;
    private FloatingActionButton mFab;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    private ExerciseSession mExerciseSession;

    private TextView mExerciseTextView;
    private TextView mSetTextView;
    private TextView mTimerTextView;
    private EditText mRepEditText;
    private EditText mWeightEditText;

    private boolean mBound = false;
    private WorkoutService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        programId = intent.getLongExtra(EXTRA_PROGRAM_ID, -1);
        programName = intent.getStringExtra(EXTRA_PROGRAM_NAME);

        setContentView(R.layout.activity_workout_session);

        setupToolbar();
        setupBottomSheets();
        setupFab();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to service
        Intent intent = new Intent(this, WorkoutService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WorkoutService.LocalBinder binder = (WorkoutService.LocalBinder) iBinder;
            mService = binder.getService();
            mBound = true;

            setupPager(mService.getWorkouts());

            mExerciseSession = mService.getCurrentExercise();
            if (mExerciseSession != null) {
                updateBottomSheetView();
            }
            updateFabVisibility();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workout_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.option_finish_workout:
                stopService(new Intent(this, WorkoutService.class));
                // TODO save data
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(programName);
    }

    private void setupPager(List<Workout> workouts) {
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
                if (mExerciseSession.completeSet(reps, weight)) {
                    Exercise exercise = mExerciseSession.getExercise();
                    exercise.setReps(reps);
                    exercise.setWeight(weight);
                    mPager.getAdapter().notifyDataSetChanged();
                    updateBottomSheetView();

                    mService.startTimer(mExerciseSession.getExercise().getRestTime() * 1000);
                }
            }
        });
    }

    private void updateFabVisibility() {
        if (mExerciseSession != null) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onExerciseSelected(ExerciseSession es) {
        if (es != null && !es.equals(mExerciseSession)) {
            mService.setSelectedExercise(es);
            mExerciseSession = es;
            updateBottomSheetView();
        }
        if (mFab.getVisibility() == View.GONE) {
            updateFabVisibility();
        }

    }

    private void updateBottomSheetView() {
        Exercise e = mExerciseSession.getExercise();
        mExerciseTextView.setText(e.getName());

        int currentSet = mExerciseSession.getCurrentSet();
        String setString = getResources().getString(R.string.set) + " "
                + currentSet + "/" + e.getSets();
        String completeSet = getResources().getString(R.string.exercise_complete);

        String s = currentSet != ExerciseSession.EXERCISE_COMPLETE ? setString : completeSet;
        mSetTextView.setText(s);

        mRepEditText.setText(String.valueOf(e.getReps()));
        mWeightEditText.setText(String.valueOf(e.getWeight()));
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
            ArrayList<? extends ExerciseSession> s = mService.getSession(w);
            return WorkoutSessionFragment.newInstance(w.getId(), w.getName(), s);
        }

        @Override
        public int getItemPosition(Object object) {
            WorkoutSessionFragment fragment = (WorkoutSessionFragment) object;
            if (fragment != null) {
                fragment.notifyAdapterDataChanged();
            }
            return super.getItemPosition(object);
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
