package com.autilite.plan_g.program

import android.os.Parcel
import android.os.Parcelable

import java.util.ArrayList

/**
 * Created by kelvin on 09/12/16.
 */

class Program : BaseModel {
    var days: MutableList<Day>

    val numDays: Int
        get() = days.size

    constructor(id: Long, name: String, description: String, numDays: Int) : super(id, name, description) {
        days = ArrayList()

        for (i in 0 until numDays) {
            days.add(Day())
        }
    }

    protected constructor(parcel: Parcel) : super(parcel) {
        days = parcel.createTypedArrayList(Day.CREATOR)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeTypedList(days)
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * Helper function to get the program day
     *
     * @param day The 1-index day
     * @return
     */
    fun getDay(day: Int): Day {
        val index = day - 1
        return days[index]
    }

    class Day : Parcelable {
        var workouts: MutableList<Workout>

        constructor() {
            this.workouts = ArrayList()
        }

        protected constructor(parcel: Parcel) {
            workouts = parcel.createTypedArrayList(Workout)
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeTypedList(workouts)
        }

        override fun describeContents(): Int {
            return 0
        }

        fun addWorkout(workout: Workout?): Boolean {
            if (workout == null) {
                return false
            }
            workouts.add(workout)
            return true
        }

        fun removeWorkout(workout: Workout) {
            workouts.remove(workout)
        }

        fun removeWorkout(id: Long) {
            val it = workouts.iterator()
            while (it.hasNext()) {
                val w = it.next()
                if (w.id == id)
                    it.remove()
            }
        }

        companion object {

            val CREATOR: Parcelable.Creator<Day> = object : Parcelable.Creator<Day> {
                override fun createFromParcel(`in`: Parcel): Day {
                    return Day(`in`)
                }

                override fun newArray(size: Int): Array<Day?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }

    companion object CREATOR: Parcelable.Creator<Program> {
        override fun createFromParcel(`in`: Parcel): Program {
            return Program(`in`)
        }

        override fun newArray(size: Int): Array<Program?> {
            return arrayOfNulls(size)
        }
    }
}
