package com.autilite.plan_g.fragment.dialog;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.autilite.plan_g.R;
import com.autilite.plan_g.database.WorkoutContract;

/**
 * Created by Kelvin on Jun 13, 2017.
 */

public class CreateProgramDialog extends AbstractCreateDialog {
    @Override
    protected TableRow getTableHeading() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        return (TableRow) inflater.inflate(R.layout.program_table_row_heading, null);
    }

    @Override
    protected int getCreateEntryName() {
        return R.string.program_name;
    }

    @Override
    protected int getTitle() {
        return R.string.create_program_title;
    }

    @Override
    protected void addTableRow(TableLayout table) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        TableRow row = (TableRow) inflater.inflate(R.layout.program_table_row, table,false);

        final Button workoutChooser = (Button) row.findViewById(R.id.program_create_workout_chooser);

        workoutChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final Cursor cursor = db.getWorkoutTable();
                builder.setTitle(R.string.choose_workout)
                        .setCursor(cursor, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Set button text to the chosen workout
                                // and store the workoutId in the button tag
                                cursor.moveToPosition(i);
                                long workoutId = cursor.getLong(cursor.getColumnIndex(WorkoutContract.WorkoutEntry._ID));
                                String name = cursor.getString(cursor.getColumnIndex(WorkoutContract.WorkoutEntry.COLUMN_NAME));
                                workoutChooser.setText(name);
                                workoutChooser.setTag(workoutId);
                                cursor.close();
                            }
                        }, WorkoutContract.WorkoutEntry.COLUMN_NAME);
                builder.show();
            }
        });

        AppCompatSpinner spinner = (AppCompatSpinner) row.findViewById(R.id.program_create_day);
        Integer[] days = getDaysArray(7);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, days);
        spinner.setAdapter(adapter);

        table.addView(row);

    }

    private Integer[] getDaysArray(int max) {
        Integer[] a = new Integer[max];
        for (int i = 0; i < max; i++) {
            a[i] = i+1;
        }
        return a;
    }
}
