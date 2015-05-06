package com.github.slofurno.moviefire.Service;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by slofurno on 5/5/2015.
 */
public enum OttoBus {
    INSTANCE;
    public static OttoBus getInstance() {
        return INSTANCE;
    }
    private Bus BUS = new Bus(ThreadEnforcer.ANY);

    public Bus getBus(){
        return BUS;
    }


}