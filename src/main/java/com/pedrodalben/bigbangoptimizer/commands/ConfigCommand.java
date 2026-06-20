package com.pedrodalben.bigbangoptimizer.commands;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.integrations.cobblemon.CobblemonCompatibilityReport;
import com.pedrodalben.bigbangoptimizer.integrations.cobblemon.CobblemonEntityInspector;
import com.pedrodalben.bigbangoptimizer.integrations.cobblemon.CobblemonIntegration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class ConfigCommand {

    public static int reload(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("§a✔ Configuração recarregada."), false);
        source.sendSuccess(() -> Component.literal("§7Nota: Alterações de config requerem restart do servidor para efeito completo."), false);
        return 1;
    }

    public static int debugCobblemon(CommandSourceStack source) {
        CobblemonIntegration integration = CobblemonIntegration.getInstance();

        if (integration == null) {
            source.sendSuccess(() -> Component.literal("§cCobblemon integration: não inicializada"), false);
            return 1;
        }

        source.sendSuccess(() -> Component.literal("§6§l=== Cobblemon Debug ==="), false);
        source.sendSuccess(() -> Component.literal("§7Disponível: §f" + (integration.isAvailable() ? "§aSim" : "§cNão")), false);
        source.sendSuccess(() -> Component.literal("§7Ativo: §f" + (integration.isEnabled() ? "§aSim" : "§cNão")), false);

        if (integration.isEnabled()) {
            CobblemonEntityInspector inspector = integration.getInspector();
            source.sendSuccess(() -> Component.literal("§7Verificações disponíveis: §f" + inspector.getAvailableChecks()), false);
            CobblemonCompatibilityReport.generate(inspector);
        }

        return 1;
    }
}
