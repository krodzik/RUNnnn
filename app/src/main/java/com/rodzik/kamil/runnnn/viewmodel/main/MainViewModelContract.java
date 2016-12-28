package com.rodzik.kamil.runnnn.viewmodel.main;


public interface MainViewModelContract {

    interface ViewModel {
        void connectToBluetoothDevice();
        void destroy();
    }

    interface View {
        void requestLocationAccessPermission();
        void setHeartRateSwitchOff();
        void requestEnableBluetooth();
        void showProgressDialog();
        void dismissProgressDialog();
        void showFirstNeedToAddDeviceDialog();
        void showCannotConnectToDeviceDialog();
    }
}
