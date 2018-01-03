package com.example.valleypioneer.redenvelopehelper;


/**
 * Created by valleypioneer on 2018/1/3.
 */

public class Application extends android.app.Application {
    private static Application mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Application getInstance(){
        return mInstance;
    }

}
