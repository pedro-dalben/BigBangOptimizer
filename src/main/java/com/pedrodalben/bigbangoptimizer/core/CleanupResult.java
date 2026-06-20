package com.pedrodalben.bigbangoptimizer.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class CleanupResult {
    private int itemsRemoved = 0;
    private int pokemonRemoved = 0;
    private int lootBallsRemoved = 0;
    private int temporaryEntitiesRemoved = 0;
    private int totalProtected = 0;
    private final Map<String, Integer> protectedByReason = new LinkedHashMap<>();
    private final Map<String, DimensionStats> dimensionStats = new LinkedHashMap<>();
    private long durationMs = 0;
    private String trigger = "unknown";
    private String reason = "none";

    public void addItemRemoved(int count) { itemsRemoved += count; }
    public void addPokemonRemoved(int count) { pokemonRemoved += count; }
    public void addLootBallsRemoved(int count) { lootBallsRemoved += count; }
    public void addTemporaryEntitiesRemoved(int count) { temporaryEntitiesRemoved += count; }
    public void addTotalProtected(int count) { totalProtected += count; }
    public void addProtectedByReason(String reason, int count) { protectedByReason.merge(reason, count, Integer::sum); }

    public void addDimensionStat(String dimension, boolean isItem, int count) {
        dimensionStats.computeIfAbsent(dimension, k -> new DimensionStats()).add(isItem, count);
    }

    public int getItemsRemoved() { return itemsRemoved; }
    public int getPokemonRemoved() { return pokemonRemoved; }
    public int getLootBallsRemoved() { return lootBallsRemoved; }
    public int getTemporaryEntitiesRemoved() { return temporaryEntitiesRemoved; }
    public int getTotalProtected() { return totalProtected; }
    public Map<String, Integer> getProtectedByReason() { return protectedByReason; }
    public Map<String, DimensionStats> getDimensionStats() { return dimensionStats; }
    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
    public String getTrigger() { return trigger; }
    public void setTrigger(String trigger) { this.trigger = trigger; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getTotalRemoved() {
        return itemsRemoved + pokemonRemoved + lootBallsRemoved + temporaryEntitiesRemoved;
    }

    public static class DimensionStats {
        private int itemsRemoved = 0;
        private int pokemonRemoved = 0;

        public void add(boolean isItem, int count) {
            if (isItem) itemsRemoved += count;
            else pokemonRemoved += count;
        }

        public int getItemsRemoved() { return itemsRemoved; }
        public int getPokemonRemoved() { return pokemonRemoved; }
    }
}
