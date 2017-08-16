package com.autilite.plan_g.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.autilite.plan_g.R;
import com.autilite.plan_g.service.WorkoutService;
import com.autilite.plan_g.fragment.WorkoutSessionFragment;
import com.autilite.plan_g.program.Exercise;
import com.autilite.plan_g.program.session.ExerciseSession;
import com.autilite.plan_g.program.Workout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelvin on Jun 16, 2017.
 */

public class WorkoutSessionActivity extends AppCompatActivity implements WorkoutSessionFragment.OnFragmentInteractionListener {

    public static String EXTRA_PROGRAM_ID = "EXTRA_PROGRAM_ID";
    public static String EXTRA_PROGRAM_NAME = "EXTRA_PROGRAM_NAME";
    public static String EXTRA_PROGRAM_DAY = "EXTRA_PROGRAM_DAY";

    private long programId;
    private String programName;
    private int programDay;

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
        programDay = intent.getIntExtra(EXTRA_PROGRAM_DAY, -1);

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

        IntentFilter actions = new IntentFilter();
        actions.addAction(WorkoutService.BROADCAST_COUNTDOWN);
        actions.addAction(WorkoutService.BROADCAST_UPDATED_SESSION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mTimerReceiver, actions);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTimerReceiver);
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

    private BroadcastReceiver mTimerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WorkoutService.BROADCAST_COUNTDOWN.equals(intent.getAction())) {
                if (intent.getExtras() != null) {
                    long millisUntilFinished = intent.getLongExtra(WorkoutService.EXTRA_BROADCAST_COUNTDOWN, 0);
                    String timer = new SimpleDateFormat("mm:ss").format(millisUntilFinished);
                    mTimerTextView.setText(timer);
                }
            } else if (WorkoutService.BROADCAST_UPDATED_SESSION.equals(intent.getAction())) {
                mPager.getAdapter().notifyDataSetChanged();
                updateBottomSheetView();
            }
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
                Intent intent = new Intent(this, WorkoutService.class);
                intent.setAction(WorkoutService.ACTION_SAVE_SESSION);
                startService(intent);

                stopService(new Intent(this, WorkoutService.class));
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

        // TODO setup watcher so the service gets the rep/weight value when it is changed
        // Currently, it is set so that the service only knows the user-input when they click
        // on the fab.
        // There is another case where the user can complete the set via notification.
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
                mService.completeSet(reps, weight);
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

        int currentSet = mExerciseSession.getCurrentSet();
        int currentRep = mExerciseSession.getCurrentRep();
        double currentWeight = mExerciseSession.getCurrentWeight();

        String setString = getResources().getString(R.string.set) + " "
                + currentSet + "/" + e.getSets();
        String completeSet = getResources().getString(R.string.exercise_complete);

        String s = mExerciseSession.isSessionDone() ? completeSet : setString;

        mExerciseTextView.setText(e.getName());
        mSetTextView.setText(s);
        mRepEditText.setText(String.valueOf(currentRep));
        mWeightEditText.setText(String.valueOf(currentWeight));
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
