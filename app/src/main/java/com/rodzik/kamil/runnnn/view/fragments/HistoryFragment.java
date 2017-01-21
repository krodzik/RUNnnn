package com.rodzik.kamil.runnnn.view.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.data.StopwatchProvider;
import com.rodzik.kamil.runnnn.database.RealmInt;
import com.rodzik.kamil.runnnn.database.RunObject;
import com.rodzik.kamil.runnnn.model.HistoryItemModel;
import com.rodzik.kamil.runnnn.model.SummarySingleton;
import com.rodzik.kamil.runnnn.utils.PolyUtil;
import com.rodzik.kamil.runnnn.view.activities.SummaryActivity;
import com.rodzik.kamil.runnnn.view.adapters.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class HistoryFragment extends Fragment {
    public HistoryFragment() {
        // Required empty public constructor
    }

    private List<HistoryItemModel> mHistoryItemModels = new ArrayList<>();
    private Realm mRealm;
    private HistoryAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);

        mAdapter = new HistoryAdapter(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int itemPosition) {
                RealmResults<RunObject> results = mRealm.where(RunObject.class).findAll();
                putRunObjectInSummarySingleton(results.get(itemPosition));
                // Start Summary Activity
                Intent intent = new Intent(getActivity(), SummaryActivity.class);
                getActivity().startActivity(intent);
            }
        });
        rv.setAdapter(mAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("onResume");
        mAdapter.clearAdapter();

        // Load database
        RealmResults<RunObject> results = mRealm.where(RunObject.class).findAll();
        for (RunObject runObject : results) {
            mHistoryItemModels.add(new HistoryItemModel(runObject.getDateTime(),
                    StopwatchProvider.formatToReadableTime(runObject.getTimeInMilliseconds()),
                    String.format(Locale.US, "%.2f", runObject.getDistance() / 1000)));
        }

        mAdapter.setHistoryItemModels(mHistoryItemModels);
        mAdapter.notifyDataSetChanged();
    }

    private void putRunObjectInSummarySingleton(RunObject runObject) {
        SummarySingleton.getInstance().reset();
        SummarySingleton.getInstance().setName(runObject.getDateTime());
        SummarySingleton.getInstance().setTimeInMilliseconds(runObject.getTimeInMilliseconds());
        if (runObject.getEncodedLatLngList() != null && !runObject.getEncodedLatLngList().isEmpty()) {
            SummarySingleton.getInstance().setLatLngList(PolyUtil.decode(runObject.getEncodedLatLngList()));
            SummarySingleton.getInstance().setDistance(runObject.getDistance());
        }
        if (runObject.getHeartRateList() != null && !runObject.getHeartRateList().isEmpty()) {
            List<Integer> heartRateList = new ArrayList<>();
            for (RealmInt realmInt : runObject.getHeartRateList()) {
                heartRateList.add(realmInt.getVal());
            }
            SummarySingleton.getInstance().setHeartRate(heartRateList);
        }
        SummarySingleton.getInstance().setFromDatabase(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
