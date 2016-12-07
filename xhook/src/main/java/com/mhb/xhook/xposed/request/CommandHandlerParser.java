package com.mhb.xhook.xposed.request;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XhookLogManager;

import org.json.JSONException;
import org.json.JSONObject;


public class CommandHandlerParser {

    private static final BasicLog LOG = XhookLogManager.getInstance();

    private static final String ACTION_NAME_KEY = "action";
    private static final String ACTION_TEST_CMD = "test_cmd";

    public static CommandHandler parserCommand(String cmd) {
        CommandHandler handler = null;
        try {
            JSONObject jsoncmd = new JSONObject(cmd);
            String action = jsoncmd.getString(ACTION_NAME_KEY);
            LOG.debug("the cmd = " + action);
            if (ACTION_TEST_CMD.equals(action)) {
                handler = new TestCommandHandler();
            } else {
                LOG.error(action + " cmd is invalid! ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return handler;
    }

}
