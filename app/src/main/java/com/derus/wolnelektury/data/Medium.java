package com.derus.wolnelektury.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Medium implements Parcelable
{

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("director")
    @Expose
    private String director;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("artist")
    @Expose
    private String artist;
    public final static Parcelable.Creator<Medium> CREATOR = new Creator<Medium>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Medium createFromParcel(Parcel in) {
            Medium instance = new Medium();
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            instance.director = ((String) in.readValue((String.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.artist = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Medium[] newArray(int size) {
            return (new Medium[size]);
        }

    }
            ;

    /**
     * No args constructor for use in serialization
     *
     */
    public Medium() {
    }

    /**
     *
     * @param name
     * @param artist
     * @param type
     * @param director
     * @param url
     */
    public Medium(String url, String director, String type, String name, String artist) {
        super();
        this.url = url;
        this.director = director;
        this.type = type;
        this.name = name;
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(url);
        dest.writeValue(director);
        dest.writeValue(type);
        dest.writeValue(name);
        dest.writeValue(artist);
    }

    public int describeContents() {
        return 0;
    }

}