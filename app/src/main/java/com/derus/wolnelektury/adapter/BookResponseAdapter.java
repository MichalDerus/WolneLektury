package com.derus.wolnelektury.adapter;

import android.content.Context;
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
import com.derus.wolnelektury.data.BookResponse;
import com.derus.wolnelektury.listener.OnBookItemClickListener;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.List;

/**
 * Created by Michal on 05.06.2017.
 */

public class BookResponseAdapter extends RecyclerView.Adapter<BookResponseAdapter.BookViewHolder> implements SectionTitleProvider {

    private List<BookResponse> books;
    private int rowLayout;
    private Context context;
    private OnBookItemClickListener listener;

    @Override
    public String getSectionTitle(int position) {
        return books.get(position).getTitle().substring(0, 1);
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        View view;
        LinearLayout layoutItemBook;
        ImageView imageCover;
        TextView tvBookTitle;
        TextView tvAuthorName;
        BookResponse book;

        public BookViewHolder(View v) {
            super(v);
            view = v;
            layoutItemBook = (LinearLayout) v.findViewById(R.id.layout_item_book);
            imageCover = (ImageView) v.findViewById(R.id.image_item_book_cover);
            tvBookTitle = (TextView) v.findViewById(R.id.text_item_book_title);
            tvAuthorName = (TextView) v.findViewById(R.id.text_item_book_author);
        }
    }

    public BookResponseAdapter(List<BookResponse> books, int rowLayout, Context context, OnBookItemClickListener listener) {
        this.books = books;
        this.rowLayout = rowLayout;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public BookResponseAdapter.BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookResponseAdapter.BookViewHolder holder, final int position) {
        holder.book = books.get(position);
        holder.tvBookTitle.setText(books.get(position).getTitle());
        holder.tvAuthorName.setText(books.get(position).getAuthor());

        Glide.with(context)
                .load("https://wolnelektury.pl/media/" + books.get(position).getCover())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.no_cover)
                .override(600, 800)
                .into(holder.imageCover);

        ViewCompat.setTransitionName(holder.imageCover, holder.book.getCover() + position);
        ViewCompat.setTransitionName(holder.tvBookTitle, holder.book.getTitle() + position);
        ViewCompat.setTransitionName(holder.tvAuthorName, holder.book.getAuthor() + position);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onBookItemClick(holder.book, holder.imageCover, holder.tvBookTitle, holder.tvAuthorName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
