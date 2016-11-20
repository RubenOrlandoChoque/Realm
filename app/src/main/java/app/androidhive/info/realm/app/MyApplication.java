package app.androidhive.info.realm.app;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        
        

        super.onCreate();

        
        File folder = new File(Environment.getExternalStorageDirectory(), "MaterialCamera");
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(folder)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }
}
