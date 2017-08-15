package com.autilite.weightlifttracker.fragment.dialog;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.database.ExerciseContract;

/**
 * Created by Kelvin on Jun 9, 2017.
 */

public class CreateWorkoutDialog extends AbstractCreateDialog {

    @Override
    protected TableRow getTableHeading() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        return (TableRow) inflater.inflate(R.layout.workout_table_row_heading, null);
    }

    @Override
    protected int getCreateEntryName() {
        return R.string.workout_name;
    }

    @Override
    protected int getTitle() {
        return R.string.create_workout_title;
    }

    @Override
    protected void addTableRow(TableLayout table) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        TableRow row = (TableRow) inflater.inflate(R.layout.workout_table_row, table,false);

        final Button exerciseChooser = (Button) row.findViewById(R.id.workout_create_exercise_chooser);
        exerciseChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final Cursor cursor = db.getExerciseInfoTable();
                builder.setTitle(R.string.choose_exercise)
                        .setCursor(cursor, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Set button text to the chosen exercise
                                // and store the exerciseId in the button tag
                                cursor.moveToPosition(i);
                                long exerciseId = cursor.getLong(cursor.getColumnIndex(ExerciseContract.ExerciseInfoEntry._ID));
                                String name = cursor.getString(cursor.getColumnIndex(ExerciseContract.ExerciseInfoEntry.COLUMN_NAME));
                                exerciseChooser.setText(name);
                                exerciseChooser.setTag(exerciseId);
                                cursor.close();
                            }
                        }, ExerciseContract.ExerciseInfoEntry.COLUMN_NAME);
                builder.show();
            }
        });

        table.addView(row);
    }

}
