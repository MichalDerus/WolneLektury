package com.derus.wolnelektury.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpochInfo implements Parcelable
{

    @SerializedName("sort_key")
    @Expose
    public String sortKey;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("description")
    @Expose
    public String description;
    public final static Parcelable.Creator<EpochInfo> CREATOR = new Creator<EpochInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public EpochInfo createFromParcel(Parcel in) {
            EpochInfo instance = new EpochInfo();
            instance.sortKey = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public EpochInfo[] newArray(int size) {
            return (new EpochInfo[size]);
        }

    }
            ;

    /**
     * No args constructor for use in serialization
     *
     */
    public EpochInfo() {
    }

    /**
     *
     * @param description
     * @param name
     * @param sortKey
     * @param url
     */
    public EpochInfo(String sortKey, String url, String name, String description) {
        super();
        this.sortKey = sortKey;
        this.url = url;
        this.name = name;
        this.description = description;
    }

    public EpochInfo withSortKey(String sortKey) {
        this.sortKey = sortKey;
        return this;
    }

    public EpochInfo withUrl(String url) {
        this.url = url;
        return this;
    }

    public EpochInfo withName(String name) {
        this.name = name;
        return this;
    }

    public EpochInfo withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDescription() { return description; }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(sortKey);
        dest.writeValue(url);
        dest.writeValue(name);
        dest.writeValue(description);
    }

    public int describeContents() {
        return 0;
    }

}