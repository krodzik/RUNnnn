package com.rodzik.kamil.runnnn.view.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.model.SummaryModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private List<SummaryModel> mSummaryModels = new ArrayList<>();
    private final OnItemClickListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
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

        public void bind(final SummaryModel item, final int position, final OnItemClickListener listener) {
            mDateView.setText(item.getDate());
            mDistanceView.setText(String.valueOf(item.getDistance()));
            mTimeView.setText(item.getTime());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryAdapter(List<SummaryModel> myDataset, OnItemClickListener listener) {
        mSummaryModels = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(mSummaryModels.get(position), position, mListener);
    }

    @Override
    public int getItemCount() {
        return mSummaryModels.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int itemPosition);
    }
}
