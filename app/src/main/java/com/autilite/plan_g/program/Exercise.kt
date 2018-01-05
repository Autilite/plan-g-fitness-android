package com.autilite.plan_g.program

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by kelvin on 09/12/16.
 */

class Exercise : BaseModel {
    var baseExerciseId: Long = 0
    var sets: Int = 0
    // TODO fix reps to use custom atomic setRepsRange
    private var reps: Int = 0
    private var repsMin: Int = 0
    private var repsMax: Int = 0
    var repsIncrement: Int = 0
    var weight: Double = 0.toDouble()
    var weightIncrement: Double = 0.toDouble()
    var restTime: Int = 0           // In seconds

    constructor(id: Long,
                name: String,
                description: String,
                baseExerciseId: Long,
                sets: Int,
                reps: Int,
                repsMin: Int,
                repsMax: Int,
                repsIncr: Int,
                weight: Double,
                weightIncrement: Double,
                restTime: Int)
            : super(id, name, description) {
        this.baseExerciseId = baseExerciseId
        this.sets = sets
        this.reps = reps
        this.repsMin = repsMin
        this.repsMax = repsMax
        this.repsIncrement = repsIncr
        this.weight = weight
        this.weightIncrement = weightIncrement
        this.restTime = restTime
    }

    protected constructor(parcel: Parcel) : super(parcel) {
        baseExerciseId = parcel.readLong()
        sets = parcel.readInt()
        reps = parcel.readInt()
        repsMin = parcel.readInt()
        repsMax = parcel.readInt()
        repsIncrement = parcel.readInt()
        weight = parcel.readDouble()
        weightIncrement = parcel.readDouble()
        restTime = parcel.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeLong(baseExerciseId)
        dest.writeInt(sets)
        dest.writeInt(reps)
        dest.writeInt(repsMin)
        dest.writeInt(repsMax)
        dest.writeInt(repsIncrement)
        dest.writeDouble(weight)
        dest.writeDouble(weightIncrement)
        dest.writeInt(restTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getReps(): Int {
        return reps
    }

    fun setReps(reps: Int) {
        setRepRange(reps, repsMin, repsMax)
    }

    fun getRepsMin(): Int {
        return repsMin
    }

    fun setRepsMin(min: Int) {
        setRepRange(reps, min, repsMax)
    }

    fun getRepsMax(): Int {
        return repsMax
    }

    fun setRepsMax(max: Int) {
        setRepRange(reps, repsMin, max)
    }

    /**
     * Atomic method for setting the rep, min, max fields while ensuring the range constraint
     *
     * @param reps The new current repetition
     * @param min The lowest repetition for the exercise
     * @param max The highest repetition for the exercise
     */
    fun setRepRange(reps: Int, min: Int, max: Int) {
        if (isValidInterval(reps, min, max)) {
            this.reps = reps
            repsMin = min
            repsMax = max
        } else {
            throw IllegalArgumentException("Range must satisfy 0 < repsMin <= reps <= repsMax." +
                    " Found reps=" + reps + ", [" + min + "," + max + "]")
        }
    }

    private fun isValidInterval(reps: Int, min: Int, max: Int): Boolean {
        return min > 0 && min <= max && reps >= min && reps <= max
    }

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return arrayOfNulls(size)
        }
    }

}
