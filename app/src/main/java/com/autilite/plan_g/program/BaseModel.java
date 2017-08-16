package com.autilite.plan_g.program;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kelvin on Jul 30, 2017.
 */

public abstract class BaseModel implements Parcelable {
    protected final long id;

    protected String name;

    protected String description;

    public BaseModel(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public BaseModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
