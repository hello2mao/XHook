package com.mhb.xhook.xposed.request;

import com.mhb.xhook.AppConfig;


import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;


public class CommandHandlerParser {

    private static final Logger log = Logger.getLogger(AppConfig.CONF_TAG);

    private static final String ACTION_NAME_KEY = "action";
    private static final String ACTION_TEST_CMD = "test_cmd";

    public static CommandHandler parserCommand(String cmd) {
        CommandHandler handler = null;
        try {
            JSONObject jsoncmd = new JSONObject(cmd);
            String action = jsoncmd.getString(ACTION_NAME_KEY);
            log.debug("the cmd = " + action);
            if (ACTION_TEST_CMD.equals(action)) {
                handler = new TestCommandHandler();
            } else {
                log.error(action + " cmd is invalid! ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return handler;
    }

}
