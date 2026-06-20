package com.pedrodalben.bigbangoptimizer.commands;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.cleanup.*;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import com.pedrodalben.bigbangoptimizer.core.CleanupExecution;
import com.pedrodalben.bigbangoptimizer.core.CleanupPlan;
import com.pedrodalben.bigbangoptimizer.core.CleanupResult;
import com.pedrodalben.bigbangoptimizer.core.OptimizerPermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Map;

public class ClearCommand {

    private static ServerPlayer getPlayer(CommandSourceStack source) {
        try {
            return source.getPlayer();
        } catch (Exception e) {
            return null;
        }
    }

    public static int previewItems(CommandSourceStack source) {
        if (!OptimizerPermissions.hasPreviewPermission(getPlayer(source))) return 0;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            source.sendFailure(Component.literal("§cServidor não disponível."));
            return 0;
        }

        ItemCleanupModule module = new ItemCleanupModule();
        CleanupPlan plan = module.preview(server);

        source.sendSuccess(() -> Component.literal("§6§l=== Preview de Itens ==="), false);
        source.sendSuccess(() -> Component.literal("§7Escaneados: §f" + plan.getTotalScanned()), false);
        source.sendSuccess(() -> Component.literal("§7Elegíveis para remoção: §a" + plan.getEligibleCount()), false);
        source.sendSuccess(() -> Component.literal("§7Protegidos: §c" + plan.getProtectedTotal()), false);

        for (Map.Entry<String, Integer> entry : plan.getProtectedCounts().entrySet()) {
            source.sendSuccess(() -> Component.literal("§7  - " + entry.getKey() + ": §f" + entry.getValue()), false);
        }

        return 1;
    }

    public static int previewPokemon(CommandSourceStack source) {
        if (!OptimizerPermissions.hasPreviewPermission(getPlayer(source))) return 0;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            source.sendFailure(Component.literal("§cServidor não disponível."));
            return 0;
        }

        PokemonCleanupModule module = new PokemonCleanupModule();
        module.initialize();
        CleanupPlan plan = module.preview(server);

        source.sendSuccess(() -> Component.literal("§6§l=== Preview de Pokémon ==="), false);
        source.sendSuccess(() -> Component.literal("§7Escaneados: §f" + plan.getTotalScanned()), false);
        source.sendSuccess(() -> Component.literal("§7Elegíveis para remoção: §a" + plan.getEligibleCount()), false);
        source.sendSuccess(() -> Component.literal("§7Protegidos: §c" + plan.getProtectedTotal()), false);

        for (Map.Entry<String, Integer> entry : plan.getProtectedCounts().entrySet()) {
            source.sendSuccess(() -> Component.literal("§7  - " + entry.getKey() + ": §f" + entry.getValue()), false);
        }

        return 1;
    }

    public static int previewAll(CommandSourceStack source) {
        previewItems(source);
        previewPokemon(source);
        return 1;
    }

    public static int nowItems(CommandSourceStack source) {
        if (!OptimizerPermissions.hasAdminPermission(getPlayer(source))) {
            source.sendFailure(Component.literal("§cPermissão negada."));
            return 0;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            source.sendFailure(Component.literal("§cServidor não disponível."));
            return 0;
        }

        OptimizerConfig config = OptimizerConfig.getInstance();
        ItemCleanupModule module = new ItemCleanupModule();
        CleanupExecution execution = module.execute(server, false, config.getItemsMaxRemovedPerRun());

        source.sendSuccess(() -> Component.literal("§a✔ Limpeza de itens concluída."), false);
        source.sendSuccess(() -> Component.literal("§7Removidos: §f" + execution.getRemovedCount()), false);
        source.sendSuccess(() -> Component.literal("§7Protegidos: §c" + execution.getProtectedTotal()), false);

        return 1;
    }

    public static int nowPokemon(CommandSourceStack source) {
        if (!OptimizerPermissions.hasAdminPermission(getPlayer(source))) {
            source.sendFailure(Component.literal("§cPermissão negada."));
            return 0;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            source.sendFailure(Component.literal("§cServidor não disponível."));
            return 0;
        }

        OptimizerConfig config = OptimizerConfig.getInstance();
        PokemonCleanupModule module = new PokemonCleanupModule();
        module.initialize();
        CleanupExecution execution = module.execute(server, false, config.getPokemonMaxRemovedPerRun());

        source.sendSuccess(() -> Component.literal("§a✔ Limpeza de Pokémon concluída."), false);
        source.sendSuccess(() -> Component.literal("§7Removidos: §f" + execution.getRemovedCount()), false);
        source.sendSuccess(() -> Component.literal("§7Protegidos: §c" + execution.getProtectedTotal()), false);

        return 1;
    }

    public static int nowAll(CommandSourceStack source) {
        if (!OptimizerPermissions.hasAdminPermission(getPlayer(source))) {
            source.sendFailure(Component.literal("§cPermissão negada."));
            return 0;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            source.sendFailure(Component.literal("§cServidor não disponível."));
            return 0;
        }

        CleanupResult result = CleanupRunner.run(
            BigBangOptimizer.getInstance().getModuleRegistry(),
            server, "command", "manual_command"
        );

        source.sendSuccess(() -> Component.literal("§a✔ Limpeza completa concluída."), false);
        source.sendSuccess(() -> Component.literal("§7Itens removidos: §f" + result.getItemsRemoved()), false);
        source.sendSuccess(() -> Component.literal("§7Pokémon removidos: §f" + result.getPokemonRemoved()), false);
        source.sendSuccess(() -> Component.literal("§7Protegidos: §f" + result.getTotalProtected()), false);
        source.sendSuccess(() -> Component.literal("§7Duração: §f" + result.getDurationMs() + "ms"), false);

        return 1;
    }

    public static int scheduleItems(CommandSourceStack source) {
        if (!OptimizerPermissions.hasAdminPermission(getPlayer(source))) {
            source.sendFailure(Component.literal("§cPermissão negada."));
            return 0;
        }

        BigBangOptimizer.getInstance().getScheduler().scheduleImmediate();
        source.sendSuccess(() -> Component.literal("§a✔ Limpeza agendada para próximo tick."), false);
        return 1;
    }

    public static int schedulePokemon(CommandSourceStack source) {
        if (!OptimizerPermissions.hasAdminPermission(getPlayer(source))) {
            source.sendFailure(Component.literal("§cPermissão negada."));
            return 0;
        }

        BigBangOptimizer.getInstance().getScheduler().scheduleImmediate();
        source.sendSuccess(() -> Component.literal("§a✔ Limpeza agendada para próximo tick."), false);
        return 1;
    }

    public static int cancel(CommandSourceStack source) {
        if (!OptimizerPermissions.hasAdminPermission(getPlayer(source))) {
            source.sendFailure(Component.literal("§cPermissão negada."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("§a✔ Nenhuma limpeza agendada para cancelar."), false);
        return 1;
    }
}
