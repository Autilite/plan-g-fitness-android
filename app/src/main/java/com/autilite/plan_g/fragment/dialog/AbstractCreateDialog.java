package com.autilite.plan_g.fragment.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.autilite.plan_g.R;
import com.autilite.plan_g.database.WorkoutDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class AbstractCreateDialog extends DialogFragment {
    protected WorkoutDatabase db;

    public interface CreateDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);

    }
    // Use this instance of the interface to deliver action events
    CreateDialogListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mListener = (CreateDialogListener) getTargetFragment();
            db = new WorkoutDatabase(getActivity());
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement CreateWorkoutListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dialog_create_entry, null);

        // Create entry name
        TextView createNameHeading = (TextView) view.findViewById(R.id.heading_create_name);
        EditText createNameEditable = (EditText) view.findViewById(R.id.heading_create_name_editview);
        // Set their text values
        createNameHeading.setText(getCreateEntryName());
        createNameEditable.setHint(getCreateEntryName());

        builder.setTitle(getTitle())
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogPositiveClick(AbstractCreateDialog.this);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(AbstractCreateDialog.this);
                    }
                });

        // Populate table
        final TableLayout table = (TableLayout) view.findViewById(R.id.entry_create_table);

        TableRow row = getTableHeading();
        table.addView(row);

        // Set button to add table row
        Button button = (Button) view.findViewById(R.id.button_add_exercise);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTableRow(table);
            }
        });

        // Provide a single entry table row by default
        addTableRow(table);

        return builder.create();
    }

    protected abstract TableRow getTableHeading();

    protected abstract int getCreateEntryName();

    protected abstract int getTitle();

    protected abstract void addTableRow(TableLayout table);

}
