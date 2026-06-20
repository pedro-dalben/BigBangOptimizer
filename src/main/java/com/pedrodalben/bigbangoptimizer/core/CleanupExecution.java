package com.pedrodalben.bigbangoptimizer.core;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CleanupExecution {
    private final List<Entity> removed = new ArrayList<>();
    private final Map<String, Integer> protectedCounts = new LinkedHashMap<>();
    private int totalScanned = 0;
    private long durationMs = 0;
    private final String moduleType;
    private final String trigger;

    public CleanupExecution(String moduleType, String trigger) {
        this.moduleType = moduleType;
        this.trigger = trigger;
    }

    public void addRemoved(Entity entity) {
        removed.add(entity);
    }

    public void addProtected(String reason) {
        protectedCounts.merge(reason, 1, Integer::sum);
    }

    public void incrementScanned() {
        totalScanned++;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public List<Entity> getRemoved() {
        return Collections.unmodifiableList(removed);
    }

    public Map<String, Integer> getProtectedCounts() {
        return Collections.unmodifiableMap(protectedCounts);
    }

    public int getTotalScanned() { return totalScanned; }
    public int getRemovedCount() { return removed.size(); }
    public int getProtectedTotal() { return protectedCounts.values().stream().mapToInt(Integer::intValue).sum(); }
    public long getDurationMs() { return durationMs; }
    public String getModuleType() { return moduleType; }
    public String getTrigger() { return trigger; }
}
