package com.autilite.weightlifttracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CreateWorkoutDialog.CreateWorkoutListener
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
        } else if (id == R.id.nav_workout) {
            mTitle = getString(R.string.nav_workout);
            onWorkoutSelected();
        } else if (id == R.id.nav_programs) {
            mTitle = getString(R.string.nav_programs);
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

    private void setTitle() {
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setTitle(mTitle);
    }

    public void onWorkoutSelected() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new WorkoutFragment())
                .commit();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TableLayout table = (TableLayout) dialog.getDialog().findViewById(R.id.workout_create_table);
        EditText nameEditText = (EditText) dialog.getDialog().findViewById(R.id.workout_create_name);
        String workoutName = nameEditText.getText().toString();

        // Ignore first row since that is the headings
        for (int i = 1; i < table.getChildCount(); i++) {
            View c = table.getChildAt(i);
            if (c instanceof TableRow) {
                TableRow row = (TableRow) c;
                Button exercise = (Button) row.findViewById(R.id.workout_create_exercise_chooser);
                Toast.makeText(this, exercise.getTag().toString(), Toast.LENGTH_LONG).show();
                EditText sets = (EditText) row.findViewById(R.id.workout_create_sets);
                EditText reps = (EditText) row.findViewById(R.id.workout_create_reps);
                EditText weight = (EditText) row.findViewById(R.id.workout_create_weight);

                String wExercise = exercise.getText().toString();
                int wSets = Integer.parseInt(sets.getText().toString());
                int wReps = Integer.parseInt(reps.getText().toString());
                float wWeight = Float.parseFloat(weight.getText().toString());

                if (!workoutDb.insertWorkout(workoutName, wExercise, wSets, wReps, wWeight)) {
                    Toast.makeText(this, "Exercise " +wExercise + " could not be inserted", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    @Override
    protected void onDestroy() {
        workoutDb.close();
        super.onDestroy();
    }
}
