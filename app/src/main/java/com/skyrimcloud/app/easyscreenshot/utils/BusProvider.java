package com.skyrimcloud.app.easyscreenshot.utils;


import com.squareup.otto.Bus;
public final class BusProvider {
    private static final Bus bus = new Bus();

    public static Bus get() {
        return bus;
    }
    private BusProvider() {
    }
}