package com.autilite.weightlifttracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.fragment.AbstractFormFragment;
import com.autilite.weightlifttracker.fragment.EditProgramFragment;
import com.autilite.weightlifttracker.program.BaseModel;
import com.autilite.weightlifttracker.program.Program;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditProgram extends CreateForm {
    public static final String EXTRA_PROGRAM = "EXTRA_PROGRAM";

    public static final String RESULT_ACTION = "com.autilite.weightlifttracker.activity.EditProgram.RESULT_ACTION";
    public static final String EXTRA_RESULT_PROGRAM = "EXTRA_RESULT_PROGRAM";

    @Override
    protected AbstractFormFragment createContentFragment() {
        if (getIntent().getExtras() != null) {
            setTitle(R.string.edit_program);
            Program program = getIntent().getParcelableExtra(EXTRA_PROGRAM);
            return EditProgramFragment.newInstance(program);
        } else {
            setTitle(R.string.create_program);
            return EditProgramFragment.newInstance(null);
        }
    }

    @Override
    protected boolean saveForm() {
        BaseModel model = contentFragment.save();
        boolean isSuccess = model != null;
        if (!isSuccess) {
            Toast.makeText(this, R.string.create_program_fail, Toast.LENGTH_SHORT).show();
        } else {
            Intent result = new Intent(RESULT_ACTION);
            result.putExtra(EXTRA_RESULT_PROGRAM, model);
            setResult(Activity.RESULT_OK, result);
        }
        return isSuccess;
    }
}
