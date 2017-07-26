package com.derus.wolnelektury.listener;

import android.widget.ImageView;
import android.widget.TextView;

import com.derus.wolnelektury.data.BookResponse;

/**
 * Created by Michal on 19.06.2017.
 */

public interface OnBookItemClickListener {
    void onBookItemClick(BookResponse bookResponse, ImageView sharedImageView, TextView sharedTitle, TextView sharedAuthor);
    void onSavedBookClick(int id, ImageView sharedImageView, TextView sharedTitle, TextView sharedAuthor);
}