/*
 * Copyright (C) 2014 Medlert, Inc.
 */
package ge.tot.weatherapp;

import com.squareup.otto.Bus;

/**
 * BusProvider
 */
public class BusProvider {
    private static BusProvider ourInstance = new BusProvider();
    private Bus bus;

    public static BusProvider getInstance() {
        return ourInstance;
    }

    public static Bus getBus() {
        return getInstance().bus;
    }

    private BusProvider() {
        this.bus = new Bus();
    }


}
