package com.autilite.weightlifttracker.program;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kelvin on 09/12/16.
 */

public class Program {
    private String name;
    private String description;
    private List<Workout> workouts;

    public Program(String name, String description) {
        this.name = name;
        this.description = description;
        workouts = new LinkedList<>();
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
        // don't add exercise if there's workout of the same name
        for (Workout w: workouts) {
            if (w.getName().equals(workout.getName())) {
                return false;
            }
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
