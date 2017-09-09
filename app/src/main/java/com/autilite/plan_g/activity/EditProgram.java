package com.autilite.plan_g.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.AbstractFormFragment;
import com.autilite.plan_g.fragment.EditProgramFragment;
import com.autilite.plan_g.program.BaseModel;
import com.autilite.plan_g.program.Program;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditProgram extends CreateForm {
    public static final String EXTRA_PROGRAM = "EXTRA_PROGRAM";

    public static final String RESULT_ACTION = "com.autilite.plan_g.activity.EditProgram.RESULT_ACTION";
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
    protected boolean onDeleteEntryCallback() {
        return false;
    }

    @Override
    protected boolean saveForm() {
        BaseModel model = ((AbstractFormFragment) contentFragment).save();
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
