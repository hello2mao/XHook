package com.mhb.xhookapp.base.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mhb.xhookapp.AppConfig;

import org.apache.log4j.Logger;


/**
 * BaseActivity
 */
public abstract class BaseActivity extends Activity {

    protected final String AC_NAME = this.getClass().getSimpleName();
    protected final Logger log = Logger.getLogger(AppConfig.CONF_TAG);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.info(AC_NAME + "-->onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
