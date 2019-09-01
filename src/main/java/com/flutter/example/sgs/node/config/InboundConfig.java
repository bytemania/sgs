package com.flutter.example.sgs.node.config;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class InboundConfig {

    public final String TOPIC;
    public final String CONSUMER_GROUP;
    public final String BOOTSTRAP_SERVERS;
    public final String AUTO_OFFSET_RESET;
    public final Config KAFKA_CONSUMER_CONFIG;

    InboundConfig() {
        final Config CONFIG = ConfigFactory.load().getConfig("akka.kafka.consumer.kafka-clients");
        TOPIC = CONFIG.getString("topic");
        CONSUMER_GROUP = CONFIG.getString("group.id");
        BOOTSTRAP_SERVERS = CONFIG.getString("bootstrap.servers");
        AUTO_OFFSET_RESET = CONFIG.getString("auto.offset.reset");
        KAFKA_CONSUMER_CONFIG = ConfigFactory.load().getConfig("akka.kafka.consumer");
    }

}
