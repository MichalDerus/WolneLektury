package com.derus.wolnelektury.rest;

import com.derus.wolnelektury.data.Author;
import com.derus.wolnelektury.data.AuthorResponse;
import com.derus.wolnelektury.data.Book;
import com.derus.wolnelektury.data.BookResponse;
import com.derus.wolnelektury.data.Epoch;
import com.derus.wolnelektury.data.EpochInfo;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by Michal on 05.06.2017.
 */

public interface ApiInterface {

    @GET("daisy")
    Call<ArrayList<BookResponse>> getAllBook();

    @GET("authors")
    Call<ArrayList<AuthorResponse>> getListOfAuthors();

    @GET("{url}books/")
    Call<ArrayList<BookResponse>> getAuthorBooks(@Path(value = "url", encoded = true)String url);

    @GET("epochs")
    Call<ArrayList<Epoch>> getListOfEpochs();

    @GET("{url}books/")
    Call<ArrayList<BookResponse>> getEpochBooks(@Path(value = "url", encoded = true)String url);

    @GET()
    Call<Book> getBook(@Url String url);

    @GET()
    Call<Author> getAuthor(@Url String url);

    @GET()
    Call<EpochInfo> getEpochInfo(@Url String url);

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

}
