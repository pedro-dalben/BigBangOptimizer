package com.pedrodalben.bigbangoptimizer.monitoring;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public class DimensionStatistics {
    private static DimensionStatistics INSTANCE;

    public static DimensionStatistics getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DimensionStatistics();
        }
        return INSTANCE;
    }

    public Map<String, Integer> getLoadedChunks(MinecraftServer server) {
        Map<String, Integer> chunks = new LinkedHashMap<>();
        for (ServerLevel level : server.getAllLevels()) {
            ResourceLocation dimKey = level.dimension().location();
            chunks.put(dimKey.toString(), level.getChunkSource().getLoadedChunksCount());
        }
        return chunks;
    }

    public int getOnlinePlayerCount(MinecraftServer server) {
        return server.getPlayerList().getPlayerCount();
    }
}
