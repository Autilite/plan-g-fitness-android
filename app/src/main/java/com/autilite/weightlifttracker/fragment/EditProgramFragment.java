package com.autilite.weightlifttracker.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.activity.ChooseWorkouts;
import com.autilite.weightlifttracker.program.BaseModel;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProgramFragment extends AbstractFormFragment {

    private static final int CHOOSE_WORKOUT = 1;

    private RecyclerView mRecyclerView;
    private AddWorkoutAdapter mAdapter;

    private EditText mEditName;
    private EditText mEditDescription;

    private List<Program.Day> programDays;

    public EditProgramFragment() {
        // Required empty public constructor
    }

    public static AbstractFormFragment newInstance(Program program) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_MODEL_OBJ, program);

        EditProgramFragment fragment = new EditProgramFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        programDays = getProgramDayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_form, container, false);
        mEditName = (EditText) view.findViewById(R.id.input_name);
        mEditDescription = (EditText) view.findViewById(R.id.input_description);

        if (getFormType() == Type.EDIT) {
            mEditName.setText(model.getName());
            mEditDescription.setText(model.getDescription());
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new AddWorkoutAdapter(programDays);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    protected BaseModel insertNewEntry() {
        name = mEditName.getText().toString();
        String description = mEditDescription.getText().toString();
        int numDays = programDays.size();

        if (name.equals("")) {
            return null;
        }

        long programId = db.createProgram(name, description, numDays);
        if (programId == -1)
            return null;

        Program program = new Program(programId, name, description, numDays);
        for (int i = 0; i < numDays; i++) {
            Program.Day workoutDay = programDays.get(i);

            int day = i + 1;
            for (Workout w : workoutDay.getWorkouts()) {
                db.addWorkoutToProgram(programId, w.getId(), day);
            }
        }

        for (int day = 1; day < numDays + 1; day++) {
            List<Workout> workouts = db.getProgramWorkouts(programId, day);
            for (Workout w : workouts) {
                program.addWorkout(day, w);
            }
        }

        return program;
    }

    @Override
    protected BaseModel editEntry() {
        name = mEditName.getText().toString();
        String description = mEditDescription.getText().toString();

        if (name.equals("")) {
            return null;
        }

        if (db.updateProgram(id, name, description, programDays)) {
            Program program = new Program(id, name, description, programDays.size());
            program.setDays(programDays);
            return program;
        } else {
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_WORKOUT) {
            if (resultCode == Activity.RESULT_OK) {
                Long[] ids = (Long[]) data.getSerializableExtra(ChooseWorkouts.EXTRA_RESULT_CHOSEN_WORKOUTS);
                int day = data.getIntExtra(ChooseWorkouts.EXTRA_RESULT_DAY, -1);
                int index = day - 1;
                programDays.set(index, dayFromWorkoutIds(ids));

                // At the moment, the adapter does not use the list of IDs for drawing the View
                // Because of this, we don't need to notify the adapter that the data has changed
//                mAdapter.notifyDayChanged(day);
            }
        }
    }

    private List<Program.Day> getProgramDayList() {
        if (getFormType() == Type.EDIT) {
            Program program = (Program) model;
            return program.getDays();

        } else {
            return new ArrayList<>();
        }
    }

    public class AddWorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_VIEW = 0;
        private static final int CONTENT_VIEW = 1;
        private static final int FOOTER_VIEW = 2;

        private static final int HEADER_SIZE = 1;
        private static final int FOOTER_SIZE = 1;

        private List<Program.Day> days;

        public AddWorkoutAdapter(List<Program.Day> programDays) {
            this.days = programDays;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == HEADER_VIEW) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false);
                return new TitleViewHolder(view);
            }
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_list_item1, parent, false);
            return new DayViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == HEADER_VIEW) {
                TextView text = ((TitleViewHolder) holder).textView;
                text.setText(getString(R.string.days));
            } else if (holder.getItemViewType() == CONTENT_VIEW) {
                DayViewHolder dvh = (DayViewHolder) holder;

                int day = getDay(position);
                dvh.setDay(day);
            } else if (holder.getItemViewType() == FOOTER_VIEW) {
                TextView text = ((DayViewHolder) holder).dayText;
                text.setText(R.string.add_day);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position < HEADER_SIZE) {
                return HEADER_VIEW;
            } else if (position >= HEADER_SIZE + days.size()) {
                return FOOTER_VIEW;
            } else
                return CONTENT_VIEW;
        }

        @Override
        public int getItemCount() {
            return days.size() + HEADER_SIZE + FOOTER_SIZE;
        }

        private Program.Day getContentWorkouts(int position) {
            return days.get(position - HEADER_SIZE);
        }

        private int getDay(int position) {
            // Add 1 to change 0-index to 1-index
            return position - HEADER_SIZE + 1;
        }

        private Program.Day getProgramDay(int day) {
            return days.get(day - 1);
        }

        /**
         * A helper function to notify any registered observers that the <code>day</code>
         * reflected at <code>position</code> has been newly inserted.
         *
         * @param position Position of the newly inserted day in the data set
         * @see #notifyItemInserted(int)
         */
        public void notifyDayInserted(int position) {
            notifyItemInserted(HEADER_SIZE + position);
        }

        /**
         * A helper function to notify any registered observers that the <code>day</code>
         * reflected at <code>position</code> has been changed.
         *
         * @param position Position of the newly inserted day in the data set
         * @see #notifyItemChanged(int)
         */
        public void notifyDayChanged(int position) {
            notifyItemChanged(HEADER_SIZE + position);
        }

        public class TitleViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public TitleViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.header);
            }
        }

        public class DayViewHolder extends RecyclerView.ViewHolder {

            private TextView dayText;

            private int day;

            public DayViewHolder(View itemView) {
                super(itemView);
                dayText = (TextView) itemView.findViewById(R.id.text1);

                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (getItemViewType() == FOOTER_VIEW) {
                            days.add(new Program.Day());
                            notifyDayInserted(days.size() - 1);
                        } else {
                            Program.Day programDay = getProgramDay(day);
                            Long[] ids = workoutIdsFromDay(programDay);

                            Intent intent = new Intent(getActivity(), ChooseWorkouts.class);
                            intent.putExtra(ChooseWorkouts.EXTRA_DAY, day);
                            intent.putExtra(ChooseWorkouts.EXTRA_SELECTED_IDS, ids);
                            startActivityForResult(intent, CHOOSE_WORKOUT);
                        }
                    }
                });
            }

            private void setDay(int day) {
                this.day = day;

                String format = String.format(getString(R.string.day_placeholder), String.valueOf(day));
                dayText.setText(format);
            }

        }
    }

    private Program.Day dayFromWorkoutIds(Long[] ids) {
        Program.Day day = new Program.Day();
        for (Long wId : ids) {
            Workout w = db.getWorkout(wId);
            day.addWorkout(w);
        }
        return day;
    }

    private Long[] workoutIdsFromDay(Program.Day day) {
        List<Workout> workouts = day.getWorkouts();
        Long[] ids = new Long[workouts.size()];
        for (int i = 0; i < workouts.size(); i++) {
            ids[i] = workouts.get(i).getId();
        }
        return ids;
    }
}
