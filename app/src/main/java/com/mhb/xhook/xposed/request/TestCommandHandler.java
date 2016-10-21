package com.mhb.xhook.xposed.request;

import com.mhb.xhook.AppConfig;

import org.apache.log4j.Logger;

public class TestCommandHandler implements CommandHandler {

    private final Logger log = Logger.getLogger(AppConfig.CONF_TAG);

    @Override
    public void doAction() {
        log.debug("TestCommandHandler ==>> doAction");
    }

}
