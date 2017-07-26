package com.derus.wolnelektury.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class BookResponse implements Parcelable{

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("cover")
    @Expose
    private String cover;
    @SerializedName("epoch")
    @Expose
    private String epoch;
    @SerializedName("href")
    @Expose
    private String href;
    @SerializedName("genre")
    @Expose
    private String genre;
    @SerializedName("cover_thumb")
    @Expose
    private String coverThumb;

    /**
     * No args constructor for use in serialization
     *
     */
    public BookResponse() {
    }

    /**
     *
     * @param genre
     * @param author
     * @param cover
     * @param title
     * @param coverThumb
     * @param epoch
     * @param href
     * @param url
     * @param kind
     */
    public BookResponse(String kind, String author, String url, String title, String cover, String epoch, String href, String genre, String coverThumb) {
        super();
        this.kind = kind;
        this.author = author;
        this.url = url;
        this.title = title;
        this.cover = cover;
        this.epoch = epoch;
        this.href = href;
        this.genre = genre;
        this.coverThumb = coverThumb;
    }

    protected BookResponse(Parcel in) {
        kind = in.readString();
        author = in.readString();
        url = in.readString();
        title = in.readString();
        cover = in.readString();
        epoch = in.readString();
        href = in.readString();
        genre = in.readString();
        coverThumb = in.readString();
    }

    public static final Creator<BookResponse> CREATOR = new Creator<BookResponse>() {
        @Override
        public BookResponse createFromParcel(Parcel in) {
            return new BookResponse(in);
        }

        @Override
        public BookResponse[] newArray(int size) {
            return new BookResponse[size];
        }
    };

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getEpoch() {
        return epoch;
    }

    public void setEpoch(String epoch) {
        this.epoch = epoch;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCoverThumb() {
        return coverThumb;
    }

    public void setCoverThumb(String coverThumb) {
        this.coverThumb = coverThumb;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(kind);
        parcel.writeString(author);
        parcel.writeString(url);
        parcel.writeString(title);
        parcel.writeString(cover);
        parcel.writeString(epoch);
        parcel.writeString(href);
        parcel.writeString(genre);
        parcel.writeString(coverThumb);
    }
}