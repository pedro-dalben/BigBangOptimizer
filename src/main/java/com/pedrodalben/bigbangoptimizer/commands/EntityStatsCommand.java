package com.pedrodalben.bigbangoptimizer.commands;

import com.pedrodalben.bigbangoptimizer.monitoring.EntityStatisticsCollector;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.stream.Collectors;

public class EntityStatsCommand {

    public static int execute(CommandSourceStack source) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            source.sendFailure(Component.literal("§cServidor não disponível."));
            return 0;
        }

        EntityStatisticsCollector collector = EntityStatisticsCollector.getInstance();
        Map<String, Integer> byType = collector.countByType(server);
        Map<String, Integer> byDimension = collector.countByDimension(server);
        int items = collector.countItems(server);
        int pokemon = collector.countPokemon(server);

        source.sendSuccess(() -> Component.literal("§6§l=== Estatísticas de Entidades ==="), false);
        source.sendSuccess(() -> Component.literal("§7Total de entidades: §f" + byType.values().stream().mapToInt(Integer::intValue).sum()), false);
        source.sendSuccess(() -> Component.literal("§7Itens soltos: §f" + items), false);
        source.sendSuccess(() -> Component.literal("§7Pokémon: §f" + pokemon), false);

        source.sendSuccess(() -> Component.literal("§7§l--- Por Dimensão ---"), false);
        byDimension.forEach((dim, count) ->
            source.sendSuccess(() -> Component.literal("§7  " + dim + ": §f" + count), false)
        );

        source.sendSuccess(() -> Component.literal("§7§l--- Top 10 por Tipo ---"), false);
        byType.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry ->
                source.sendSuccess(() -> Component.literal("§7  " + entry.getKey() + ": §f" + entry.getValue()), false)
            );

        return 1;
    }

    public static int executeTop(CommandSourceStack source) {
        return execute(source);
    }

    public static int executeDimension(CommandSourceStack source, String dimension) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            source.sendFailure(Component.literal("§cServidor não disponível."));
            return 0;
        }

        EntityStatisticsCollector collector = EntityStatisticsCollector.getInstance();
        Map<String, Integer> byType = collector.countByType(server);

        source.sendSuccess(() -> Component.literal("§6§l=== Entidades em " + dimension + " ==="), false);
        byType.forEach((type, count) ->
            source.sendSuccess(() -> Component.literal("§7  " + type + ": §f" + count), false)
        );

        return 1;
    }
}
