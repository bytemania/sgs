package com.flutter.example.sgs.node.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RetryConfig {

    public final int TRIES;
    public final int TIMEOUT_SECONDS;
    public final int INTERVAL_SECONDS;

    RetryConfig() {
        final Config CONFIG = ConfigFactory.load().getConfig("retry");
        TRIES = CONFIG.getInt("tries");
        TIMEOUT_SECONDS = CONFIG.getInt("timeoutSeconds");
        INTERVAL_SECONDS = CONFIG.getInt("intervalSeconds");
    }



}
