package com.mercy.virtualboard;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class VirtualNoticeBoardApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("virtualboard.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
