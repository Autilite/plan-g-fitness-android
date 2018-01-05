package com.autilite.plan_g.program

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kelvin on Jul 30, 2017.
 */

abstract class BaseModel : Parcelable {
    val id: Long
    var name: String
    var description: String

    constructor(id: Long, name: String, description: String) {
        this.id = id
        this.name = name
        this.description = description
    }

    protected constructor(parcel: Parcel) {
        id = parcel.readLong()
        name = parcel.readString()
        description = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }
}
