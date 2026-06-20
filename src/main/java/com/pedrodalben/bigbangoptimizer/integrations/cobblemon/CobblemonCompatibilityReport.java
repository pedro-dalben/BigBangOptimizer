package com.pedrodalben.bigbangoptimizer.integrations.cobblemon;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;

public class CobblemonCompatibilityReport {
    public static void generate(CobblemonEntityInspector inspector) {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] === Cobblemon Compatibility Report ===");
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Available checks: {}", inspector.getAvailableChecks());
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] ==========================================");
    }
}
