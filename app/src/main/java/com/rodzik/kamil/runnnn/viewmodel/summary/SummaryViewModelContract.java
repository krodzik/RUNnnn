package com.rodzik.kamil.runnnn.viewmodel.summary;


public interface SummaryViewModelContract {

    interface ViewModel {
        void destroy();
    }

    interface View {
        void saveTraining();
        void exitSummary();
    }
}
