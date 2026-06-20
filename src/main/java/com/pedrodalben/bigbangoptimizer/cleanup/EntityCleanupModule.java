package com.pedrodalben.bigbangoptimizer.cleanup;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import com.pedrodalben.bigbangoptimizer.core.OptimizerModule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

public class EntityCleanupModule implements OptimizerModule {
    public static final String ID = "entity_cleanup";

    @Override
    public String id() { return ID; }

    @Override
    public String name() { return "Entity Cleanup Module"; }

    @Override
    public void initialize() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Entity Cleanup Module initialized.");
    }

    @Override
    public void shutdown() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Entity Cleanup Module shut down.");
    }

    @Override
    public boolean isEnabled() {
        OptimizerConfig config = OptimizerConfig.getInstance();
        return config != null && config.isCleanupEnabled();
    }

    public int countAllEntities(MinecraftServer server) {
        int count = 0;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                count++;
            }
        }
        return count;
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

    public List<Entity> getLoadedEntities(MinecraftServer server) {
        List<Entity> entities = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                entities.add(entity);
            }
        }
        return entities;
    }
}
