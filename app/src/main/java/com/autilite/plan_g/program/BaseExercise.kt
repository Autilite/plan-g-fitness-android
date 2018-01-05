package com.autilite.plan_g.program

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kelvin on Aug 15, 2017.
 */

class BaseExercise : BaseModel {
    constructor(id: Long, name: String, description: String) : super(id, name, description)

    protected constructor(parcel: Parcel) : super(parcel)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseExercise> {
        override fun createFromParcel(parcel: Parcel): BaseExercise {
            return BaseExercise(parcel)
        }

        override fun newArray(size: Int): Array<BaseExercise?> {
            return arrayOfNulls(size)
        }
    }
}
