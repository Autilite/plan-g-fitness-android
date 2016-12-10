package com.autilite.weightlifttracker.program;

/**
 * Created by kelvin on 09/12/16.
 */

public class Exercise {
    private String name;
    private int sets;
    private int reps;
    private int weight;
    private double weightIncrement;
    private long restTime;

    public Exercise(String name, int sets, int reps, int weight, double increment, long breakTime) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.weightIncrement = increment;
        this.restTime = breakTime;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getWeightIncrement() {
        return weightIncrement;
    }

    public void setWeightIncrement(double weightIncrement) {
        this.weightIncrement = weightIncrement;
    }

    public long getRestTime() {
        return restTime;
    }

    public void setRestTime(long restTime) {
        this.restTime = restTime;
    }
}