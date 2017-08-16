package com.autilite.plan_g.program;

import android.os.Parcel;

/**
 * Created by kelvin on 09/12/16.
 */

public class Exercise extends BaseModel {
    private long baseExerciseId;
    private int sets;
    private int reps;
    private double weight;
    private double weightIncrement;
    private int restTime;           // In seconds

    public Exercise(long id, String name, String description, long baseExerciseId, int sets, int reps, double weight, double weightIncrement, int restTime) {
        // TODO convert to builder pattern
        super(id, name, description);
        this.baseExerciseId = baseExerciseId;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.weightIncrement = weightIncrement;
        this.restTime = restTime;
    }

    protected Exercise(Parcel in) {
        super(in);
        baseExerciseId = in.readLong();
        sets = in.readInt();
        reps = in.readInt();
        weight = in.readDouble();
        weightIncrement = in.readDouble();
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

    public long getBaseExerciseId() {
        return baseExerciseId;
    }

    public void setBaseExerciseId(long baseExerciseId) {
        this.baseExerciseId = baseExerciseId;
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

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeightIncrement() {
        return weightIncrement;
    }

    public void setWeightIncrement(double weightIncrement) {
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
        super.writeToParcel(parcel, i);
        parcel.writeLong(baseExerciseId);
        parcel.writeInt(sets);
        parcel.writeInt(reps);
        parcel.writeDouble(weight);
        parcel.writeDouble(weightIncrement);
        parcel.writeInt(restTime);
    }
}
