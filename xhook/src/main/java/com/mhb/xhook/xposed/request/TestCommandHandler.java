package com.mhb.xhook.xposed.request;

import com.mhb.xhook.logging.BasicLog;
import com.mhb.xhook.logging.XHookLogManager;

public class TestCommandHandler implements CommandHandler {

    private static final BasicLog LOG = XHookLogManager.getInstance();

    @Override
    public void doAction() {
        LOG.debug("TestCommandHandler ==>> doAction");
    }

}
