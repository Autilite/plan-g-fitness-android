package com.autilite.weightlifttracker.program;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kelvin on 09/12/16.
 */

public class Workout extends BaseModel {
    private List<Exercise> exercises;

    public Workout(long id, String name, String description) {
        super(id, name, description);
        exercises = new ArrayList<>(4);
    }

    protected Workout(Parcel in) {
        super(in);
        exercises = in.createTypedArrayList(Exercise.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(exercises);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    public List<Exercise> getExercises() {
        return exercises;
    }

    public boolean addExercise(Exercise exercise) {
        if (exercise == null)
            return false;
        exercises.add(exercise);
        return true;
    }

    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
    }

    public void removeExercise(String exercise) {
        Iterator<Exercise> it = exercises.iterator();
        while (it.hasNext()) {
            Exercise e = it.next();
            if (e.getName().equals(exercise)) {
                it.remove();
            }
        }
    }

}
