package com.autilite.weightlifttracker.program;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kelvin on 09/12/16.
 */

public class Exercise implements Parcelable {
    private final long id;
    private String name;
    private int sets;
    private int reps;
    private double weight;
    private float weightIncrement;
    private int restTime;           // In seconds

    public Exercise(long id, String name, int sets, int reps, double weight) {
        // TODO configurable default values
        this(id, name, sets, reps, weight, 5);
    }

    public Exercise(long id, String name, int sets, int reps, double weight, float weightIncrement) {
        // TODO configurable default values
        this(id, name, sets, reps, weight, weightIncrement, 90);
    }

    public Exercise(long id, String name, int sets, int reps, double weight, float weightIncrement, int restTime) {
        this.id = id;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.weightIncrement = weightIncrement;
        this.restTime = restTime;
    }

    protected Exercise(Parcel in) {
        id = in.readLong();
        name = in.readString();
        sets = in.readInt();
        reps = in.readInt();
        weight = in.readDouble();
        weightIncrement = in.readFloat();
        restTime = in.readInt();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public double getWeightIncrement() {
        return weightIncrement;
    }

    public void setWeightIncrement(float weightIncrement) {
        this.weightIncrement = weightIncrement;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeInt(sets);
        parcel.writeInt(reps);
        parcel.writeDouble(weight);
        parcel.writeFloat(weightIncrement);
        parcel.writeInt(restTime);
    }
}