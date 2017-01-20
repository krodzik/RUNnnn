package com.rodzik.kamil.runnnn.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.model.SummaryModel;
import com.rodzik.kamil.runnnn.view.adapters.HistoryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {
    public HistoryFragment() {
        // Required empty public constructor
    }

    private List<SummaryModel> mSummaryModels = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.ENGLISH);
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        mSummaryModels.add(new SummaryModel(dateFormat.format(date), "15:45", 4.25));
        mSummaryModels.add(new SummaryModel(dateFormat.format(date), "18:45", 1.25));
        mSummaryModels.add(new SummaryModel(dateFormat.format(date), "09:45", 6.21));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        HistoryAdapter adapter = new HistoryAdapter(mSummaryModels, new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int itemPosition) {
                Toast.makeText(getContext(), "Item Clicked " + itemPosition, Toast.LENGTH_LONG).show();
            }
        });
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }
}
