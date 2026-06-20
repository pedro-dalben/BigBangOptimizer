package com.pedrodalben.bigbangoptimizer.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.tuple.Pair;

public class OptimizerConfig {
    private static OptimizerConfig INSTANCE;
    private final ModConfigSpec spec;
    private final ModConfigSpec.ConfigValue<Boolean> enabled;
    private final ModConfigSpec.ConfigValue<String> logLevel;

    private final ModConfigSpec.ConfigValue<Integer> monitoringSampleInterval;
    private final ModConfigSpec.ConfigValue<Integer> monitoringHistorySize;

    private final ModConfigSpec.ConfigValue<Boolean> cleanupEnabled;
    private final ModConfigSpec.ConfigValue<Integer> cleanupIntervalMinutes;
    private final ModConfigSpec.ConfigValue<Integer> cleanupCooldownMinutes;
    private final ModConfigSpec.ConfigValue<Boolean> cleanupDryRun;
    private final ModConfigSpec.ConfigValue<Integer> cleanupMaxExecutionTimeMs;
    private final ModConfigSpec.ConfigValue<Integer> cleanupMaxEntitiesScannedPerTick;

    private final ModConfigSpec.ConfigValue<Boolean> warningsEnabled;
    private final ModConfigSpec.ConfigValue<? extends java.util.List<? extends Integer>> warningsSeconds;
    private final ModConfigSpec.ConfigValue<Boolean> warningsChat;
    private final ModConfigSpec.ConfigValue<Boolean> warningsTitle;
    private final ModConfigSpec.ConfigValue<Boolean> warningsActionbar;

    private final ModConfigSpec.ConfigValue<Boolean> itemsEnabled;
    private final ModConfigSpec.ConfigValue<Integer> itemsMinimumGlobalCount;
    private final ModConfigSpec.ConfigValue<Integer> itemsMinimumAgeSeconds;
    private final ModConfigSpec.ConfigValue<Integer> itemsMaxRemovedPerRun;
    private final ModConfigSpec.ConfigValue<Double> itemsProtectNearPlayersRadius;
    private final ModConfigSpec.ConfigValue<? extends java.util.List<? extends String>> itemsWhitelist;
    private final ModConfigSpec.ConfigValue<? extends java.util.List<? extends String>> itemsBlacklist;

    private final ModConfigSpec.ConfigValue<Boolean> pokemonEnabled;
    private final ModConfigSpec.ConfigValue<Integer> pokemonMinimumGlobalCount;
    private final ModConfigSpec.ConfigValue<Integer> pokemonMinimumAgeSeconds;
    private final ModConfigSpec.ConfigValue<Integer> pokemonMaxRemovedPerRun;
    private final ModConfigSpec.ConfigValue<Double> pokemonProtectNearPlayersRadius;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectShiny;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectLegendary;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectMythical;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectPlayerOwned;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectBattling;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectEvolving;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectPastureTethered;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectBattleClones;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectMounted;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectNamed;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectNpcRelated;
    private final ModConfigSpec.ConfigValue<Boolean> pokemonProtectBigBangOptimizerTag;

    private final ModConfigSpec.ConfigValue<Boolean> lootBallsEnabled;
    private final ModConfigSpec.ConfigValue<Integer> lootBallsMinimumGlobalCount;
    private final ModConfigSpec.ConfigValue<Integer> lootBallsMinimumAgeSeconds;
    private final ModConfigSpec.ConfigValue<Integer> lootBallsMaxRemovedPerRun;

    private final ModConfigSpec.ConfigValue<Boolean> temporaryEntitiesEnabled;
    private final ModConfigSpec.ConfigValue<? extends java.util.List<? extends String>> temporaryEntityIds;

    private final ModConfigSpec.ConfigValue<String> triggerMode;
    private final ModConfigSpec.ConfigValue<Double> triggerMinimumTps;
    private final ModConfigSpec.ConfigValue<Double> triggerMaximumMspt;
    private final ModConfigSpec.ConfigValue<Integer> triggerConsecutiveBadSamples;

    private static MinecraftServer currentServer;

    public OptimizerConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("General settings").push("general");
        enabled = builder.define("enabled", true);
        logLevel = builder.define("log_level", "INFO");
        builder.pop();

        builder.comment("Monitoring settings").push("monitoring");
        monitoringSampleInterval = builder.defineInRange("sample_interval_seconds", 20, 1, 300);
        monitoringHistorySize = builder.defineInRange("history_size", 180, 10, 3600);
        builder.pop();

