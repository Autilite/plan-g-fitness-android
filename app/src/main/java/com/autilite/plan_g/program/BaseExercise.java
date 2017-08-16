package com.autilite.plan_g.program;

import android.os.Parcel;

/**
 * Created by Kelvin on Aug 15, 2017.
 */

public class BaseExercise extends BaseModel {
    public BaseExercise(long id, String name, String description) {
        super(id, name, description);
    }

    public BaseExercise(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseExercise> CREATOR = new Creator<BaseExercise>() {
        @Override
        public BaseExercise createFromParcel(Parcel in) {
            return new BaseExercise(in);
        }

        @Override
        public BaseExercise[] newArray(int size) {
            return new BaseExercise[size];
        }
    };
}
