package com.autilite.weightlifttracker.program;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kelvin on 09/12/16.
 */

public class Workout {
    private String name;
    private List<Exercise> mainExercises;
    private List<Exercise> accessoryExercises;

    public Workout(String name) {
        this.name = name;
        mainExercises = new ArrayList<>(4);
        accessoryExercises = new ArrayList<>(4);
    }

    public List<Exercise> getMainExercises() {
        return mainExercises;
    }

    public List<Exercise> getAccessoryExercises() {
        return accessoryExercises;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean addMainExercise(Exercise exercise) {
        // don't add exercise if there's an exercise with the same name
        for (Exercise e: mainExercises) {
            if (e.getName().equals(exercise.getName())) {
                return false;
            }
        }
        mainExercises.add(exercise);
        return true;
    }

    public boolean addAccessoryExercise(Exercise exercise) {
        // don't add exercise if there's an exercise with the same name
        for (Exercise e: accessoryExercises) {
            if (e.getName().equals(exercise.getName())) {
                return false;
            }
        }
        accessoryExercises.add(exercise);
        return true;
    }

    public void removeMainExercise(Exercise exercise) {
        mainExercises.remove(exercise);
    }

    public void removeMainExercise(String exercise) {
        Iterator<Exercise> it = mainExercises.iterator();
        while (it.hasNext()) {
            Exercise e = it.next();
            if (e.getName().equals(exercise)) {
                it.remove();
            }
        }
    }

    public void removeAccessoryExercise(Exercise exercise) {
        accessoryExercises.remove(exercise);
    }

    public void removeAccessoryExercise(String exercise) {
        Iterator<Exercise> it = accessoryExercises.iterator();
        while (it.hasNext()) {
            Exercise e = it.next();
            if (e.getName().equals(exercise)) {
                it.remove();
            }
        }
    }
}
