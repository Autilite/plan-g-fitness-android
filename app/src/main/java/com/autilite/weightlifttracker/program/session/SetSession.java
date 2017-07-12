package com.autilite.weightlifttracker.program.session;

/**
 * Created by Kelvin on Jul 5, 2017.
 */

public class SetSession {
    private int setNumber;
    private int reps;
    private double weight;

    protected SetSession(int setNumber, int reps, double weight) {
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
