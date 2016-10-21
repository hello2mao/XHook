package com.mhb.xhook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mhb.xhook.R;
import com.mhb.xhook.main.MainActivity;


/**
 * 应用启动界面
 */
public class AppStart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("mhb-test", "AppStart -->onCreate");

        // 防止第三方跳转时出现双实例
        // 进入AppManager.class可以发现其实是一个Activity.class实例的管理类.
        // 主要负责用栈的方式管理已经存在的Activity实例.
        // 通过AppManager.getActivity(MainActivity.class)查询是否管理类里面是否已经保存MainActivity的实例.
        // 如果有则将已经存在的实例finish(防止MainActivity.class双实例)
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }

        setContentView(R.layout.app_start);
        findViewById(R.id.app_start_view).postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectTo();
            }
        }, 800);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
