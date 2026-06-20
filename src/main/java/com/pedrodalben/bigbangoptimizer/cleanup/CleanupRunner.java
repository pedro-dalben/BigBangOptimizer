package com.pedrodalben.bigbangoptimizer.cleanup;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import com.pedrodalben.bigbangoptimizer.core.CleanupExecution;
import com.pedrodalben.bigbangoptimizer.core.CleanupResult;
import com.pedrodalben.bigbangoptimizer.core.ModuleRegistry;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

public class CleanupRunner {

    public static CleanupResult run(ModuleRegistry registry, MinecraftServer server, String trigger, String reason) {
        long startTime = System.currentTimeMillis();
        OptimizerConfig config = OptimizerConfig.getInstance();
        CleanupResult result = new CleanupResult();
        result.setTrigger(trigger);
        result.setReason(reason);

        boolean dryRun = config.isCleanupDryRun();

        if (config.isItemsEnabled()) {
            ItemCleanupModule itemModule = (ItemCleanupModule) registry.get("item_cleanup");
            if (itemModule != null && itemModule.isEnabled()) {
                CleanupExecution itemExec = itemModule.execute(server, dryRun, config.getItemsMaxRemovedPerRun());
                result.addItemRemoved(itemExec.getRemovedCount());
                for (net.minecraft.world.entity.Entity entity : itemExec.getRemoved()) {
                    result.addDimensionStat(entity.level().dimension().location().toString(), true, 1);
                }
                for (Map.Entry<String, Integer> entry : itemExec.getProtectedCounts().entrySet()) {
                    result.addProtectedByReason(entry.getKey(), entry.getValue());
                }
            }
        }

        if (config.isPokemonEnabled()) {
            PokemonCleanupModule pokemonModule = (PokemonCleanupModule) registry.get("pokemon_cleanup");
            if (pokemonModule != null && pokemonModule.isEnabled()) {
                CleanupExecution pokemonExec = pokemonModule.execute(server, dryRun, config.getPokemonMaxRemovedPerRun());
                result.addPokemonRemoved(pokemonExec.getRemovedCount());
                for (net.minecraft.world.entity.Entity entity : pokemonExec.getRemoved()) {
                    result.addDimensionStat(entity.level().dimension().location().toString(), false, 1);
                }
                for (Map.Entry<String, Integer> entry : pokemonExec.getProtectedCounts().entrySet()) {
                    result.addProtectedByReason(entry.getKey(), entry.getValue());
                }
            }
        }

        if (config.isLootBallsEnabled()) {
            ItemCleanupModule lootBallModule = (ItemCleanupModule) registry.get("item_cleanup");
            if (lootBallModule != null && lootBallModule.isEnabled()) {
                result.addLootBallsRemoved(0);
            }
        }

        if (config.isTemporaryEntitiesEnabled()) {
            TemporaryEntityCleanupModule tempModule = (TemporaryEntityCleanupModule) registry.get("temporary_entity_cleanup");
            if (tempModule != null && tempModule.isEnabled()) {
                CleanupExecution tempExec = tempModule.execute(server, dryRun, config.getItemsMaxRemovedPerRun());
                result.addTemporaryEntitiesRemoved(tempExec.getRemovedCount());
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        result.setDurationMs(duration);

        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Cleanup completed in {}ms. Items: {}, Pokemon: {}, Protected: {}",
                duration, result.getItemsRemoved(), result.getPokemonRemoved(), result.getTotalProtected());

        return result;
    }
}
