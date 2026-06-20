package com.pedrodalben.bigbangoptimizer.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ComponentFactory {

    public static Component text(String text) {
        return Component.literal(text);
    }

    public static MutableComponent mutable(String text) {
        return Component.literal(text);
    }

    public static Component success(String text) {
        return Component.literal("§a" + text);
    }

    public static Component warning(String text) {
        return Component.literal("§6" + text);
    }

    public static Component error(String text) {
        return Component.literal("§c" + text);
    }

    public static Component info(String text) {
        return Component.literal("§7" + text);
    }
}
