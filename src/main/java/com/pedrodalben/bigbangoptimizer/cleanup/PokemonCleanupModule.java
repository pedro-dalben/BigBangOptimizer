package com.pedrodalben.bigbangoptimizer.cleanup;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import com.pedrodalben.bigbangoptimizer.core.CleanupExecution;
import com.pedrodalben.bigbangoptimizer.core.CleanupPlan;
import com.pedrodalben.bigbangoptimizer.core.OptimizerModule;
import com.pedrodalben.bigbangoptimizer.integrations.cobblemon.CobblemonEntityInspector;
import com.pedrodalben.bigbangoptimizer.integrations.cobblemon.CobblemonIntegration;
import com.pedrodalben.bigbangoptimizer.util.SafeEntityRemoval;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PokemonCleanupModule implements OptimizerModule {
    public static final String ID = "pokemon_cleanup";

    private CobblemonEntityInspector inspector;

    @Override
    public String id() { return ID; }

    @Override
    public String name() { return "Pokemon Cleanup Module"; }

    @Override
    public void initialize() {
        CobblemonIntegration integration = CobblemonIntegration.getInstance();
        if (integration != null && integration.isEnabled()) {
            inspector = integration.getInspector();
        }
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Pokemon Cleanup Module initialized. Inspector: {}", inspector != null ? "available" : "unavailable");
    }

    @Override
    public void shutdown() {
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Pokemon Cleanup Module shut down.");
    }

    @Override
    public boolean isEnabled() {
        OptimizerConfig config = OptimizerConfig.getInstance();
        return config != null && config.isPokemonEnabled() && inspector != null;
    }

    public int countPokemon(MinecraftServer server) {
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

    public CleanupPlan preview(MinecraftServer server) {
        if (inspector == null) return new CleanupPlan("pokemon");

        OptimizerConfig config = OptimizerConfig.getInstance();
        CleanupPlan plan = new CleanupPlan("pokemon");

        List<Entity> pokemonEntities = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (inspector.isPokemon(entity)) {
                    pokemonEntities.add(entity);
                    plan.incrementScanned();
                }
            }
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        double protectRadius = config.getPokemonProtectNearPlayersRadius();
        int minAgeTicks = config.getPokemonMinimumAgeSeconds() * 20;

        for (Entity entity : pokemonEntities) {
            String protectionReason = getProtectionReason(entity, players, protectRadius, minAgeTicks, config);
            if (protectionReason != null) {
                plan.addProtected(protectionReason);
            } else {
                plan.addEligible(entity);
            }
        }

        return plan;
    }

    public CleanupExecution execute(MinecraftServer server, boolean dryRun, int maxRemovals) {
        if (inspector == null) return new CleanupExecution("pokemon", "unavailable");

        OptimizerConfig config = OptimizerConfig.getInstance();
        CleanupExecution execution = new CleanupExecution("pokemon", dryRun ? "preview" : "execute");

        List<Entity> pokemonEntities = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (inspector.isPokemon(entity)) {
                    pokemonEntities.add(entity);
                    execution.incrementScanned();
                }
            }
        }

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        double protectRadius = config.getPokemonProtectNearPlayersRadius();
        int minAgeTicks = config.getPokemonMinimumAgeSeconds() * 20;

        List<Entity> eligibleCandidates = new ArrayList<>();

        for (Entity entity : pokemonEntities) {
            String reason = getProtectionReason(entity, players, protectRadius, minAgeTicks, config);
            if (reason != null) {
                execution.addProtected(reason);
            } else {
                eligibleCandidates.add(entity);
            }
        }

        eligibleCandidates.sort(Comparator.comparingInt(e -> -e.tickCount));

        List<Entity> toRemove = new ArrayList<>();
        for (Entity entity : eligibleCandidates) {
            if (toRemove.size() >= maxRemovals) break;
            toRemove.add(entity);
        }

        for (Entity entity : toRemove) {
            if (!dryRun) {
                SafeEntityRemoval.remove(entity);
            }
            execution.addRemoved(entity);
        }

        return execution;
    }

    private String getProtectionReason(Entity entity, List<ServerPlayer> players, double protectRadius, int minAgeTicks, OptimizerConfig config) {
        if (!inspector.isPokemon(entity)) return "not_pokemon";

        PokemonEntity pokemonEntity = (PokemonEntity) entity;

        if (config.isPokemonProtectPlayerOwned() && inspector.isPlayerOwned(pokemonEntity)) {
            return "player_owned";
        }

        if (config.isPokemonProtectBattling() && inspector.isInBattle(pokemonEntity)) {
            return "battling";
        }

        if (config.isPokemonProtectEvolving() && inspector.isEvolving(pokemonEntity)) {
            return "evolving";
        }

        if (config.isPokemonProtectShiny() && inspector.isShiny(pokemonEntity)) {
            return "shiny";
        }

        if (config.isPokemonProtectLegendary() && inspector.isLegendaryOrMythical(pokemonEntity)) {
            return "legendary_mythical";
        }

        if (config.isPokemonProtectPastureTethered() && inspector.isPastureTethered(pokemonEntity)) {
            return "pasture_tethered";
        }

        if (config.isPokemonProtectBattleClones() && inspector.isBattleClone(pokemonEntity)) {
            return "battle_clone";
        }

        if (config.isPokemonProtectMounted() && inspector.isMountedOrHasPassengers(pokemonEntity)) {
            return "mounted";
        }

        if (config.isPokemonProtectNamed() && pokemonEntity.getCustomName() != null) {
            return "named";
        }

        if (config.isPokemonProtectNpcRelated() && inspector.isNpcRelated(pokemonEntity)) {
            return "npc_related";
        }

        if (entity.getTags().contains("bigbangoptimizer:protected")) {
            return "bbo_tag";
        }

        if (config.isPokemonProtectBigBangOptimizerTag() && entity.getTags().contains("bigbangoptimizer:protected")) {
            return "bbo_tag";
        }

        if (entity.tickCount < minAgeTicks) {
            return "too_young";
        }

        if (protectRadius > 0 && inspector.isNearAnyPlayer(pokemonEntity, protectRadius)) {
            return "near_player";
        }

        return null;
    }
}
