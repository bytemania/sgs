package com.flutter.example.sgs.node.config;

public enum ConfigFactory {

    INSTANCE;

    private InboundConfig inboundConfig;
    private ClusterConfig clusterConfig;

    public synchronized InboundConfig getInboundConfig() {
        if (inboundConfig == null) {
            inboundConfig = new InboundConfig();
        }
        return inboundConfig;
    }

    public synchronized ClusterConfig getClusterConfig() {
        if (clusterConfig == null) {
            clusterConfig = new ClusterConfig();
        }
        return clusterConfig;
    }
}
