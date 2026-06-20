package com.pedrodalben.bigbangoptimizer.core;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

public class CleanupScheduler {
    public enum State {
        IDLE, SCHEDULED, WARNING, EXECUTING, COOLDOWN
    }

    private final ModuleRegistry moduleRegistry;
    private State state = State.IDLE;
    private long nextScheduledTick = 0;
    private long cooldownEndTick = 0;
    private int currentWarningIndex = 0;
    private List<Integer> warningSeconds;
    private long executionStartTick = 0;
    private long lastThresholdCheckTick = 0;
    private int consecutiveBadSamplesCount = 0;

    public CleanupScheduler(ModuleRegistry moduleRegistry) {
        this.moduleRegistry = moduleRegistry;
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent.Post event) {
        if (!OptimizerConfig.getInstance().isEnabled()) return;
        if (!OptimizerConfig.getInstance().isCleanupEnabled()) return;

        MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        long currentTick = server.getTickCount();

        switch (state) {
            case IDLE:
                checkSchedule(currentTick, server);
                break;
            case SCHEDULED:
                handleScheduled(currentTick);
                break;
            case WARNING:
                handleWarning(currentTick);
                break;
            case COOLDOWN:
                handleCooldown(currentTick);
                break;
            default:
                break;
        }
    }

    private void checkSchedule(long currentTick, MinecraftServer server) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        String mode = config.getTriggerMode();

        // 1. Time Schedule check (for "schedule" or "threshold_or_schedule" modes)
        boolean timeTrigger = false;
        if ("schedule".equalsIgnoreCase(mode) || "threshold_or_schedule".equalsIgnoreCase(mode)) {
            if (nextScheduledTick == 0) {
                nextScheduledTick = currentTick + (config.getCleanupIntervalMinutes() * 60L * 20L);
            }
            if (currentTick >= nextScheduledTick) {
                timeTrigger = true;
            }
        }

        // 2. Performance Threshold check (for "threshold" or "threshold_or_schedule" modes)
        boolean perfTrigger = false;
        if ("threshold".equalsIgnoreCase(mode) || "threshold_or_schedule".equalsIgnoreCase(mode)) {
            long checkInterval = config.getMonitoringSampleInterval() * 20L;
            if (currentTick - lastThresholdCheckTick >= checkInterval) {
                lastThresholdCheckTick = currentTick;
                if (evaluatePerformanceThreshold(server)) {
                    consecutiveBadSamplesCount++;
                    if (consecutiveBadSamplesCount >= config.getTriggerConsecutiveBadSamples()) {
                        perfTrigger = true;
                        consecutiveBadSamplesCount = 0;
                    }
                } else {
                    consecutiveBadSamplesCount = 0;
                }
            }
        }

