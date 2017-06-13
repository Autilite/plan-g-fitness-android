package com.autilite.weightlifttracker;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.autilite.weightlifttracker.database.ExerciseInfoContract;
import com.autilite.weightlifttracker.database.WorkoutProgramDbHelper;

/**
 * Created by Kelvin on Jun 9, 2017.
 */

public class CreateWorkoutDialog extends DialogFragment {

    public interface CreateWorkoutListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);

    }
    // Use this instance of the interface to deliver action events
    CreateWorkoutListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mListener = (CreateWorkoutListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement CreateWorkoutListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_create_workout, null);

        builder.setTitle(R.string.create_workout_title)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogPositiveClick(CreateWorkoutDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(CreateWorkoutDialog.this);
                    }
                });

        Dialog d = builder.create();

        final TableLayout table = (TableLayout) view.findViewById(R.id.workout_create_table);
        Button button = (Button) view.findViewById(R.id.button_add_exercise);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                TableRow row = (TableRow) inflater.inflate(R.layout.workout_table_row, table,false);
                table.addView(row);
            }
        });

        final WorkoutProgramDbHelper db = new WorkoutProgramDbHelper(getActivity());
        final Button exerciseChooser = (Button) view.findViewById(R.id.workout_create_exercise_chooser);
        exerciseChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final Cursor cursor = db.getAllExerciseInfo();
                builder.setTitle(R.string.choose_exercise)
                        .setCursor(cursor, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Set button text to the chosen exercise
                                // and store the exerciseId in the button tag
                                cursor.moveToPosition(i);
                                long exerciseId = cursor.getLong(cursor.getColumnIndex(ExerciseInfoContract.ExerciseInfoEntry._ID));
                                String name = cursor.getString(cursor.getColumnIndex(ExerciseInfoContract.ExerciseInfoEntry.COLUMN_NAME));
                                exerciseChooser.setText(name);
                                exerciseChooser.setTag(exerciseId);
                                cursor.close();
                            }
                        }, ExerciseInfoContract.ExerciseInfoEntry.COLUMN_NAME);
                builder.show();
            }
        });

        return d;
    }

}
