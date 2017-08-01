package com.autilite.weightlifttracker.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.autilite.weightlifttracker.R;

public class ChooseWorkouts extends AppCompatActivity {

    public static final String EXTRA_PROGRAM_ID = "EXTRA_PROGRAM_ID";
    public static final String EXTRA_DAY = "EXTRA_PROGRAM_DAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_workout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
