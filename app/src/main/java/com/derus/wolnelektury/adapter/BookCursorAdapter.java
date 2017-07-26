package com.derus.wolnelektury.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.derus.wolnelektury.R;
import com.derus.wolnelektury.db.BookContract;
import com.derus.wolnelektury.listener.OnBookItemClickListener;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.io.File;

/**
 * Created by Michal on 19.07.2017.
 */

public class BookCursorAdapter extends RecyclerView.Adapter<BookCursorAdapter.MyBookViewHolder> implements SectionTitleProvider {

    private Cursor cursor;
    private Context context;
    private OnBookItemClickListener listener;

    public BookCursorAdapter(Context context, Cursor cursor, OnBookItemClickListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    @Override
    public MyBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new MyBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyBookViewHolder holder, int position) {

        if (!cursor.moveToPosition(position))
            return;

        String bookTitle = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE));
        String authorName = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR_NAME));
        String imagePath = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_IMAGE_PATH));
        final int id = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID));

        holder.tvBookTitle.setText(bookTitle);
        holder.tvAuthorName.setText(authorName);

        Glide.with(context)
                .load(new File(imagePath))
                .override(600, 800)
                .into(holder.imageCover);

        ViewCompat.setTransitionName(holder.imageCover, imagePath);
        ViewCompat.setTransitionName(holder.tvBookTitle, bookTitle + position);
        ViewCompat.setTransitionName(holder.tvAuthorName, authorName + position);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onSavedBookClick(id, holder.imageCover, holder.tvBookTitle, holder.tvAuthorName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public String getSectionTitle(int position) {
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE)).substring(0, 1);
    }

/*    public void swapCursor(Cursor newCursor) {
        // Always close the previous cursor first
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }*/

    public class MyBookViewHolder extends RecyclerView.ViewHolder {
        View view;
        LinearLayout layoutItemBook;
        ImageView imageCover;
        TextView tvBookTitle;
        TextView tvAuthorName;

        public MyBookViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            layoutItemBook = (LinearLayout) itemView.findViewById(R.id.layout_item_book);
            imageCover = (ImageView) itemView.findViewById(R.id.image_item_book_cover);
            tvBookTitle = (TextView) itemView.findViewById(R.id.text_item_book_title);
            tvAuthorName = (TextView) itemView.findViewById(R.id.text_item_book_author);
        }
    }
}
