package com.pedrodalben.bigbangoptimizer.core;

public interface OptimizerModule {
    String id();
    String name();
    void initialize();
    void shutdown();
    boolean isEnabled();
}
