package com.pedrodalben.bigbangoptimizer.monitoring;

import com.pedrodalben.bigbangoptimizer.integrations.cobblemon.CobblemonEntityInspector;
import com.pedrodalben.bigbangoptimizer.integrations.cobblemon.CobblemonIntegration;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public class EntityStatisticsCollector {
    private static EntityStatisticsCollector INSTANCE;

    public static EntityStatisticsCollector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EntityStatisticsCollector();
        }
        return INSTANCE;
    }

    public Map<String, Integer> countByType(MinecraftServer server) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                ResourceLocation key = EntityType.getKey(entity.getType());
                counts.merge(key.toString(), 1, Integer::sum);
            }
        }
        return counts;
    }

    public Map<String, Integer> countByDimension(MinecraftServer server) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ServerLevel level : server.getAllLevels()) {
            ResourceLocation dimKey = level.dimension().location();
            int entityCount = 0;
            for (Entity entity : level.getAllEntities()) {
                entityCount++;
            }
            counts.put(dimKey.toString(), entityCount);
        }
        return counts;
    }

    public int countItems(MinecraftServer server) {
        int count = 0;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemEntity) {
                    count++;
                }
            }
        }
        return count;
    }

    public int countPokemon(MinecraftServer server) {
        CobblemonIntegration integration = CobblemonIntegration.getInstance();
        if (integration == null || !integration.isEnabled()) return 0;

        CobblemonEntityInspector inspector = integration.getInspector();
        if (inspector == null) return 0;

        int count = 0;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (inspector.isPokemon(entity)) {
                    count++;
                }
            }
        }
        return count;
    }
}
