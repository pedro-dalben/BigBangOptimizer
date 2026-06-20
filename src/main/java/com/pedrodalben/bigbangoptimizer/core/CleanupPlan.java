package com.pedrodalben.bigbangoptimizer.core;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CleanupPlan {
    private final List<Entity> eligible = new ArrayList<>();
    private final Map<String, Integer> protectedCounts = new LinkedHashMap<>();
    private int totalScanned = 0;
    private final String moduleType;

    public CleanupPlan(String moduleType) {
        this.moduleType = moduleType;
    }

    public void addEligible(Entity entity) {
        eligible.add(entity);
    }

    public void addProtected(String reason) {
        protectedCounts.merge(reason, 1, Integer::sum);
    }

    public void incrementScanned() {
        totalScanned++;
    }

    public List<Entity> getEligible() {
        return eligible;
    }

    public Map<String, Integer> getProtectedCounts() {
        return protectedCounts;
    }

    public int getTotalScanned() {
        return totalScanned;
    }

    public int getEligibleCount() {
        return eligible.size();
    }

    public int getProtectedTotal() {
        return protectedCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    public String getModuleType() {
        return moduleType;
    }
}
