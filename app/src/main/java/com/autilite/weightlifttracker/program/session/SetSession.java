package com.autilite.weightlifttracker.program.session;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kelvin on Jul 5, 2017.
 */

public class SetSession implements Parcelable {
    private int setNumber;
    private int reps;
    private double weight;

    protected SetSession(int setNumber, int reps, double weight) {
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
    }

    protected SetSession(Parcel in) {
        setNumber = in.readInt();
        reps = in.readInt();
        weight = in.readDouble();
    }

    public static final Creator<SetSession> CREATOR = new Creator<SetSession>() {
        @Override
        public SetSession createFromParcel(Parcel in) {
            return new SetSession(in);
        }

        @Override
        public SetSession[] newArray(int size) {
            return new SetSession[size];
        }
    };

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

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(setNumber);
        parcel.writeInt(reps);
        parcel.writeDouble(weight);
    }
}
