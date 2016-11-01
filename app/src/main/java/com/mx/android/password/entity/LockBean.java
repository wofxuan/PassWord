package com.mx.android.password.entity;

/**
 * Created by mxuan on 2016-07-10.
 */
public class LockBean {
    private String warn;
    private int color;

    public LockBean(String name ) {
        this.warn = name;
    }

    public String getWarn() {
        return warn;
    }

    public void setWarn(String warn) {
        this.warn = warn;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }
}
