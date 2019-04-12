package com.toshi.aerke.Utilitis;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class NotifactionSender {

    public  class FirebaseInstanceService extends FirebaseInstanceIdService{
        @Override
        public void onTokenRefresh() {
            super.onTokenRefresh();

        }
    }
}
