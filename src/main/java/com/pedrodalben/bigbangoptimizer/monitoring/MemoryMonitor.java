package com.pedrodalben.bigbangoptimizer.monitoring;

public class MemoryMonitor {
    private static MemoryMonitor INSTANCE;

    public static MemoryMonitor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MemoryMonitor();
        }
        return INSTANCE;
    }

    public long getUsedHeapMB() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }

    public long getMaxHeapMB() {
        return Runtime.getRuntime().maxMemory() / (1024 * 1024);
    }

    public double getHeapUsagePercent() {
        long used = getUsedHeapMB();
        long max = getMaxHeapMB();
        return max > 0 ? (used * 100.0 / max) : 0;
    }
}
