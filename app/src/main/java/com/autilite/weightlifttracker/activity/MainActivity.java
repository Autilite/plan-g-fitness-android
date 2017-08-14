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
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.ProgramContract;
import com.autilite.weightlifttracker.database.WorkoutDatabase;
import com.autilite.weightlifttracker.fragment.ProgramFragment;
import com.autilite.weightlifttracker.fragment.StartProgramFragment;
import com.autilite.weightlifttracker.fragment.WorkoutFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private CharSequence mTitle;
    private WorkoutDatabase workoutDb;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = new Spinner(getSupportActionBar().getThemedContext());
        toolbar.addView(spinner);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        workoutDb = new WorkoutDatabase(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        MenuItem item = navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(item);
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
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            spinner.setVisibility(View.GONE);
        }

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // change R.id.content_main
        // change toolbar
        // ->set title
        if (id == R.id.nav_profile) {
            mTitle = getString(R.string.nav_profile);
        } else if (id == R.id.nav_start_session) {
            mTitle =  getString(R.string.start_session);
            if (getSupportActionBar() != null){
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                spinner.setVisibility(View.VISIBLE);
            }
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
        // Set Spinner
        String[] from = new String[]{ProgramContract.ProgramEntry.COLUMN_NAME};
        int[] to = new int[]{android.R.id.text1};
        Cursor programs = workoutDb.getProgramTable();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, android.R.layout.simple_spinner_dropdown_item, programs, from, to, 0);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor item = (Cursor) adapterView.getAdapter().getItem(i);
                long id = item.getLong(
                        item.getColumnIndex(ProgramContract.ProgramEntry._ID));
                String name = item.getString(
                        item.getColumnIndex(ProgramContract.ProgramEntry.COLUMN_NAME));

                // Save selection in SharedPrefs
                SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                editor.putLong(getString(R.string.last_used_program), id);
                editor.apply();

                // Start the fragment
                StartProgramFragment f
                        = StartProgramFragment.newInstance(id, name, workoutDb.getProgramDay(MainActivity.this, id));
                replaceContentFragment(f);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        long no_program_selected = -1;

        // Get the last used program
        long savedProgramId = sharedPrefs.getLong(getString(R.string.last_used_program), no_program_selected);

        // Get the position of the last used program in the cursor
        int position = getCursorPosition(savedProgramId, programs);
        if (position == -1) {
            // Last used program not found in cursor. Default to first option.
            spinner.setSelection(0);
            // TODO handle case where there are no programs
        } else {
            spinner.setSelection(position);
        }
    }

    /**
     * Returns id's position in cursor or -1 if id is not in the cursor
     * @param id
     * @param cursor
     * @return
     */
    private int getCursorPosition(long id, Cursor cursor) {
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            long locationId = cursor.getLong(cursor.getColumnIndex(ProgramContract.ProgramEntry._ID));
            if (locationId == id){
                return i;
            }
            cursor.moveToNext();
        }
        return -1;
    }

    /**
     * Show an alert dialog to select and switch to the selected program
     */
    private void selectAndSwitchProgram(){
        final Cursor programs = workoutDb.getProgramTable();

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
                        StartProgramFragment f =
                                StartProgramFragment.newInstance(progId, progName, workoutDb.getProgramDay(MainActivity.this, progId));
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
