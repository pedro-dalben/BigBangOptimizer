package com.pedrodalben.bigbangoptimizer.cleanup;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import com.pedrodalben.bigbangoptimizer.core.CleanupExecution;
import com.pedrodalben.bigbangoptimizer.core.CleanupPlan;
import com.pedrodalben.bigbangoptimizer.core.OptimizerModule;
import com.pedrodalben.bigbangoptimizer.util.SafeEntityRemoval;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.*;

public class ItemCleanupModule implements OptimizerModule {
    public static final String ID = "item_cleanup";

    @Override
    public String id() { return ID; }

    @Override
    public String name() { return "Item Cleanup Module"; }

    @Override
    public void initialize() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Item Cleanup Module initialized.");
    }

    @Override
    public void shutdown() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Item Cleanup Module shut down.");
    }

    @Override
    public boolean isEnabled() {
        OptimizerConfig config = OptimizerConfig.getInstance();
        return config != null && config.isItemsEnabled();
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

    public CleanupPlan preview(MinecraftServer server) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        CleanupPlan plan = new CleanupPlan("items");

        List<ItemEntity> allItems = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemEntity itemEntity) {
                    allItems.add(itemEntity);
                    plan.incrementScanned();
                }
            }
        }

        double protectRadius = config.getItemsProtectNearPlayersRadius();
        List<String> whitelist = config.getItemsWhitelist();
        List<String> blacklist = config.getItemsBlacklist();
        int minAgeTicks = config.getItemsMinimumAgeSeconds() * 20;

        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        for (ItemEntity item : allItems) {
            if (item.tickCount < minAgeTicks) {
                plan.addProtected("too_young");
                continue;
            }

            if (protectRadius > 0 && isNearPlayer(item, players, protectRadius)) {
                plan.addProtected("near_player");
                continue;
            }

            if (hasBboTag(item)) {
                plan.addProtected("bbo_tag");
                continue;
            }

            String itemId = BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString();
            if (!blacklist.isEmpty() && blacklist.contains(itemId)) {
                plan.addProtected("blacklisted");
                continue;
            }
            if (!whitelist.isEmpty() && !whitelist.contains(itemId)) {
                plan.addProtected("not_whitelisted");
                continue;
            }

            plan.addEligible(item);
        }

        return plan;
    }

    public CleanupExecution execute(MinecraftServer server, boolean dryRun, int maxRemovals) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        CleanupExecution execution = new CleanupExecution("items", dryRun ? "preview" : "execute");

        List<ItemEntity> allItems = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemEntity itemEntity) {
                    allItems.add(itemEntity);
                    execution.incrementScanned();
                }
            }
        }

        double protectRadius = config.getItemsProtectNearPlayersRadius();
        List<String> whitelist = config.getItemsWhitelist();
        List<String> blacklist = config.getItemsBlacklist();
        int minAgeTicks = config.getItemsMinimumAgeSeconds() * 20;

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        List<ItemEntity> toRemove = new ArrayList<>();

        for (ItemEntity item : allItems) {
            if (toRemove.size() >= maxRemovals) break;

            if (item.tickCount < minAgeTicks) {
                execution.addProtected("too_young");
                continue;
            }

            if (protectRadius > 0 && isNearPlayer(item, players, protectRadius)) {
                execution.addProtected("near_player");
                continue;
            }

            if (hasBboTag(item)) {
                execution.addProtected("bbo_tag");
                continue;
            }

            String itemId = BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString();
            if (!blacklist.isEmpty() && blacklist.contains(itemId)) {
                execution.addProtected("blacklisted");
                continue;
            }
            if (!whitelist.isEmpty() && !whitelist.contains(itemId)) {
                execution.addProtected("not_whitelisted");
                continue;
            }

            toRemove.add(item);
        }

        for (ItemEntity item : toRemove) {
            if (!dryRun) {
                SafeEntityRemoval.remove(item);
            }
            execution.addRemoved(item);
        }

        return execution;
    }

    public Map<String, Integer> countByItem(List<ItemEntity> items) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ItemEntity item : items) {
            String id = BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString();
            counts.merge(id, item.getItem().getCount(), Integer::sum);
        }
        return counts;
    }

    private boolean isNearPlayer(ItemEntity item, List<ServerPlayer> players, double radius) {
        for (ServerPlayer player : players) {
            if (player.distanceToSqr(item) <= radius * radius) {
                return true;
            }
        }
        return false;
    }

    private boolean hasBboTag(Entity entity) {
        return entity.getTags().contains("bigbangoptimizer:protected");
    }
}
