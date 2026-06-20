package com.pedrodalben.bigbangoptimizer.cleanup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.core.CleanupResult;
import com.pedrodalben.bigbangoptimizer.util.TimeFormatters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Map;

public class EntityCleanupAuditLogger {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path LOG_DIR = Paths.get("logs", "bigbangoptimizer");

    public static void logExecution(CleanupResult result) {
        try {
            Files.createDirectories(LOG_DIR);
        } catch (IOException e) {
            BigBangOptimizer.LOGGER.error("[BigBangOptimizer] Failed to create log directory: {}", e.getMessage());
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("timestamp", TimeFormatters.formatTimestamp(Instant.now()));
        json.addProperty("trigger", result.getTrigger());
        json.addProperty("reason", result.getReason());
        json.addProperty("duration_ms", result.getDurationMs());
        json.addProperty("items_removed", result.getItemsRemoved());
        json.addProperty("pokemon_removed", result.getPokemonRemoved());
        json.addProperty("loot_balls_removed", result.getLootBallsRemoved());
        json.addProperty("temporary_entities_removed", result.getTemporaryEntitiesRemoved());
        json.addProperty("total_protected", result.getTotalProtected());

        JsonObject protectedObj = new JsonObject();
        for (Map.Entry<String, Integer> entry : result.getProtectedByReason().entrySet()) {
            protectedObj.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("protected", protectedObj);

        JsonObject dimensionsObj = new JsonObject();
        for (Map.Entry<String, CleanupResult.DimensionStats> entry : result.getDimensionStats().entrySet()) {
            JsonObject dimJson = new JsonObject();
            dimJson.addProperty("items_removed", entry.getValue().getItemsRemoved());
            dimJson.addProperty("pokemon_removed", entry.getValue().getPokemonRemoved());
            dimensionsObj.add(entry.getKey(), dimJson);
        }
        json.add("dimensions", dimensionsObj);

        Path logFile = LOG_DIR.resolve("cleanup-" + Instant.now().toEpochMilli() + ".json");
        try {
            Files.writeString(logFile, GSON.toJson(json) + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Audit log written to {}", logFile);
        } catch (IOException e) {
            BigBangOptimizer.LOGGER.error("[BigBangOptimizer] Failed to write audit log: {}", e.getMessage());
        }

        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Cleanup summary:");
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer]   Items removed: {}", result.getItemsRemoved());
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer]   Pokemon removed: {}", result.getPokemonRemoved());
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer]   Total protected: {}", result.getTotalProtected());
    }
}
