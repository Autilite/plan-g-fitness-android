package com.autilite.weightlifttracker.program;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kelvin on 09/12/16.
 */

public class Program {
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
}
