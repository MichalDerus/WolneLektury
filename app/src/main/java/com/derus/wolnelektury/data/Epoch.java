package com.derus.wolnelektury.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Epoch implements Parcelable
{

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("href")
    @Expose
    private String href;
    @SerializedName("name")
    @Expose
    private String name;
    public final static Parcelable.Creator<Epoch> CREATOR = new Creator<Epoch>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Epoch createFromParcel(Parcel in) {
            Epoch instance = new Epoch();
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            instance.href = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Epoch[] newArray(int size) {
            return (new Epoch[size]);
        }

    }
            ;

    /**
     * No args constructor for use in serialization
     *
     */
    public Epoch() {
    }

    /**
     *
     * @param name
     * @param href
     * @param url
     */
    public Epoch(String url, String href, String name) {
        super();
        this.url = url;
        this.href = href;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(url);
        dest.writeValue(href);
        dest.writeValue(name);
    }

    public int describeContents() {
        return 0;
    }

}