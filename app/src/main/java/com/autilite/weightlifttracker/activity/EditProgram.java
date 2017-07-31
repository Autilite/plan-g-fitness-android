package com.autilite.weightlifttracker.activity;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.fragment.AbstractFormFragment;
import com.autilite.weightlifttracker.fragment.EditProgramFragment;
import com.autilite.weightlifttracker.program.Program;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditProgram extends CreateForm {
    public static final String EXTRA_PROGRAM = "EXTRA_PROGRAM";

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
        return false;
    }
}
