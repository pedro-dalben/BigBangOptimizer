package com.pedrodalben.bigbangoptimizer.util;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

public class SafeEntityRemoval {

    public static void remove(Entity entity) {
        if (entity == null || !entity.isAlive()) return;

        if (entity instanceof ItemEntity itemEntity) {
            if (itemEntity.getItem().isEmpty()) {
                entity.discard();
                return;
            }
        }

        if (entity.isPassenger()) {
            entity.stopRiding();
        }

        entity.discard();
    }
}
