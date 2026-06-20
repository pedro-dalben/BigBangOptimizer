package com.pedrodalben.bigbangoptimizer.integrations;

public interface OptimizerIntegration {
    String id();
    boolean isAvailable();
    void initialize();
    void shutdown();
    boolean isEnabled();
}
