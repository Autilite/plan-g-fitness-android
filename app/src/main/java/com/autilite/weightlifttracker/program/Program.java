package com.autilite.weightlifttracker.program;

import android.os.Parcel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kelvin on 09/12/16.
 */

public class Program extends BaseModel {
    private List<Workout> workouts;

    public Program(long id, String name, String description) {
        super(id, name, description);
        workouts = new LinkedList<>();
    }

    protected Program(Parcel in) {
        super(in);
        workouts = in.createTypedArrayList(Workout.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(workouts);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Program> CREATOR = new Creator<Program>() {
        @Override
        public Program createFromParcel(Parcel in) {
            return new Program(in);
        }

        @Override
        public Program[] newArray(int size) {
            return new Program[size];
        }
    };

    public boolean addWorkout(Workout workout) {
        if (workout == null) {
            return false;
        }
        workouts.add(workout);
        return true;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void removeWorkout(Workout workout) {
        workouts.remove(workout);
    }

    public void removeWorkout(String name) {
        Iterator<Workout> it = workouts.iterator();
        while (it.hasNext()) {
            Workout w = it.next();
            if (w.getName().equals(name))
                it.remove();
        }
    }
}
