package com.rodzik.kamil.runnnn.viewmodel.summary;


import com.google.android.gms.maps.GoogleMap;

public interface SummaryViewModelContract {

    interface ViewModel {
        void onMapReady(GoogleMap googleMap);

        void destroy();
    }

    interface View {
        void hideMapFragment();
    }
}
