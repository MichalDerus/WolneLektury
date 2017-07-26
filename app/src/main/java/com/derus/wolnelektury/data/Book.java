package com.derus.wolnelektury.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Book implements Parcelable
{

    @SerializedName("xml")
    @Expose
    private String xml;
    @SerializedName("fb2")
    @Expose
    private String fb2;
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;
    @SerializedName("mobi")
    @Expose
    private String mobi;
    @SerializedName("kinds")
    @Expose
    private List<Kind> kinds = null;
    @SerializedName("parent")
    @Expose
    private Object parent;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("media")
    @Expose
    private List<Medium> media = null;
    @SerializedName("cover")
    @Expose
    private String cover;
    @SerializedName("epochs")
    @Expose
    private List<Epoch> epochs = null;
    @SerializedName("html")
    @Expose
    private String html;
    @SerializedName("authors")
    @Expose
    private List<AuthorResponse> authors = null;
    @SerializedName("pdf")
    @Expose
    private String pdf;
    @SerializedName("txt")
    @Expose
    private String txt;
    @SerializedName("children")
    @Expose
    private List<Object> children = null;
    @SerializedName("epub")
    @Expose
    private String epub;
    @SerializedName("cover_thumb")
    @Expose
    private String coverThumb;
    public final static Parcelable.Creator<Book> CREATOR = new Creator<Book>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Book createFromParcel(Parcel in) {
            Book instance = new Book();
            instance.xml = ((String) in.readValue((String.class.getClassLoader())));
            instance.fb2 = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.genres, (com.derus.wolnelektury.data.Genre.class.getClassLoader()));
            instance.mobi = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.kinds, (com.derus.wolnelektury.data.Kind.class.getClassLoader()));
            instance.parent = in.readValue((Object.class.getClassLoader()));
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.media, (com.derus.wolnelektury.data.Medium.class.getClassLoader()));
            instance.cover = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.epochs, (com.derus.wolnelektury.data.Epoch.class.getClassLoader()));
            instance.html = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.authors, (com.derus.wolnelektury.data.Author.class.getClassLoader()));
            instance.pdf = ((String) in.readValue((String.class.getClassLoader())));
            instance.txt = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.children, (java.lang.Object.class.getClassLoader()));
            instance.epub = ((String) in.readValue((String.class.getClassLoader())));
            instance.coverThumb = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Book[] newArray(int size) {
            return (new Book[size]);
        }

    }
            ;

    /**
     * No args constructor for use in serialization
     *
     */
    public Book() {
    }

    /**
     *
     * @param genres
     * @param epochs
     * @param coverThumb
     * @param pdf
     * @param kinds
     * @param mobi
     * @param children
     * @param parent
     * @param xml
     * @param txt
     * @param url
     * @param authors
     * @param title
     * @param cover
     * @param fb2
     * @param epub
     * @param html
     * @param media
     */
    public Book(String xml, String fb2, List<Genre> genres, String mobi, List<Kind> kinds, Object parent, String title, String url, List<Medium> media, String cover, List<Epoch> epochs, String html, List<AuthorResponse> authors, String pdf, String txt, List<Object> children, String epub, String coverThumb) {
        super();
        this.xml = xml;
        this.fb2 = fb2;
        this.genres = genres;
        this.mobi = mobi;
        this.kinds = kinds;
        this.parent = parent;
        this.title = title;
        this.url = url;
        this.media = media;
        this.cover = cover;
        this.epochs = epochs;
        this.html = html;
        this.authors = authors;
        this.pdf = pdf;
        this.txt = txt;
        this.children = children;
        this.epub = epub;
        this.coverThumb = coverThumb;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getFb2() {
        return fb2;
    }

    public void setFb2(String fb2) {
        this.fb2 = fb2;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getMobi() {
        return mobi;
    }

    public void setMobi(String mobi) {
        this.mobi = mobi;
    }

    public List<Kind> getKinds() {
        return kinds;
    }

    public void setKinds(List<Kind> kinds) {
        this.kinds = kinds;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Medium> getMedia() {
        return media;
    }

    public void setMedia(List<Medium> media) {
        this.media = media;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<Epoch> getEpochs() {
        return epochs;
    }

    public void setEpochs(List<Epoch> epochs) {
        this.epochs = epochs;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<AuthorResponse> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorResponse> authors) {
        this.authors = authors;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public List<Object> getChildren() {
        return children;
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }

    public String getEpub() {
        return epub;
    }

    public void setEpub(String epub) {
        this.epub = epub;
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(xml);
        dest.writeValue(fb2);
        dest.writeList(genres);
        dest.writeValue(mobi);
        dest.writeList(kinds);
        dest.writeValue(parent);
        dest.writeValue(title);
        dest.writeValue(url);
        dest.writeList(media);
        dest.writeValue(cover);
        dest.writeList(epochs);
        dest.writeValue(html);
        dest.writeList(authors);
        dest.writeValue(pdf);
        dest.writeValue(txt);
        dest.writeList(children);
        dest.writeValue(epub);
        dest.writeValue(coverThumb);
    }

    public int describeContents() {
        return 0;
    }

}