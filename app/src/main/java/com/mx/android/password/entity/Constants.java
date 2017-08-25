package com.mx.android.password.entity;

/**
 * Created by mxuan on 2016-07-10.
 */
public class Constants {
    public static int REALM_VERSION = 1;

    public static int VIEW_MODE = 0;
    public static int CREATE_MODE = 1;

    public static int CREATE_GESTURE = 1;
    public static int UPDATE_GESTURE = 2;

    public static String PS_Backdir = "PSBackdir";

    public final static class EVEN_BUS {
        public static int INDEX_EVENT_SUCCESS = 1;
        public static int CHANGE_THEME = 2;
        public static int CHANGE_PASS_WORD_SHOW = 3;
        public static int FILTER_EVENT_SUCCESS = 4;
    }

    public final static class SETTING {
        public static String OPEN_GESTURE = "OPEN_GESTURE";
        public static String OPEN_PASS_WORD_SHOW = "OPEN_PASS_WORD_SHOW";
        public static String AUTO_BACKUP = "AUTO_BACKUP";
    }
}
