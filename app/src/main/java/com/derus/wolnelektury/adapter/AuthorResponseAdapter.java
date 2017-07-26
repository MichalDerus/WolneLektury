package com.derus.wolnelektury.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.derus.wolnelektury.R;
import com.derus.wolnelektury.data.AuthorResponse;
import com.derus.wolnelektury.listener.OnAuthorItemClickListener;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.List;

public class AuthorResponseAdapter extends RecyclerView.Adapter<AuthorResponseAdapter.ViewHolder> implements SectionTitleProvider {

    private List<AuthorResponse> authors;
    private OnAuthorItemClickListener authorClickListener;

    public AuthorResponseAdapter(List<AuthorResponse> authors, OnAuthorItemClickListener authorClickListener) {
        this.authors = authors;
        this.authorClickListener = authorClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_author, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.author = authors.get(position);
        holder.tvAuthorName.setText(authors.get(position).getName());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authorClickListener != null) {
                    authorClickListener.onAuthorItemClick(holder.author);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return authors.size();
    }

    @Override
    public String getSectionTitle(int position) {
        return authors.get(position).getName().substring(0, 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvAuthorName;
        public AuthorResponse author;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvAuthorName = (TextView) view.findViewById(R.id.text_item_author_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvAuthorName.getText() + "'";
        }
    }
}
