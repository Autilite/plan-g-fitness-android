package com.autilite.weightlifttracker.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.ProgramContract;
import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;
import com.autilite.weightlifttracker.fragment.ProgramFragment;
import com.autilite.weightlifttracker.fragment.StartProgramFragment;
import com.autilite.weightlifttracker.fragment.WorkoutFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private CharSequence mTitle;
    private WorkoutProgramDbHelper workoutDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_workout);

        workoutDb = new WorkoutProgramDbHelper(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // change R.id.content_main
        // change toolbar
        // ->set title
        if (id == R.id.nav_profile) {
            mTitle = getString(R.string.nav_profile);
        } else if (id == R.id.nav_start_session) {
            mTitle =  getString(R.string.start_session);
            onStartSessionSelected();
        } else if (id == R.id.nav_workout) {
            mTitle = getString(R.string.nav_workout);
            onWorkoutSelected();
        } else if (id == R.id.nav_programs) {
            mTitle = getString(R.string.nav_programs);
            onProgramSelected();
        } else if (id == R.id.nav_setting) {
            // open settings fragment
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_rate_review) {
        } else if (id == R.id.nav_send_feedback) {
        }
        setTitle();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onStartSessionSelected() {
        final SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        long no_program_selected = -1;

        // Get the last used program
        long progId = sharedPrefs.getLong(getString(R.string.last_used_program), no_program_selected);
        String progName = workoutDb.getProgramName(progId);

        // If there is no last selection or if the program can't be found in the database (deleted),
        // then prompt the user to select a new program
        if (progId == no_program_selected || progName == null) {
            selectAndSwitchProgram();
        } else {
            // Otherwise, switch to the fragment
            StartProgramFragment f = StartProgramFragment.newInstance(progId, progName);
            replaceContentFragment(f);
        }
    }

    private void selectAndSwitchProgram(){
        final Cursor programs = workoutDb.getAllPrograms();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_program)
                .setCursor(programs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get selected program
                        programs.moveToPosition(i);
                        long progId = programs.getLong(
                                programs.getColumnIndex(ProgramContract.ProgramEntry._ID));
                        String progName = programs.getString(
                                programs.getColumnIndex(ProgramContract.ProgramEntry.COLUMN_NAME));
                        programs.close();

                        // Save selection in SharedPrefs
                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putLong(getString(R.string.last_used_program), progId);
                        editor.apply();

                        // Start the fragment
                        StartProgramFragment f = StartProgramFragment.newInstance(progId, progName);
                        replaceContentFragment(f);
                    }

                }, ProgramContract.ProgramEntry.COLUMN_NAME);
        builder.create().show();
    }

    private void replaceContentFragment(Fragment frag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, frag)
                .commit();
    }

    private void onProgramSelected() {
        replaceContentFragment(new ProgramFragment());
    }

    private void setTitle() {
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setTitle(mTitle);
    }

    public void onWorkoutSelected() {
        replaceContentFragment(new WorkoutFragment());
    }

    @Override
    protected void onDestroy() {
        workoutDb.close();
        super.onDestroy();
    }
}
