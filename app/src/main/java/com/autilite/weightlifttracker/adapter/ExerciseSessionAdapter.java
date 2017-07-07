package com.autilite.weightlifttracker.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autilite.weightlifttracker.R;
import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.session.ExerciseSession;
import com.autilite.weightlifttracker.widget.ExtendableListView;

import java.util.List;

/**
 * Created by Kelvin on Jun 17, 2017.
 */

public class ExerciseSessionAdapter extends RecyclerView.Adapter<ExerciseSessionAdapter.ExerciseSessionViewHolder> {

    private Context mContext;
    private final List<ExerciseSession> session;
    private OnItemClickListener mListener;

    public class ExerciseSessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int expandedIcon = R.drawable.ic_expand_more_black_24dp;
        int collpaseIcon = R.drawable.ic_expand_less_black_24dp;

        private TextView name;
        private TextView sets;
        private ImageView btnOptions;
        private ImageView expandExercises;
        private ExtendableListView listView;

        private boolean isListVisible;

        public ExerciseSessionViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.exercise_name);
            sets = (TextView) itemView.findViewById(R.id.complete_sets);
            btnOptions = (ImageView) itemView.findViewById(R.id.exercise_options);
            expandExercises = (ImageView) itemView.findViewById(R.id.expand_exercises);
            listView = (ExtendableListView) itemView.findViewById(R.id.exercise_details);
            listView.setVisibility(View.GONE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener !=  null) {
                mListener.onItemClick(view, session.get(getAdapterPosition()));
            }
        }
    }

    public ExerciseSessionAdapter(Context mContext, List<ExerciseSession> exerciseSession) {
        this.mContext = mContext;
        this.session = exerciseSession;
    }

    @Override
    public ExerciseSessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.exercise_session_card, parent, false);
        return new ExerciseSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ExerciseSessionViewHolder holder, int position) {
        ExerciseSession es = session.get(position);
        Exercise e = es.getExercise();
        holder.name.setText(e.getName());
        String completeSet = mContext.getString(R.string.complete_set) + ": "
                + es.getNumCompleteSets() + "/" + e.getSets();
        holder.sets.setText(completeSet);

        // Setup exercise sets
        holder.expandExercises.setImageResource(holder.expandedIcon);
        ExerciseSessionItemAdapter adapter = new ExerciseSessionItemAdapter(mContext, R.layout.session_exercise_item_listview, es.getSetSessions());
        holder.listView.setAdapter(adapter);

        holder.btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(mContext, view);
                menu.getMenuInflater().inflate(R.menu.options_session_exercise, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onMenuClick(item);
                    }
                });
                menu.show();
            }

            private boolean onMenuClick(MenuItem item) {
                // TODO add menu click functionality
                int id = item.getItemId();
                switch (id) {
                    case R.id.option_add_set:
                        break;
                    case R.id.option_edit:
                        break;
                    case R.id.option_mark_complete:
                        break;
                }
                return true;
            }
        });

        holder.expandExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle list visibility
                holder.isListVisible = !holder.isListVisible;
                holder.listView.setVisibility(holder.isListVisible ? View.VISIBLE : View.GONE);
                holder.expandExercises.setImageResource(holder.isListVisible ? holder.collpaseIcon : holder.expandedIcon);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemCount() {
        return session.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, ExerciseSession session);
    }

}
