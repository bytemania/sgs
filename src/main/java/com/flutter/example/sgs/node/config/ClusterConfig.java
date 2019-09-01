package com.flutter.example.sgs.node.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClusterConfig {

    public final int NUMBER_OF_SHARDS;
    public final String SHARD_FEED_REGION_NAME;

    ClusterConfig() {
        final Config CONFIG = ConfigFactory.load().getConfig("cluster-settings");
        NUMBER_OF_SHARDS = CONFIG.getInt("number-of-shards");
        SHARD_FEED_REGION_NAME = CONFIG.getString("shard-feed-region-name");
    }
}
