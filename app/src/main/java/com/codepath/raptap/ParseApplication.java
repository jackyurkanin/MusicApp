package com.codepath.raptap;


import android.app.Application;

import com.codepath.raptap.models.Sound;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Sound.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("eYvnKNUosm9eMHGzaJUkivR4P7iiWBElCrnApZUF")
                .clientKey("rvSyVz86BVL8zDOCsx9YKuYSGPwcdXJYwTXA8p3V")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
