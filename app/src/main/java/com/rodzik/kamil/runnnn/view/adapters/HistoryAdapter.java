package com.rodzik.kamil.runnnn.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.model.HistoryItemModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private List<HistoryItemModel> mHistoryItemModels = new ArrayList<>();
    private final OnItemClickListener mListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mDateView;
        public TextView mDistanceView;
        public TextView mTimeView;

        public MyViewHolder(View v) {
            super(v);
            mDateView = (TextView) v.findViewById(R.id.date);
            mDistanceView = (TextView) v.findViewById(R.id.distance);
            mTimeView = (TextView) v.findViewById(R.id.time);
        }

        public void bind(final HistoryItemModel item, final int position, final OnItemClickListener listener) {
            mDateView.setText(item.getDate());
            mDistanceView.setText(String.valueOf(item.getDistance()));
            mTimeView.setText(item.getTime());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public HistoryAdapter(OnItemClickListener listener) {
        mListener = listener;
    }

    public void clearAdapter() {
        mHistoryItemModels.clear();
    }

    public void setHistoryItemModels(List<HistoryItemModel> myDataset) {
        mHistoryItemModels = myDataset;
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(mHistoryItemModels.get(position), position, mListener);
    }

    @Override
    public int getItemCount() {
        return mHistoryItemModels.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int itemPosition);
    }
}
