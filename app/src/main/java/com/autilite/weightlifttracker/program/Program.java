package com.autilite.weightlifttracker.program;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kelvin on 09/12/16.
 */

public class Program extends BaseModel {
    private List<Day> days;

    public Program(long id, String name, String description, int numDays) {
        super(id, name, description);
        days = new ArrayList<>();

        for (int i = 0; i < numDays; i++) {
            days.add(new Day());
        }
    }

    protected Program(Parcel in) {
        super(in);
        days = in.createTypedArrayList(Day.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(days);
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

    public int getNumDays() {
        return days.size();
    }

    public List<Day> getDays() {
        return days;
    }

    public boolean addWorkout(int day, Workout workout) {
        int index = day - 1;
        return days.get(index).addWorkout(workout);
    }

    public void removeWorkout(int day, Workout workout) {
        int index = day - 1;
        days.get(index).removeWorkout(workout);
    }

    public void removeWorkout(int day, long id) {
        int index = day - 1;
        days.get(index).removeWorkout(id);
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public static class Day implements Parcelable {

        private List<Workout> workouts;

        public Day() {
            this.workouts = new ArrayList<>();
        }

        protected Day(Parcel in) {
            workouts = in.createTypedArrayList(Workout.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(workouts);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Day> CREATOR = new Creator<Day>() {
            @Override
            public Day createFromParcel(Parcel in) {
                return new Day(in);
            }

            @Override
            public Day[] newArray(int size) {
                return new Day[size];
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

        public void removeWorkout(long id) {
            Iterator<Workout> it = workouts.iterator();
            while (it.hasNext()) {
                Workout w = it.next();
                if (w.getId() == id)
                    it.remove();
            }
        }

    }
}