        builder.comment("Cleanup settings").push("cleanup");
        cleanupEnabled = builder.define("enabled", true);
        cleanupIntervalMinutes = builder.defineInRange("interval_minutes", 15, 1, 1440);
        cleanupCooldownMinutes = builder.defineInRange("cooldown_minutes", 20, 0, 1440);
        cleanupDryRun = builder.define("dry_run", false);
        cleanupMaxExecutionTimeMs = builder.defineInRange("max_execution_time_ms", 150, 10, 5000);
        cleanupMaxEntitiesScannedPerTick = builder.defineInRange("max_entities_scanned_per_tick", 250, 10, 10000);
        builder.pop();

        builder.comment("Warning settings").push("warnings");
        warningsEnabled = builder.define("enabled", true);
        warningsSeconds = builder.defineListAllowEmpty("seconds", java.util.Arrays.asList(60, 30, 10, 5), () -> 5, obj -> obj instanceof Integer i && i > 0);
        warningsChat = builder.define("chat", true);
        warningsTitle = builder.define("title", false);
        warningsActionbar = builder.define("actionbar", false);
        builder.pop();

        builder.comment("Item cleanup settings").push("items");
        itemsEnabled = builder.define("enabled", true);
        itemsMinimumGlobalCount = builder.defineInRange("minimum_global_count", 500, 1, 100000);
        itemsMinimumAgeSeconds = builder.defineInRange("minimum_age_seconds", 90, 0, 3600);
        itemsMaxRemovedPerRun = builder.defineInRange("max_removed_per_run", 500, 1, 10000);
        itemsProtectNearPlayersRadius = builder.defineInRange("protect_near_players_radius", 24.0, 0.0, 256.0);
        itemsWhitelist = builder.defineListAllowEmpty("whitelist", java.util.Collections.emptyList(), () -> "", obj -> obj instanceof String);
        itemsBlacklist = builder.defineListAllowEmpty("blacklist", java.util.Collections.emptyList(), () -> "", obj -> obj instanceof String);
        builder.pop();

        builder.comment("Pokemon cleanup settings").push("pokemon");
        pokemonEnabled = builder.define("enabled", true);
        pokemonMinimumGlobalCount = builder.defineInRange("minimum_global_count", 250, 1, 100000);
        pokemonMinimumAgeSeconds = builder.defineInRange("minimum_age_seconds", 120, 0, 3600);
        pokemonMaxRemovedPerRun = builder.defineInRange("max_removed_per_run", 100, 1, 10000);
        pokemonProtectNearPlayersRadius = builder.defineInRange("protect_near_players_radius", 64.0, 0.0, 256.0);
        pokemonProtectShiny = builder.define("protect_shiny", true);
        pokemonProtectLegendary = builder.define("protect_legendary", true);
        pokemonProtectMythical = builder.define("protect_mythical", true);
        pokemonProtectPlayerOwned = builder.define("protect_player_owned", true);
        pokemonProtectBattling = builder.define("protect_battling", true);
        pokemonProtectEvolving = builder.define("protect_evolving", true);
        pokemonProtectPastureTethered = builder.define("protect_pasture_tethered", true);
        pokemonProtectBattleClones = builder.define("protect_battle_clones", true);
        pokemonProtectMounted = builder.define("protect_mounted", true);
        pokemonProtectNamed = builder.define("protect_named", true);
        pokemonProtectNpcRelated = builder.define("protect_npc_related", true);
        pokemonProtectBigBangOptimizerTag = builder.define("protect_bigbangoptimizer_tag", true);
        builder.pop();

        builder.comment("Loot ball cleanup settings").push("loot_balls");
        lootBallsEnabled = builder.define("enabled", false);
        lootBallsMinimumGlobalCount = builder.defineInRange("minimum_global_count", 100, 1, 100000);
        lootBallsMinimumAgeSeconds = builder.defineInRange("minimum_age_seconds", 300, 0, 3600);
        lootBallsMaxRemovedPerRun = builder.defineInRange("max_removed_per_run", 100, 1, 10000);
        builder.pop();

        builder.comment("Temporary entity cleanup settings").push("temporary_entities");
        temporaryEntitiesEnabled = builder.define("enabled", false);
        temporaryEntityIds = builder.defineListAllowEmpty("entity_ids",
                java.util.Arrays.asList("ars_nouveau:follow_proj"),
                () -> "ars_nouveau:follow_proj",
                obj -> obj instanceof String);
        builder.pop();

        builder.comment("Trigger settings").push("triggers");
        triggerMode = builder.define("mode", "threshold_or_schedule");
        triggerMinimumTps = builder.defineInRange("minimum_tps", 15.0, 1.0, 20.0);
        triggerMaximumMspt = builder.defineInRange("maximum_mspt", 60.0, 1.0, 1000.0);
        triggerConsecutiveBadSamples = builder.defineInRange("consecutive_bad_samples", 3, 1, 100);
        builder.pop();

