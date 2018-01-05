package com.autilite.plan_g.program

import android.os.Parcel
import android.os.Parcelable

import java.util.ArrayList

/**
 * Created by kelvin on 09/12/16.
 */

class Workout : BaseModel {
    var exercises: MutableList<Exercise>

    constructor(id: Long, name: String, description: String) : super(id, name, description) {
        exercises = ArrayList(4)
    }

    protected constructor(parcel: Parcel) : super(parcel) {
        exercises = parcel.createTypedArrayList(Exercise.CREATOR)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeTypedList(exercises)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun addExercise(exercise: Exercise) {
        exercises.add(exercise)
    }

    fun removeExercise(exercise: Exercise) {
        exercises.remove(exercise)
    }

    fun removeExercise(exercise: String) {
        val it = exercises!!.iterator()
        while (it.hasNext()) {
            val e = it.next()
            if (e.name == exercise) {
                it.remove()
            }
        }
    }

    companion object CREATOR : Parcelable.Creator<Workout> {
        override fun createFromParcel(parcel: Parcel): Workout {
            return Workout(parcel)
        }

        override fun newArray(size: Int): Array<Workout?> {
            return arrayOfNulls(size)
        }
    }

}
