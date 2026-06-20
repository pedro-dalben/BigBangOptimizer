package com.pedrodalben.bigbangoptimizer.monitoring;

import com.pedrodalben.bigbangoptimizer.integrations.cobblemon.CobblemonIntegration;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.LinkedHashMap;
import java.util.Map;

public class PerformanceSnapshot {
    private final double tps;
    private final double mspt;
    private final long heapUsedMB;
    private final long heapMaxMB;
    private final int onlinePlayers;
    private final int totalEntities;
    private final int items;
    private final int pokemon;
    private final Map<String, Integer> entitiesByDimension;
    private final Map<String, Integer> loadedChunksByDimension;

    public PerformanceSnapshot(MinecraftServer server) {
        TpsMonitor tpsMonitor = TpsMonitor.getInstance();
        MemoryMonitor memoryMonitor = MemoryMonitor.getInstance();
        EntityStatisticsCollector entityStats = EntityStatisticsCollector.getInstance();
        DimensionStatistics dimensionStats = DimensionStatistics.getInstance();

        this.tps = tpsMonitor.getAverageTps();
        this.mspt = tpsMonitor.getAverageMspt();
        this.heapUsedMB = memoryMonitor.getUsedHeapMB();
        this.heapMaxMB = memoryMonitor.getMaxHeapMB();
        this.onlinePlayers = dimensionStats.getOnlinePlayerCount(server);
        this.totalEntities = entityStats.countByType(server).values().stream().mapToInt(Integer::intValue).sum();
        this.items = entityStats.countItems(server);
        this.pokemon = entityStats.countPokemon(server);
        this.entitiesByDimension = entityStats.countByDimension(server);
        this.loadedChunksByDimension = dimensionStats.getLoadedChunks(server);
    }

    public double getTps() { return tps; }
    public double getMspt() { return mspt; }
    public long getHeapUsedMB() { return heapUsedMB; }
    public long getHeapMaxMB() { return heapMaxMB; }
    public int getOnlinePlayers() { return onlinePlayers; }
    public int getTotalEntities() { return totalEntities; }
    public int getItems() { return items; }
    public int getPokemon() { return pokemon; }
    public Map<String, Integer> getEntitiesByDimension() { return entitiesByDimension; }
    public Map<String, Integer> getLoadedChunksByDimension() { return loadedChunksByDimension; }
}
