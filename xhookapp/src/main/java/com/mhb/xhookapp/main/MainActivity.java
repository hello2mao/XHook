package com.mhb.xhookapp.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mhb.xhook.Xhook;
import com.mhb.xhookapp.R;
import com.mhb.xhookapp.util.NetUtil;

public class MainActivity extends android.support.v4.app.FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Xhook.withToken("mhb")
                .withLoggingEnabled(true)
                .withLogLevel(5)
                .withJavaHook(true)
                .withNativeHook(true)
                .start(getApplicationContext());

        
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
//                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("http://www.cmyip.com/"));
                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("http://blog.csdn.net/hello2mao"));
                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("https://www.baidu.com/s?wd=Huawei%20Google%20Nexus%206P&rsv_spt=1&rsv_iqid=0x86b34e2b00014f32&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=0&oq=Moto%20Nexus%206&rsv_t=dfe6gRy%2B1RZ%2BXSz%2Bp2VOyWpVI%2FYziTC%2FPpPFPJR%2F85rIbEeXV2IXUkfjRfetzCoAvlJt&inputT=2466&rsv_pq=ed9278320000f876&rsv_sug3=16&rsv_sug1=9&rsv_sug7=100&rsv_sug4=2623"));
//                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("https://119.75.222.168/echo.fcgi"));
//                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("https://www.baidu.com/s?wd=ip"));
//                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("http://google.com"));
//                Log.d("test", "ip=" + NetUtil.GetNetIpWithOkHttp3("http://www.mmmmmyip.com/"));
            }
        }).start();
    }
}
