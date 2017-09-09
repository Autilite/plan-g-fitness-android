package com.autilite.plan_g.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.AbstractBaseModelFragment;
import com.autilite.plan_g.fragment.EditProgramFragment;
import com.autilite.plan_g.program.Program;
import com.autilite.plan_g.program.Workout;

import java.util.List;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditProgram extends CreateForm {
    public static final String EXTRA_PROGRAM = "EXTRA_PROGRAM";

    public static final String RESULT_ACTION = "com.autilite.plan_g.activity.EditProgram.RESULT_ACTION";
    public static final String EXTRA_RESULT_PROGRAM = "EXTRA_RESULT_PROGRAM";

    private Program program;

    @Override
    protected AbstractBaseModelFragment createContentFragment() {
        if (getIntent().getExtras() != null) {
            setTitle(R.string.edit_program);
            formType = Type.EDIT;
            program = getIntent().getParcelableExtra(EXTRA_PROGRAM);
            return EditProgramFragment.newInstance(program);
        } else {
            setTitle(R.string.create_program);
            formType = Type.CREATE;
            return EditProgramFragment.newInstance(null);
        }
    }

    @Override
    protected boolean onDeleteEntryCallback() {
        return false;
    }

    @Override
    public boolean onSave(Bundle fields) {
        String name = fields.getString(AbstractBaseModelFragment.FIELD_KEY_NAME);
        String description = fields.getString(AbstractBaseModelFragment.FIELD_KEY_DESCRIPTION);
        List<Program.Day> programDays = fields.getParcelableArrayList(EditProgramFragment.FIELD_KEY_PROGRAM_DAYS);

        if (formType == Type.CREATE) {
            program = insertNewEntry(name, description, programDays);
        } else {
            program = editEntry(program.getId(), name, description, programDays);
        }
        saveSuccessful = program != null;

        if (!saveSuccessful) {
            Toast.makeText(this, R.string.create_program_fail, Toast.LENGTH_SHORT).show();
        } else {
            Intent result = new Intent(RESULT_ACTION);
            result.putExtra(EXTRA_RESULT_PROGRAM, program);
            setResult(Activity.RESULT_OK, result);
        }
        return saveSuccessful;
    }

    protected Program insertNewEntry(String name, String description, List<Program.Day> programDays) {
        int numDays = programDays.size();

        if (name.equals("")) {
            return null;
        }

        long programId = db.createProgram(name, description, numDays);
        if (programId == -1)
            return null;

        Program program = new Program(programId, name, description, numDays);

        // Add the workouts to the database and model
        for (int i = 0; i < numDays; i++) {
            Program.Day workoutDay = programDays.get(i);

            int day = i + 1;
            for (Workout w : workoutDay.getWorkouts()) {
                db.addWorkoutToProgram(programId, w.getId(), day);
            }
            // Since we initialized program to numDays, we need to set the individual day
            program.getDays().set(i, workoutDay);
        }

        return program;
    }

    protected Program editEntry(long id, String name, String description, List<Program.Day> programDays) {
        if (name.equals("")) {
            return null;
        }

        if (db.updateProgram(id, name, description, programDays)) {
            Program p = new Program(id, name, description, programDays.size());
            p.setDays(programDays);
            return p;
        } else {
            return null;
        }
    }
}
