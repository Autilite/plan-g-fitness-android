package com.autilite.plan_g.program;

import android.os.Parcel;

/**
 * Created by kelvin on 09/12/16.
 */

public class Exercise extends BaseModel {
    private long baseExerciseId;
    private int sets;
    private int reps;
    private int repsMin;
    private int repsMax;
    private int repsIncrement;
    private double weight;
    private double weightIncrement;
    private int restTime;           // In seconds

    public Exercise(long id, String name, String description, long baseExerciseId, int sets, int reps, int repsMin, int repsMax, int repsIncr, double weight, double weightIncrement, int restTime) {
        // TODO convert to builder pattern
        super(id, name, description);
        this.baseExerciseId = baseExerciseId;
        this.sets = sets;
        this.reps = reps;
        this.repsMin = repsMin;
        this.repsMax = repsMax;
        this.repsIncrement = repsIncr;
        this.weight = weight;
        this.weightIncrement = weightIncrement;
        this.restTime = restTime;
    }

    protected Exercise(Parcel in) {
        super(in);
        baseExerciseId = in.readLong();
        sets = in.readInt();
        reps = in.readInt();
        repsMin = in.readInt();
        repsMax = in.readInt();
        repsIncrement = in.readInt();
        weight = in.readDouble();
        weightIncrement = in.readDouble();
        restTime = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(baseExerciseId);
        dest.writeInt(sets);
        dest.writeInt(reps);
        dest.writeInt(repsMin);
        dest.writeInt(repsMax);
        dest.writeInt(repsIncrement);
        dest.writeDouble(weight);
        dest.writeDouble(weightIncrement);
        dest.writeInt(restTime);
    }

    @Override
    public int describeContents() {
        return 0;
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
        setRepRange(reps, repsMin, repsMax);
    }

    public int getRepsMin() {
        return repsMin;
    }

    public void setRepsMin(int min) {
        setRepRange(reps, min, repsMax);
    }

    public int getRepsMax() {
        return repsMax;
    }

    public void setRepsMax(int max) {
        setRepRange(reps, repsMin, max);
    }

    /**
     * Atomic method for setting the rep, min, max fields while ensuring the range constraint
     *
     * @param reps The new current repetition
     * @param min The lowest repetition for the exercise
     * @param max The highest repetition for the exercise
     */
    public void setRepRange(int reps, int min, int max) {
        if (isValidInterval(reps, min, max)) {
            this.reps = reps;
            repsMin = min;
            repsMax = max;
        } else {
            throw new IllegalArgumentException("Range must satisfy 0 < repsMin <= reps <= repsMax." +
                    " Found reps=" + reps + ", [" + min + "," + max +"]");
        }
    }

    private boolean isValidInterval(int reps, int min, int max) {
        return min > 0 && min <= max && reps >= min && reps <= max;
    }

    public int getRepsIncrement() {
        return repsIncrement;
    }

    public void setRepsIncrement(int repsIncrement) {
        this.repsIncrement = repsIncrement;
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

}
