package com.derus.wolnelektury.listener;

import com.derus.wolnelektury.data.Epoch;

/**
 * Created by Michal on 27.06.2017.
 */

public interface OnEpochItemClickListener {
    void onEpochClick(Epoch epoch);
    void onInfoClick(Epoch epoch);
}
