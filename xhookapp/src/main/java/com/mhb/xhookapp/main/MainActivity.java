package com.mhb.xhookapp.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mhb.xhook.networklib.NetworkLibInit;
import com.mhb.xhookapp.R;
import com.mhb.xhookapp.base.activities.BaseActivity;
import com.mhb.xhookapp.util.NetUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("http://www.cmyip.com/"));
                String packageName = getApplicationContext().getPackageName();
                log.debug("libPath=" + "/data/data/" + packageName + "/lib/libxhooknative.so");
                new NetworkLibInit().initNativeHook("/data/data/" + packageName + "/lib/libxhooknative.so", android.os.Build.VERSION.RELEASE);

            }
        }).start();

//        String packageName = getApplicationContext().getPackageName();
//        log.debug("libPath=" + "/data/data/" + packageName + "/lib/libxhooknative.so");
//        new NetworkLibInit().initNativeHook("/data/data/" + packageName + "/lib/libxhooknative.so", android.os.Build.VERSION.RELEASE);

    }

    @Override
    public void onClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Socket socket = new Socket();
//                try {
//                    socket.connect(new InetSocketAddress("127.0.0.1", 8080), 5000);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                String ip = NetUtil.GetNetIp("http://www.cmyip.com/");
                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("http://www.cmyip.com/"));
                try {
                    Log.d("test", MainActivity.class.getMethod("onClick", View.class).toString());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

//                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("https://www.baidu.com/s?wd=ip"));
//                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("http://google.com"));
//                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("http://www.mmmmmyip.com/"));
            }
        }).start();
    }
}
