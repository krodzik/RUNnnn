package com.rodzik.kamil.runnnn.viewmodel.main;


public interface MainViewModelContract {

    interface ViewModel {
        void connectToBluetoothDevice(String deviceAddress);
        void disconnectBluetoothDevice();
        void setHeartRateToggle(boolean enabled);
        void destroy();
    }

    interface View {
        void requestLocationAccessPermission();
        void connectedToLeDevice();
        void cannotConnectToLeDevice();
        void deviceDisconnected();
    }
}
