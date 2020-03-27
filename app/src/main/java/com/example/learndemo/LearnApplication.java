package com.example.learndemo;

import android.app.Application;
import com.example.router.Router;

public class LearnApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.init(this);
    }
}
