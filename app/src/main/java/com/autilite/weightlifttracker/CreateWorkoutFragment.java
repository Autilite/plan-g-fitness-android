package com.autilite.weightlifttracker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

/**
 * Created by Kelvin on Jun 9, 2017.
 */

public class CreateWorkoutFragment extends DialogFragment {

    public interface CreateWorkoutListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);

    }
    // Use this instance of the interface to deliver action events
    CreateWorkoutListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host context implements the callback interface
        try {
            // Instantiate the CreateWorkoutListener so we can send events to the host
            mListener = (CreateWorkoutListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
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
                        mListener.onDialogPositiveClick(CreateWorkoutFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(CreateWorkoutFragment.this);
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
        return d;
    }

}
