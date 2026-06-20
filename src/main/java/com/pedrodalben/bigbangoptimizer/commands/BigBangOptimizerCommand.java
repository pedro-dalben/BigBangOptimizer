package com.pedrodalben.bigbangoptimizer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

public class BigBangOptimizerCommand {
    public static void register(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(BigBangOptimizerCommand::onCommandsRegister);
    }

    private static void onCommandsRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("bbo")
            .then(Commands.literal("status")
                .executes(ctx -> StatusCommand.execute(ctx.getSource())))
            .then(Commands.literal("entities")
                .executes(ctx -> EntityStatsCommand.execute(ctx.getSource()))
                .then(Commands.literal("top")
                    .executes(ctx -> EntityStatsCommand.executeTop(ctx.getSource())))
                .then(Commands.literal("dimension")
                    .then(Commands.argument("dimension", StringArgumentType.word())
                        .executes(ctx -> EntityStatsCommand.executeDimension(ctx.getSource(),
                            StringArgumentType.getString(ctx, "dimension"))))))
            .then(Commands.literal("clear")
                .then(Commands.literal("preview")
                    .then(Commands.literal("items")
                        .executes(ctx -> ClearCommand.previewItems(ctx.getSource())))
                    .then(Commands.literal("pokemon")
                        .executes(ctx -> ClearCommand.previewPokemon(ctx.getSource())))
                    .then(Commands.literal("all")
                        .executes(ctx -> ClearCommand.previewAll(ctx.getSource()))))
                .then(Commands.literal("now")
                    .then(Commands.literal("items")
                        .executes(ctx -> ClearCommand.nowItems(ctx.getSource())))
                    .then(Commands.literal("pokemon")
                        .executes(ctx -> ClearCommand.nowPokemon(ctx.getSource())))
                    .then(Commands.literal("all")
                        .executes(ctx -> ClearCommand.nowAll(ctx.getSource()))))
                .then(Commands.literal("schedule")
                    .then(Commands.literal("items")
                        .executes(ctx -> ClearCommand.scheduleItems(ctx.getSource())))
                    .then(Commands.literal("pokemon")
                        .executes(ctx -> ClearCommand.schedulePokemon(ctx.getSource()))))
                .then(Commands.literal("cancel")
                    .executes(ctx -> ClearCommand.cancel(ctx.getSource()))))
            .then(Commands.literal("reload")
                .executes(ctx -> ConfigCommand.reload(ctx.getSource())))
            .then(Commands.literal("debug")
                .then(Commands.literal("cobblemon")
                    .executes(ctx -> ConfigCommand.debugCobblemon(ctx.getSource()))))
        );

        dispatcher.register(Commands.literal("bigbangoptimizer")
            .redirect(dispatcher.getRoot().getChild("bbo")));

        dispatcher.register(Commands.literal("bboptimizer")
            .redirect(dispatcher.getRoot().getChild("bbo")));
    }
}