        this.spec = builder.build();
    }

    public static void refreshInstance(MinecraftServer server) {
        currentServer = server;
    }

    public static OptimizerConfig getInstance() {
        return INSTANCE;
    }

    public static void setInstance(OptimizerConfig instance) {
        INSTANCE = instance;
    }

    public ModConfigSpec getSpec() {
        return spec;
    }

    public boolean isEnabled() { return enabled.get(); }
    public String getLogLevel() { return logLevel.get(); }

    public int getMonitoringSampleInterval() { return monitoringSampleInterval.get(); }
    public int getMonitoringHistorySize() { return monitoringHistorySize.get(); }

    public boolean isCleanupEnabled() { return cleanupEnabled.get(); }
    public int getCleanupIntervalMinutes() { return cleanupIntervalMinutes.get(); }
    public int getCleanupCooldownMinutes() { return cleanupCooldownMinutes.get(); }
    public boolean isCleanupDryRun() { return cleanupDryRun.get(); }
    public int getCleanupMaxExecutionTimeMs() { return cleanupMaxExecutionTimeMs.get(); }
    public int getCleanupMaxEntitiesScannedPerTick() { return cleanupMaxEntitiesScannedPerTick.get(); }

    public boolean isWarningsEnabled() { return warningsEnabled.get(); }
    public java.util.List<Integer> getWarningsSeconds() {
        return new java.util.ArrayList<>(warningsSeconds.get());
    }
    public boolean isWarningsChat() { return warningsChat.get(); }
    public boolean isWarningsTitle() { return warningsTitle.get(); }
    public boolean isWarningsActionbar() { return warningsActionbar.get(); }

    public boolean isItemsEnabled() { return itemsEnabled.get(); }
    public int getItemsMinimumGlobalCount() { return itemsMinimumGlobalCount.get(); }
    public int getItemsMinimumAgeSeconds() { return itemsMinimumAgeSeconds.get(); }
    public int getItemsMaxRemovedPerRun() { return itemsMaxRemovedPerRun.get(); }
    public double getItemsProtectNearPlayersRadius() { return itemsProtectNearPlayersRadius.get(); }
    public java.util.List<String> getItemsWhitelist() { return new java.util.ArrayList<>(itemsWhitelist.get()); }
    public java.util.List<String> getItemsBlacklist() { return new java.util.ArrayList<>(itemsBlacklist.get()); }

    public boolean isPokemonEnabled() { return pokemonEnabled.get(); }
    public int getPokemonMinimumGlobalCount() { return pokemonMinimumGlobalCount.get(); }
    public int getPokemonMinimumAgeSeconds() { return pokemonMinimumAgeSeconds.get(); }
    public int getPokemonMaxRemovedPerRun() { return pokemonMaxRemovedPerRun.get(); }
    public double getPokemonProtectNearPlayersRadius() { return pokemonProtectNearPlayersRadius.get(); }
    public boolean isPokemonProtectShiny() { return pokemonProtectShiny.get(); }
    public boolean isPokemonProtectLegendary() { return pokemonProtectLegendary.get(); }
    public boolean isPokemonProtectMythical() { return pokemonProtectMythical.get(); }
    public boolean isPokemonProtectPlayerOwned() { return pokemonProtectPlayerOwned.get(); }
    public boolean isPokemonProtectBattling() { return pokemonProtectBattling.get(); }
    public boolean isPokemonProtectEvolving() { return pokemonProtectEvolving.get(); }
    public boolean isPokemonProtectPastureTethered() { return pokemonProtectPastureTethered.get(); }
    public boolean isPokemonProtectBattleClones() { return pokemonProtectBattleClones.get(); }
    public boolean isPokemonProtectMounted() { return pokemonProtectMounted.get(); }
    public boolean isPokemonProtectNamed() { return pokemonProtectNamed.get(); }
    public boolean isPokemonProtectNpcRelated() { return pokemonProtectNpcRelated.get(); }
    public boolean isPokemonProtectBigBangOptimizerTag() { return pokemonProtectBigBangOptimizerTag.get(); }

    public boolean isLootBallsEnabled() { return lootBallsEnabled.get(); }
    public int getLootBallsMinimumGlobalCount() { return lootBallsMinimumGlobalCount.get(); }
    public int getLootBallsMinimumAgeSeconds() { return lootBallsMinimumAgeSeconds.get(); }
    public int getLootBallsMaxRemovedPerRun() { return lootBallsMaxRemovedPerRun.get(); }

    public boolean isTemporaryEntitiesEnabled() { return temporaryEntitiesEnabled.get(); }
    public java.util.List<String> getTemporaryEntityIds() { return new java.util.ArrayList<>(temporaryEntityIds.get()); }

    public String getTriggerMode() { return triggerMode.get(); }
    public double getTriggerMinimumTps() { return triggerMinimumTps.get(); }
    public double getTriggerMaximumMspt() { return triggerMaximumMspt.get(); }
    public int getTriggerConsecutiveBadSamples() { return triggerConsecutiveBadSamples.get(); }

    public static MinecraftServer getCurrentServer() { return currentServer; }
}
