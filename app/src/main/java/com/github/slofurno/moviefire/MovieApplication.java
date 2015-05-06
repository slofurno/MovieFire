package com.github.slofurno.moviefire;

import android.app.Application;
import android.content.Intent;

import com.github.slofurno.moviefire.Service.FireService;

/**
 * Created by slofurno on 5/5/2015.
 */
public class MovieApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();

        Intent intent=new Intent(this, FireService.class);
        startService(intent);

    }

}
