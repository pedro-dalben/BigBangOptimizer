package com.pedrodalben.bigbangoptimizer.integrations.spark;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.integrations.OptimizerIntegration;

public class SparkIntegration implements OptimizerIntegration {
    public static final String ID = "spark";

    @Override
    public String id() { return ID; }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("me.lucko.spark.common.SparkPlatform");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void initialize() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Spark integration: detected, passive mode");
    }

    @Override
    public void shutdown() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Spark integration: disabled");
    }

    @Override
    public boolean isEnabled() {
        return isAvailable();
    }
}
