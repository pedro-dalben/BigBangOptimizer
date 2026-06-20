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
                checkSchedule(currentTick);
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

    private void checkSchedule(long currentTick) {
        if (nextScheduledTick > 0 && currentTick >= nextScheduledTick) {
            startWarningPhase(currentTick);
        }
    }

    private void handleScheduled(long currentTick) {
        startWarningPhase(currentTick);
    }

    private void startWarningPhase(long currentTick) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        warningSeconds = config.getWarningsSeconds();
        if (warningSeconds.isEmpty()) {
            executeCleanup(currentTick);
            return;
        }
        currentWarningIndex = 0;
        state = State.WARNING;
        sendWarning(warningSeconds.get(currentWarningIndex));
    }

    private void handleWarning(long currentTick) {
        if (currentWarningIndex >= warningSeconds.size()) {
            executeCleanup(currentTick);
            return;
        }

        int secondsRemaining = warningSeconds.get(currentWarningIndex);
        long ticksRemaining = secondsRemaining * 20L;
        long warningStartTick = currentTick - getElapsedTicksInWarning(currentTick);

        if (currentTick >= warningStartTick + ticksRemaining) {
            currentWarningIndex++;
            if (currentWarningIndex < warningSeconds.size()) {
                sendWarning(warningSeconds.get(currentWarningIndex));
            } else {
                executeCleanup(currentTick);
            }
        }
    }

    private long getElapsedTicksInWarning(long currentTick) {
        long total = 0;
        for (int i = 0; i < currentWarningIndex; i++) {
            total += warningSeconds.get(i) * 20L;
        }
        return total;
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

    public State getState() { return state; }
}
