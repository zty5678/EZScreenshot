package com.skyrimcloud.app.easyscreenshot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

/**
 * @ClassName:
 * @Description:
 * @date 2015/11/16 14:23
 */
public class App extends Application {

    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

    }


    public static Context getContext() {
        return context;
    }

}
