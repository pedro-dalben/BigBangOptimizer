package com.pedrodalben.bigbangoptimizer.core;

import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class OptimizerPermissions {
    public static boolean hasAdminPermission(ServerPlayer player) {
        if (player == null) return true;
        return player.hasPermissions(Commands.LEVEL_ADMINS);
    }

    public static boolean hasPreviewPermission(ServerPlayer player) {
        if (player == null) return true;
        return player.hasPermissions(Commands.LEVEL_ADMINS);
    }
}
