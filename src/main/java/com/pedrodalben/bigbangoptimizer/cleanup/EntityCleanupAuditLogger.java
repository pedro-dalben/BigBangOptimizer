package com.pedrodalben.bigbangoptimizer.cleanup;

import com.google.gson.Gson;
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
import java.util.concurrent.CompletableFuture;

public class EntityCleanupAuditLogger {
    private static final Gson GSON = new Gson();
    private static final Path LOG_DIR = Paths.get("logs", "bigbangoptimizer");
    private static final Path LOG_FILE = LOG_DIR.resolve("cleanup.log");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public static void logExecution(CleanupResult result) {
        // Print human-readable summary to console immediately
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer] Cleanup summary:");
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer]   Items removed: {}", result.getItemsRemoved());
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer]   Pokemon removed: {}", result.getPokemonRemoved());
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer]   Total protected: {}", result.getTotalProtected());
        BigBangOptimizer.LOGGER.info("[BigBangOptimizer]   Duration: {}ms", result.getDurationMs());

        // Prepare JSON payload
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

        final String logLine = GSON.toJson(json) + "\n";

        // Write to log file asynchronously in background thread
        CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(LOG_DIR);
                if (Files.exists(LOG_FILE) && Files.size(LOG_FILE) > MAX_FILE_SIZE) {
                    rotateLogs();
                }
                Files.writeString(LOG_FILE, logLine, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) {
                BigBangOptimizer.LOGGER.error("[BigBangOptimizer] Failed to write audit log asynchronously: {}", e.getMessage());
            }
        });
    }

    private static void rotateLogs() {
        try {
            for (int i = 5; i > 0; i--) {
                Path source = LOG_DIR.resolve("cleanup.log" + (i == 1 ? "" : "." + (i - 1)));
                Path target = LOG_DIR.resolve("cleanup.log." + i);
                if (Files.exists(source)) {
                    Files.move(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            BigBangOptimizer.LOGGER.warn("[BigBangOptimizer] Failed to rotate audit logs: {}", e.getMessage());
        }
    }
}
