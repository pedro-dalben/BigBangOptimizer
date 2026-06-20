package com.pedrodalben.bigbangoptimizer.commands;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import com.pedrodalben.bigbangoptimizer.core.CleanupScheduler;
import com.pedrodalben.bigbangoptimizer.monitoring.MemoryMonitor;
import com.pedrodalben.bigbangoptimizer.monitoring.TpsMonitor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class StatusCommand {

    public static int execute(CommandSourceStack source) {
        OptimizerConfig config = OptimizerConfig.getInstance();
        CleanupScheduler scheduler = BigBangOptimizer.getInstance().getScheduler();
        TpsMonitor tpsMonitor = TpsMonitor.getInstance();
        MemoryMonitor memoryMonitor = MemoryMonitor.getInstance();

        source.sendSuccess(() -> Component.literal("§6§l=== BigBangOptimizer Status ==="), false);
        source.sendSuccess(() -> Component.literal("§7Módulo: §f" + (config.isEnabled() ? "§aAtivo" : "§cInativo")), false);
        source.sendSuccess(() -> Component.literal("§7Scheduler: §f" + scheduler.getState()), false);
        source.sendSuccess(() -> Component.literal("§7TPS médio: §f" + String.format("%.1f", tpsMonitor.getAverageTps())), false);
        source.sendSuccess(() -> Component.literal("§7MSPT médio: §f" + String.format("%.1f", tpsMonitor.getAverageMspt()) + "ms"), false);
        source.sendSuccess(() -> Component.literal("§7Heap: §f" + memoryMonitor.getUsedHeapMB() + "/" + memoryMonitor.getMaxHeapMB() + "MB"), false);

        source.sendSuccess(() -> Component.literal("§7§l--- Módulos ---"), false);
        BigBangOptimizer.getInstance().getModuleRegistry().getAll().forEach(module -> {
            source.sendSuccess(() -> Component.literal("§7  " + module.name() + ": §f" + (module.isEnabled() ? "§aAtivo" : "§cInativo")), false);
        });

        return 1;
    }
}
