package com.derus.wolnelektury.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.derus.wolnelektury.R;
import com.derus.wolnelektury.data.Epoch;
import com.derus.wolnelektury.listener.OnEpochItemClickListener;

import java.util.List;


public class EpochAdapter extends RecyclerView.Adapter<EpochAdapter.ViewHolder> {

    private OnEpochItemClickListener epochClickListener;
    private List<Epoch> epochs;

    public EpochAdapter(List<Epoch> epochs, OnEpochItemClickListener listener) {
        this.epochs = epochs;
        this.epochClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_epoch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.epoch = epochs.get(position);
        holder.tvEpochName.setText(epochs.get(position).getName());
        holder.imageEpochInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != epochClickListener) {
                    epochClickListener.onInfoClick(holder.epoch);
                }
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != epochClickListener) {
                    epochClickListener.onEpochClick(holder.epoch);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return epochs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvEpochName;
        public final ImageView imageEpochInfo;
        public Epoch epoch;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvEpochName = (TextView) view.findViewById(R.id.text_item_epoch_name);
            imageEpochInfo = (ImageView) view.findViewById(R.id.image_item_epoch_info);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvEpochName.getText() + "'";
        }
    }
}
