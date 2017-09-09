package com.autilite.plan_g.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.autilite.plan_g.R;
import com.autilite.plan_g.fragment.AbstractBaseModelFragment;
import com.autilite.plan_g.fragment.EditProgramFragment;
import com.autilite.plan_g.program.BaseModel;
import com.autilite.plan_g.program.Program;
import com.autilite.plan_g.program.Workout;

import java.util.List;

/**
 * Created by Kelvin on Jul 25, 2017.
 */

public class EditProgram extends CreateForm {
    private Program program;

    @Override
    protected AbstractBaseModelFragment getCreateModelInstance() {
        setTitle(R.string.create_program);
        return EditProgramFragment.newInstance(null);
    }

    @Override
    protected AbstractBaseModelFragment getEditModelInstance(@NonNull BaseModel model) {
        setTitle(R.string.edit_program);
        program = (Program) model;
        return EditProgramFragment.newInstance(program);
    }

    @Override
    protected boolean onDeleteEntry(@NonNull BaseModel model) {
        return false;
    }

    @Override
    protected BaseModel insertNewEntry(Bundle fields) {
        String name = fields.getString(AbstractBaseModelFragment.FIELD_KEY_NAME);
        String description = fields.getString(AbstractBaseModelFragment.FIELD_KEY_DESCRIPTION);
        List<Program.Day> programDays = fields.getParcelableArrayList(EditProgramFragment.FIELD_KEY_PROGRAM_DAYS);

        if (name == null || name.equals("") || programDays == null) {
            return null;
        }

        int numDays = programDays.size();

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

    @Override
    protected BaseModel editEntry(Bundle fields) {
        String name = fields.getString(AbstractBaseModelFragment.FIELD_KEY_NAME);
        String description = fields.getString(AbstractBaseModelFragment.FIELD_KEY_DESCRIPTION);
        List<Program.Day> programDays = fields.getParcelableArrayList(EditProgramFragment.FIELD_KEY_PROGRAM_DAYS);

        if (name == null || name.equals("") || programDays == null) {
            return null;
        }

        if (db.updateProgram(program.getId(), name, description, programDays)) {
            program.setName(name);
            program.setDescription(description);
            program.setDays(programDays);
            return program;
        } else {
            return null;
        }
    }
}
