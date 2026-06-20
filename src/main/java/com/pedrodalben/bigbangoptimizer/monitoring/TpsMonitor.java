package com.pedrodalben.bigbangoptimizer.monitoring;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayDeque;
import java.util.Deque;

public class TpsMonitor {
    private static TpsMonitor INSTANCE;
    private final Deque<Double> tpsHistory = new ArrayDeque<>();
    private final Deque<Double> msptHistory = new ArrayDeque<>();
    private int sampleCount = 0;
    private long lastTickTime = System.nanoTime();
    private double averageTps = 20.0;
    private double averageMspt = 0.0;

    public static TpsMonitor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TpsMonitor();
        }
        return INSTANCE;
    }

    public void tick() {
        OptimizerConfig config = OptimizerConfig.getInstance();
        if (config == null || !config.isEnabled()) return;

        long now = System.nanoTime();
        double mspt = (now - lastTickTime) / 1_000_000.0;
        lastTickTime = now;

        double tps = Math.min(20.0, 1000.0 / mspt);

        int maxSize = config.getMonitoringHistorySize();
        tpsHistory.addLast(tps);
        msptHistory.addLast(mspt);
        while (tpsHistory.size() > maxSize) tpsHistory.removeFirst();
        while (msptHistory.size() > maxSize) msptHistory.removeFirst();

        sampleCount++;
        int intervalTicks = config.getMonitoringSampleInterval() * 20;
        if (sampleCount % intervalTicks == 0) {
            calculateAverages();
        }
    }

    private void calculateAverages() {
        if (tpsHistory.isEmpty()) return;
        averageTps = tpsHistory.stream().mapToDouble(Double::doubleValue).average().orElse(20.0);
        averageMspt = msptHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public double getAverageTps() { return averageTps; }
    public double getAverageMspt() { return averageMspt; }
    public Deque<Double> getTpsHistory() { return tpsHistory; }
    public Deque<Double> getMsptHistory() { return msptHistory; }
}
