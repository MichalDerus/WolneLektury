package com.derus.wolnelektury.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Author implements Parcelable
{

    @SerializedName("sort_key")
    @Expose
    private String sortKey;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    public final static Parcelable.Creator<Author> CREATOR = new Creator<Author>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Author createFromParcel(Parcel in) {
            Author instance = new Author();
            instance.sortKey = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Author[] newArray(int size) {
            return (new Author[size]);
        }

    }
            ;

    /**
     * No args constructor for use in serialization
     *
     */
    public Author() {
    }

    /**
     *
     * @param description
     * @param name
     * @param sortKey
     * @param url
     */
    public Author(String sortKey, String url, String name, String description) {
        super();
        this.sortKey = sortKey;
        this.url = url;
        this.name = name;
        this.description = description;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public Author withSortKey(String sortKey) {
        this.sortKey = sortKey;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Author withUrl(String url) {
        this.url = url;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Author withName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Author withDescription(String description) {
        this.description = description;
        return this;
    }

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