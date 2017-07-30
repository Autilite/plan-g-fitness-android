package com.autilite.weightlifttracker.program;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kelvin on 09/12/16.
 */

public class Program implements Parcelable {
    private final long id;
    private String name;
    private String description;
    private List<Workout> workouts;

    public Program(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        workouts = new LinkedList<>();
    }

    protected Program(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        workouts = in.createTypedArrayList(Workout.CREATOR);
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

    public long getId() {
        return id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeTypedList(workouts);
    }
}
