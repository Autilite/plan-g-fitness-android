package com.autilite.weightlifttracker.program;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kelvin on 09/12/16.
 */

public class Workout implements Parcelable {
    private final long id;
    private String name;
    private String description;
    private List<Exercise> exercises;

    public Workout(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        exercises = new ArrayList<>(4);
    }

    protected Workout(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        exercises = in.createTypedArrayList(Exercise.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
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

    public long getId() {
        return id;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
