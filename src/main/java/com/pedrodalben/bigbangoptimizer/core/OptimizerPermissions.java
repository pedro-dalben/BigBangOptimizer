package com.pedrodalben.bigbangoptimizer.core;

import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class OptimizerPermissions {
    public static boolean hasAdminPermission(ServerPlayer player) {
        return player.hasPermissions(Commands.LEVEL_ADMINS);
    }

    public static boolean hasPreviewPermission(ServerPlayer player) {
        return player.hasPermissions(Commands.LEVEL_ADMINS);
    }
}
