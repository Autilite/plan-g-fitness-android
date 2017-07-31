package com.autilite.weightlifttracker.fragment;


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
import com.autilite.weightlifttracker.program.BaseModel;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProgramFragment extends AbstractFormFragment {

    private RecyclerView mRecyclerView;
    private AddWorkoutAdapter mAdapter;

    private EditText mEditName;
    private EditText mEditDescription;

    private List<List<Workout>> listOfWorkouts;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_form, container, false);
        mEditName = (EditText) view.findViewById(R.id.input_name);
        mEditDescription = (EditText) view.findViewById(R.id.input_description);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        listOfWorkouts = new ArrayList<>();
        mAdapter = new AddWorkoutAdapter(listOfWorkouts);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    protected BaseModel insertNewEntry() {
        return null;
    }

    @Override
    protected BaseModel editEntry() {
        return null;
    }

    public class AddWorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_VIEW = 0;
        private static final int CONTENT_VIEW = 1;
        private static final int FOOTER_VIEW = 2;

        private static final int HEADER_SIZE = 1;
        private static final int FOOTER_SIZE = 1;

        private List<List<Workout>> days;

        public AddWorkoutAdapter(List<List<Workout>> workouts) {
            this.days = workouts;
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
                TextView text = dvh.dayText;
                String format = String.format(getString(R.string.day_placeholder), String.valueOf(getDay(position)));
                text.setText(format);
                dvh.bindWorkoutList(getContentWorkouts(position));
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

        private List<Workout> getContentWorkouts(int position) {
            return listOfWorkouts.get(position - HEADER_SIZE);
        }

        private int getDay(int position) {
            // Add 1 to change 0-index to 1-index
            return position - HEADER_SIZE + 1;
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
            private List<Workout> workouts;

            public DayViewHolder(View itemView) {
                super(itemView);
                dayText = (TextView) itemView.findViewById(R.id.text1);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getItemViewType() == FOOTER_VIEW) {
                            // Add new day row
                            listOfWorkouts.add(new ArrayList<Workout>());
                            notifyDayInserted(listOfWorkouts.size() - 1);
                        } else {
                            // TODO Start dialog to edit the day
                        }
                    }
                });
            }

            private void bindWorkoutList(List<Workout> workouts) {
                this.workouts = workouts;
            }
        }
    }
}
