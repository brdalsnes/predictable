package com.brdalsnes.predictable;

import com.firebase.client.Firebase;

/**
 * Created by Bjornar on 02/08/2015.
 */
public class PredicableApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Firebase.setAndroidContext(this);


    }
}