        if (timeTrigger || perfTrigger) {
            startWarningPhase(currentTick);
        }
    }

    private boolean evaluatePerformanceThreshold(MinecraftServer server) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        com.pedrodalben.bigbangoptimizer.monitoring.TpsMonitor tpsMonitor = com.pedrodalben.bigbangoptimizer.monitoring.TpsMonitor.getInstance();

        double avgTps = tpsMonitor.getAverageTps();
        double avgMspt = tpsMonitor.getAverageMspt();

        boolean tpsDegraded = avgTps < config.getTriggerMinimumTps();
        boolean msptDegraded = avgMspt > config.getTriggerMaximumMspt();

        if (tpsDegraded || msptDegraded) {
            int itemsCount = 0;
            if (config.isItemsEnabled()) {
                com.pedrodalben.bigbangoptimizer.cleanup.ItemCleanupModule itemModule = 
                    (com.pedrodalben.bigbangoptimizer.cleanup.ItemCleanupModule) moduleRegistry.get("item_cleanup");
                if (itemModule != null) {
                    itemsCount = itemModule.countItems(server);
                }
            }

            int pokemonCount = 0;
            if (config.isPokemonEnabled()) {
                com.pedrodalben.bigbangoptimizer.cleanup.PokemonCleanupModule pokemonModule = 
                    (com.pedrodalben.bigbangoptimizer.cleanup.PokemonCleanupModule) moduleRegistry.get("pokemon_cleanup");
                if (pokemonModule != null) {
                    pokemonCount = pokemonModule.countPokemon(server);
                }
            }

            boolean itemsExcess = config.isItemsEnabled() && itemsCount > config.getItemsMinimumGlobalCount();
            boolean pokemonExcess = config.isPokemonEnabled() && pokemonCount > config.getPokemonMinimumGlobalCount();

            return itemsExcess || pokemonExcess;
        }

        return false;
    }

    private void handleScheduled(long currentTick) {
        startWarningPhase(currentTick);
    }

    private long cleanupTargetTick = 0;

    private void startWarningPhase(long currentTick) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        warningSeconds = config.getWarningsSeconds();
        if (warningSeconds.isEmpty()) {
            executeCleanup(currentTick);
            return;
        }

        // Sort warnings descending to process from longest to shortest remaining time
        warningSeconds = new java.util.ArrayList<>(warningSeconds);
        warningSeconds.sort(java.util.Collections.reverseOrder());

        int totalWarningSeconds = warningSeconds.get(0);
        cleanupTargetTick = currentTick + (totalWarningSeconds * 20L);
        currentWarningIndex = 0;
        state = State.WARNING;

        checkAndSendWarnings(currentTick);
    }

    private void handleWarning(long currentTick) {
        if (currentTick >= cleanupTargetTick) {
            executeCleanup(currentTick);
            return;
        }

        checkAndSendWarnings(currentTick);
    }

    private void checkAndSendWarnings(long currentTick) {
        while (currentWarningIndex < warningSeconds.size()) {
            int seconds = warningSeconds.get(currentWarningIndex);
            long triggerTick = cleanupTargetTick - (seconds * 20L);
            if (currentTick >= triggerTick) {
                sendWarning(seconds);
                currentWarningIndex++;
            } else {
                break;
            }
        }
    }

    private void sendWarning(int seconds) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        if (config.isWarningsChat()) {
            var msg = net.minecraft.network.chat.Component.literal(
                "§6⚠ Limpeza de entidades em §e" + seconds + " §6segundos.\n§7Itens soltos e Pokémon selvagens distantes poderão ser removidos."
            );
            for (var player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(msg);
            }
        }
    }

    private void executeCleanup(long currentTick) {
        state = State.EXECUTING;
        executionStartTick = currentTick;

        MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            state = State.IDLE;
            return;
        }

        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Executing cleanup...");
        try {
            com.pedrodalben.bigbangoptimizer.cleanup.CleanupRunner.run(moduleRegistry, server, "schedule", "scheduled_cleanup");
        } catch (Exception e) {
            BigBangOptimizer.LOGGER.error("[BigBangOptimizer] Cleanup execution failed: {}", e.getMessage());
        }

        OptimizerConfig config = OptimizerConfig.getInstance();
        long cooldownTicks = config.getCleanupCooldownMinutes() * 60L * 20L;
        cooldownEndTick = currentTick + cooldownTicks;
        state = State.COOLDOWN;

        sendCompletionMessage(server);
    }

    private void sendCompletionMessage(MinecraftServer server) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        if (config.isWarningsChat()) {
            var msg = net.minecraft.network.chat.Component.literal(
                "§a✔ Limpeza concluída.\n§7Próxima verificação em §f" + config.getCleanupIntervalMinutes() + " §7minutos."
            );
            for (var player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(msg);
            }
        }
    }

    private void handleCooldown(long currentTick) {
        if (currentTick >= cooldownEndTick) {
            state = State.IDLE;
            OptimizerConfig config = OptimizerConfig.getInstance();
            nextScheduledTick = currentTick + (config.getCleanupIntervalMinutes() * 60L * 20L);
            BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Cooldown ended. Next cleanup scheduled in {} minutes.", config.getCleanupIntervalMinutes());
        }
    }

    public void scheduleImmediate() {
        MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        nextScheduledTick = server.getTickCount();
        state = State.SCHEDULED;
    }

    public boolean cancelActive() {
        if (state == State.SCHEDULED || state == State.WARNING) {
            state = State.IDLE;
            MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                OptimizerConfig config = OptimizerConfig.getInstance();
                nextScheduledTick = server.getTickCount() + (config.getCleanupIntervalMinutes() * 60L * 20L);
            } else {
                nextScheduledTick = 0;
            }
            return true;
        }
        return false;
    }

    public State getState() { return state; }
}
