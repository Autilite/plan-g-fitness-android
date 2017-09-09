package com.autilite.plan_g.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.autilite.plan_g.R;
import com.autilite.plan_g.database.WorkoutDatabase;
import com.autilite.plan_g.fragment.AbstractBaseModelFragment;
import com.autilite.plan_g.program.BaseModel;

/**
 * Created by Kelvin on Jul 21, 2017.
 */

public abstract class CreateForm extends AppCompatActivity implements AbstractBaseModelFragment.OnFragmentInteractionListener {
    private static final String TAG = CreateForm.class.getName();

    public static final String EXTRA_BASE_MODEL = "EXTRA_BASE_MODEL";

    public static final String RESULT_ACTION = "com.autilite.plan_g.activity.CreateForm.RESULT_ACTION";
    public static final String EXTRA_RESULT_MODEL = "EXTRA_RESULT_MODEL";

    public static final int RESULT_DELETED = -2;

    protected AbstractBaseModelFragment contentFragment;

    protected WorkoutDatabase db;

    protected Type formType;

    protected enum Type {
        CREATE,
        EDIT
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_form);

        db = new WorkoutDatabase(this);

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
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    protected AbstractBaseModelFragment createContentFragment(){
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_BASE_MODEL)) {
            formType = Type.EDIT;
            BaseModel model = getIntent().getParcelableExtra(EXTRA_BASE_MODEL);
            return getEditModelInstance(model);
        } else {
            formType = Type.CREATE;
            return getCreateModelInstance();
        }
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
                if (onSave()) {
                    finish();
                }
                return true;
            case R.id.delete_form:
                showDeleteFormDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void drawableToColor(Drawable drawable, int colorResId) {
        int color = ContextCompat.getColor(this, colorResId);
        DrawableCompat.setTint(drawable, color);
    }

    protected void showDeleteFormDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.form_delete)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onDelete()) {
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
     * Retrieve the form data from <code>AbstractBaseModelFragment</code> and set it as the
     * Activity result
     *
     * @return  true if the save was successful.
     *          false otherwise
     */
    protected boolean onSave() {
        BaseModel model = contentFragment.getBaseModel();

        boolean saveSuccessful = model != null;

        if (!saveSuccessful) {
            Toast.makeText(this, R.string.create_form_fail, Toast.LENGTH_SHORT).show();
        } else {
            Intent result = new Intent(RESULT_ACTION);
            result.putExtra(EXTRA_RESULT_MODEL, model);
            setResult(Activity.RESULT_OK, result);
        }
        return saveSuccessful;
    }

    /**
     * Delete the entry
     *
     * @return  true if the entry content is deleted successfully
     *          false otherwise
     */
    protected boolean onDelete() {
        if (formType == Type.CREATE) {
            // Since the form has yet to be saved, we can just return true and finish the activity
            return true;
        } else if (formType == Type.EDIT) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(EXTRA_BASE_MODEL)) {
                BaseModel model = getIntent().getParcelableExtra(EXTRA_BASE_MODEL);

                boolean deleteSuccess = onDeleteEntry(model);
                if (deleteSuccess) {
                    setDeletedResult();
                }
                return deleteSuccess;
            } else {
                Log.w(TAG, "There is no model to delete");
                return false;
            }
        } else {
            Log.w(TAG, "Unknown form type");
            // Return false so the activity does not close on fail
            return false;
        }
    }

    private void setDeletedResult() {
        Intent result = new Intent(RESULT_ACTION);
        setResult(RESULT_DELETED, result);
    }

    @Override
    public BaseModel onRetrieveFormData(Bundle fields) {
        if (formType == Type.CREATE) {
            return insertNewEntry(fields);
        } else if (formType == Type.EDIT) {
            return editEntry(fields);
        } else {
            Log.w(TAG, "Unknown form type");
            return null;
        }
    }

    protected abstract AbstractBaseModelFragment getCreateModelInstance();

    protected abstract AbstractBaseModelFragment getEditModelInstance(@NonNull BaseModel model);

    protected abstract BaseModel insertNewEntry(Bundle fields);

    protected abstract BaseModel editEntry(Bundle fields);

    protected abstract boolean onDeleteEntry(@NonNull BaseModel model);

}
