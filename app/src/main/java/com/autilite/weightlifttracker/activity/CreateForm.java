package com.autilite.weightlifttracker.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.fragment.CreateWorkout;

/**
 * Created by Kelvin on Jul 21, 2017.
 */

public class CreateForm extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable mDrawable = ContextCompat.getDrawable(this, R.drawable.ic_fail_black_24dp);
        drawableToColor(mDrawable, R.color.colorWhite);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(mDrawable);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new CreateWorkout())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_form, menu);
        Drawable icon = menu.findItem(R.id.create_form).getIcon();
        drawableToColor(icon, R.color.colorWhite);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_form:
                // TODO save workout
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void drawableToColor(Drawable drawable, int colorResId) {
        int color = ContextCompat.getColor(this, colorResId);
        DrawableCompat.setTint(drawable, color);
    }
}
