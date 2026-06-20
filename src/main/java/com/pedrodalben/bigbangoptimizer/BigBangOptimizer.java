package com.pedrodalben.bigbangoptimizer;

import com.pedrodalben.bigbangoptimizer.cleanup.EntityCleanupModule;
import com.pedrodalben.bigbangoptimizer.cleanup.ItemCleanupModule;
import com.pedrodalben.bigbangoptimizer.cleanup.PokemonCleanupModule;
import com.pedrodalben.bigbangoptimizer.cleanup.TemporaryEntityCleanupModule;
import com.pedrodalben.bigbangoptimizer.commands.BigBangOptimizerCommand;
import com.pedrodalben.bigbangoptimizer.config.OptimizerConfig;
import com.pedrodalben.bigbangoptimizer.core.CleanupScheduler;
import com.pedrodalben.bigbangoptimizer.core.ModuleRegistry;
import com.pedrodalben.bigbangoptimizer.integrations.OptimizerIntegration;
import com.pedrodalben.bigbangoptimizer.integrations.cobblemon.CobblemonIntegration;
import com.pedrodalben.bigbangoptimizer.integrations.ftbchunks.FtbChunksIntegration;
import com.pedrodalben.bigbangoptimizer.integrations.spark.SparkIntegration;
import com.pedrodalben.bigbangoptimizer.monitoring.TpsMonitor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BigBangOptimizer.MOD_ID)
public class BigBangOptimizer {
    public static final String MOD_ID = "bigbangoptimizer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static BigBangOptimizer INSTANCE;
    private final ModuleRegistry moduleRegistry;
    private final OptimizerConfig config;
    private CleanupScheduler scheduler;

    public BigBangOptimizer(IEventBus modEventBus, ModContainer modContainer) {
        INSTANCE = this;
        this.config = new OptimizerConfig();
        this.moduleRegistry = new ModuleRegistry();

        modContainer.registerConfig(ModConfig.Type.SERVER, config.getSpec());

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);

        BigBangOptimizerCommand.register(modEventBus);

        LOGGER.info("[BigBangOptimizer] Initializing...");
    }

    private void onServerStarting(net.neoforged.neoforge.event.server.ServerStartingEvent event) {
        OptimizerConfig.refreshInstance(event.getServer());

        registerIntegrations();

        moduleRegistry.register(new EntityCleanupModule());
        moduleRegistry.register(new ItemCleanupModule());
        moduleRegistry.register(new PokemonCleanupModule());
        moduleRegistry.register(new TemporaryEntityCleanupModule());

        scheduler = new CleanupScheduler(moduleRegistry);
        NeoForge.EVENT_BUS.register(scheduler);

        moduleRegistry.initializeAll();

        LOGGER.info("[BigBangOptimizer] Initialization complete.");
    }

    private void onServerStopping(net.neoforged.neoforge.event.server.ServerStoppingEvent event) {
        moduleRegistry.shutdownAll();
        LOGGER.info("[BigBangOptimizer] Shutdown complete.");
    }

    private void registerIntegrations() {
        CobblemonIntegration cobblemon = new CobblemonIntegration();
        FtbChunksIntegration ftbChunks = new FtbChunksIntegration();
        SparkIntegration spark = new SparkIntegration();

        registerIntegration(cobblemon);
        registerIntegration(ftbChunks);
        registerIntegration(spark);
    }

    private void registerIntegration(OptimizerIntegration integration) {
        if (integration.isAvailable()) {
            integration.initialize();
            LOGGER.info("[BigBangOptimizer] {} integration: enabled", integration.id());
        } else {
            LOGGER.info("[BigBangOptimizer] {} integration: not installed", integration.id());
        }
    }

    private void onServerTick(ServerTickEvent.Post event) {
        TpsMonitor.getInstance().tick();
    }

    public static BigBangOptimizer getInstance() {
        return INSTANCE;
    }

    public ModuleRegistry getModuleRegistry() {
        return moduleRegistry;
    }

    public OptimizerConfig getConfig() {
        return config;
    }

    public CleanupScheduler getScheduler() {
        return scheduler;
    }
}
