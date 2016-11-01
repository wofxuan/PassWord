package com.mx.android.password.presenter;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by mxuan on 2016-07-10.
 */
public interface ActivityPresenter {
    void onCreate(Bundle savedInstanceState);

    void getIntent(Intent intent);
}
