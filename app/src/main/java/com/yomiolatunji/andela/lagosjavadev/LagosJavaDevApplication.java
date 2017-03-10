package com.yomiolatunji.andela.lagosjavadev;

import android.app.Application;
import android.content.Context;

/**
 * Created by Oluwayomi on 3/8/2017.
 */

public class LagosJavaDevApplication extends Application {
    private static LagosJavaDevApplication sInstance;

    public static LagosJavaDevApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
