package com.pedrodalben.bigbangoptimizer.integrations.ftbchunks;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.integrations.OptimizerIntegration;

public class FtbChunksIntegration implements OptimizerIntegration {
    public static final String ID = "ftbchunks";

    @Override
    public String id() { return ID; }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("dev.ftb.mods.ftbchunks.FTBChunks");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void initialize() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] FTB Chunks integration: detected, no active policies");
    }

    @Override
    public void shutdown() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] FTB Chunks integration: disabled");
    }

    @Override
    public boolean isEnabled() {
        return isAvailable();
    }
}
