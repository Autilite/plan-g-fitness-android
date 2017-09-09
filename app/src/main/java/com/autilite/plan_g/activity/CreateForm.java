package com.autilite.plan_g.activity;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.autilite.plan_g.R;

/**
 * Created by Kelvin on Jul 21, 2017.
 */

public abstract class CreateForm extends AppCompatActivity {

    protected Fragment contentFragment;

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

        contentFragment = createContentFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, contentFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_form, menu);
        Drawable trash = menu.findItem(R.id.delete_form).getIcon();
        Drawable check = menu.findItem(R.id.create_form).getIcon();
        drawableToColor(trash, R.color.colorWhite);
        drawableToColor(check, R.color.colorWhite);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_form:
                if (saveForm()) {
                    finish();
                }
                return true;
            case R.id.delete_form:
                showDeleteFormDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void drawableToColor(Drawable drawable, int colorResId) {
        int color = ContextCompat.getColor(this, colorResId);
        DrawableCompat.setTint(drawable, color);
    }

    protected abstract Fragment createContentFragment();

    protected void showDeleteFormDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.form_delete)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onDeleteEntryCallback()) {
                            finish();
                        } else {
                            Toast.makeText(CreateForm.this, R.string.form_delete_failed, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }

    /**
     * The function called to for deleting the entry for the form
     *
     * @return  true if the entry content is deleted successfully
     *          false otherwise
     */
    protected abstract boolean onDeleteEntryCallback();

    /**
     * Save the form
     *
     * @return  true if the save was successful.
     *          false otherwise
     */
    protected abstract boolean saveForm();
}
