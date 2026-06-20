package com.pedrodalben.bigbangoptimizer.integrations.cobblemon;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.integrations.OptimizerIntegration;
import net.neoforged.fml.loading.FMLLoader;

public class CobblemonIntegration implements OptimizerIntegration {
    public static final String ID = "cobblemon";
    private static CobblemonIntegration INSTANCE;
    private CobblemonEntityInspector inspector;
    private boolean enabled = false;

    public CobblemonIntegration() {
        INSTANCE = this;
    }

    public static CobblemonIntegration getInstance() {
        return INSTANCE;
    }

    @Override
    public String id() { return ID; }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.cobblemon.mod.common.Cobblemon");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void initialize() {
        if (!isAvailable()) {
            BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Cobblemon not found, integration disabled.");
            return;
        }

        try {
            inspector = new CobblemonEntityInspector();
            enabled = true;
            BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Cobblemon integration: enabled");
            BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Available checks: {}", inspector.getAvailableChecks());
        } catch (Exception e) {
            BigBangOptimizer.LOGGER.error("[BigBangOptimizer] Failed to initialize Cobblemon integration: {}", e.getMessage());
            enabled = false;
        }
    }

    @Override
    public void shutdown() {
        enabled = false;
        inspector = null;
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Cobblemon integration: disabled");
    }

    @Override
    public boolean isEnabled() {
        return enabled && inspector != null;
    }

    public CobblemonEntityInspector getInspector() {
        return inspector;
    }
}
