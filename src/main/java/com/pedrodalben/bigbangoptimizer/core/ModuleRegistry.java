package com.pedrodalben.bigbangoptimizer.core;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModuleRegistry {
    private final Map<String, OptimizerModule> modules = new LinkedHashMap<>();

    public void register(OptimizerModule module) {
        modules.put(module.id(), module);
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Registered module: {}", module.name());
    }

    public void initializeAll() {
        for (OptimizerModule module : modules.values()) {
            if (module.isEnabled()) {
                try {
                    module.initialize();
                    BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Initialized module: {}", module.name());
                } catch (Exception e) {
                    BigBangOptimizer.LOGGER.error("[BigBangOptimizer] Failed to initialize module {}: {}", module.name(), e.getMessage());
                }
            }
        }
    }

    public void shutdownAll() {
        for (OptimizerModule module : modules.values()) {
            try {
                module.shutdown();
            } catch (Exception e) {
                BigBangOptimizer.LOGGER.error("[BigBangOptimizer] Failed to shutdown module {}: {}", module.name(), e.getMessage());
            }
        }
    }

    public OptimizerModule get(String id) {
        return modules.get(id);
    }

    public List<OptimizerModule> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(modules.values()));
    }

    public List<OptimizerModule> getEnabled() {
        List<OptimizerModule> enabled = new ArrayList<>();
        for (OptimizerModule module : modules.values()) {
            if (module.isEnabled()) {
                enabled.add(module);
            }
        }
        return enabled;
    }
}
