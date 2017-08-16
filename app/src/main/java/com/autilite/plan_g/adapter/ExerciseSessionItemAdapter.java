package com.autilite.plan_g.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.autilite.plan_g.R;
import com.autilite.plan_g.program.session.SetSession;

import java.util.List;

/**
 * Created by Kelvin on Jul 5, 2017.
 */

public class ExerciseSessionItemAdapter extends ArrayAdapter<SetSession> {

    public ExerciseSessionItemAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<SetSession> sets) {
        super(context, resource, sets);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.session_exercise_item_listview, null);
        }

        SetSession set = getItem(position);
        if (set != null) {
            TextView setNoView = (TextView) view.findViewById(R.id.exercise_item_set_no);
            TextView repWeightView = (TextView) view.findViewById(R.id.exercise_item_repweight);

            int reps = set.getReps();
            double weight = set.getWeight();

            setNoView.setText(getContext().getResources().getString(R.string.set) + " "
                    + String.valueOf(set.getSetNumber()));
            repWeightView.setText(
                    reps < 0 || weight < 0 ?
                            "" : String.valueOf(reps + "x" + weight));
        }

        return view;
    }
}
