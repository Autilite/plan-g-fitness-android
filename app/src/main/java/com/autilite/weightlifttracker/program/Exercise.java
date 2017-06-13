package com.autilite.weightlifttracker.program;

/**
 * Created by kelvin on 09/12/16.
 */

public class Exercise {
    private String name;
    private int sets;
    private int reps;
    private float weight;
    private float weightIncrement;
    private int restTime;           // In seconds

    public Exercise(String name, int sets, int reps, float weight) {
        // TODO configurable default values
        this(name, sets, reps, weight, 5);
    }

    public Exercise(String name, int sets, int reps, float weight, float weightIncrement) {
        // TODO configurable default values
        this(name, sets, reps, weight, 5, 90);
    }

    public Exercise(String name, int sets, int reps, float weight, float increment, int restTime) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.weightIncrement = increment;
        this.restTime = restTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getWeightIncrement() {
        return weightIncrement;
    }

    public void setWeightIncrement(float weightIncrement) {
        this.weightIncrement = weightIncrement;
    }

    public long getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }
}