package com.mhb.xhook.xposed.launch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mhb.xhook.AppConfig;
import com.mhb.xhook.xposed.request.CommandHandler;
import com.mhb.xhook.xposed.request.CommandHandlerParser;

import org.apache.log4j.Logger;

public class CommandBroadcastReceiver extends BroadcastReceiver {

    private final Logger log = Logger.getLogger(AppConfig.CONF_TAG);
    public static String INTENT_ACTION = "com.xhook.invoke";
    public static String TARGET_KEY = "target";
    public static String COMMAND_NAME_KEY = "cmd";

    @Override
    public void onReceive(final Context arg0, Intent arg1) {
        if (INTENT_ACTION.equals(arg1.getAction())) {
            try {
                int pid = arg1.getIntExtra(TARGET_KEY, 0);
                if (pid == android.os.Process.myPid()) {
                    String cmd = arg1.getStringExtra(COMMAND_NAME_KEY);
                    final CommandHandler handler = CommandHandlerParser.parserCommand(cmd);
                    if (handler != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.doAction();
                            }
                        }).start();
                    } else {
                        log.error("the cmd is invalid");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
