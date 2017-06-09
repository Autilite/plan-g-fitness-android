package com.autilite.weightlifttracker.program;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kelvin on 09/12/16.
 */

public class Workout {
    private String name;
    private List<Exercise> exercises;

    public Workout(String name) {
        this.name = name;
        exercises = new ArrayList<>(4);
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

    public boolean addExercise(Exercise exercise) {
        // don't add exercise if there's an exercise with the same name
        for (Exercise e: exercises) {
            if (e.getName().equals(exercise.getName())) {
                return false;
            }
        }
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
