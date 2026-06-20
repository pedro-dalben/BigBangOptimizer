package com.pedrodalben.bigbangoptimizer.cleanup;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import com.pedrodalben.bigbangoptimizer.core.CleanupExecution;
import com.pedrodalben.bigbangoptimizer.core.OptimizerModule;
import com.pedrodalben.bigbangoptimizer.util.SafeEntityRemoval;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class TemporaryEntityCleanupModule implements OptimizerModule {
    public static final String ID = "temporary_entity_cleanup";

    @Override
    public String id() { return ID; }

    @Override
    public String name() { return "Temporary Entity Cleanup Module"; }

    @Override
    public void initialize() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Temporary Entity Cleanup Module initialized (disabled by default).");
    }

    @Override
    public void shutdown() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Temporary Entity Cleanup Module shut down.");
    }

    @Override
    public boolean isEnabled() {
        OptimizerConfig config = OptimizerConfig.getInstance();
        return config != null && config.isTemporaryEntitiesEnabled();
    }

    public int countTemporaryEntities(MinecraftServer server) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        Set<String> targetIds = new HashSet<>(config.getTemporaryEntityIds());
        int count = 0;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                ResourceLocation key = EntityType.getKey(entity.getType());
                if (targetIds.contains(key.toString())) {
                    count++;
                }
            }
        }
        return count;
    }

    public CleanupExecution execute(MinecraftServer server, boolean dryRun, int maxRemovals) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        CleanupExecution execution = new CleanupExecution("temporary_entities", dryRun ? "preview" : "execute");
        Set<String> targetIds = new HashSet<>(config.getTemporaryEntityIds());

        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        List<Entity> toRemove = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                ResourceLocation key = EntityType.getKey(entity.getType());
                if (targetIds.contains(key.toString())) {
                    execution.incrementScanned();

                    if (entity.getTags().contains("bigbangoptimizer:protected")) {
                        execution.addProtected("bbo_tag");
                        continue;
                    }

                    boolean nearPlayer = false;
                    for (ServerPlayer player : players) {
                        if (player.distanceToSqr(entity) < 64 * 64) {
                            nearPlayer = true;
                            break;
                        }
                    }
                    if (nearPlayer) {
                        execution.addProtected("near_player");
                        continue;
                    }

                    if (toRemove.size() < maxRemovals) {
                        toRemove.add(entity);
                    }
                }
            }
        }

        if (!dryRun) {
            for (Entity entity : toRemove) {
                SafeEntityRemoval.remove(entity);
                execution.addRemoved(entity);
            }
        } else {
            execution.getRemoved().addAll(toRemove);
        }

        return execution;
    }
}
