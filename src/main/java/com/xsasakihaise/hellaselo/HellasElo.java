package com.xsasakihaise.hellaselo;

import com.xsasakihaise.hellascontrol.api.CoreCheck;
import com.xsasakihaise.hellaselo.commands.EloAddMatchCommand;
import com.xsasakihaise.hellaselo.commands.EloTableCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Forge entry point that wires the Hellas Elo ranking system into the server lifecycle.
 * <p>
 * The mod keeps the lightweight Elo configuration and manager singletons alive so that
 * commands and other extensions can record battle outcomes and display the ranking table.
 * </p>
 */
@Mod("hellaselo")
public class HellasElo {

    private static final Logger LOGGER = LogManager.getLogger("HellasElo");
    private static final String ENTITLEMENT_KEY = "hellaselo";
    private static volatile boolean ENABLED = false;
    private static volatile String DISABLE_REASON = "UNINITIALIZED";

    /** Shared runtime configuration that defines base K-factor values. */
    public static EloConfig config;
    /** Tracks player ratings and persists them to disk. */
    public static EloManager eloManager;

    /**
     * Instantiates the configuration/manager pair and registers event handlers if the
     * dependency on HellasControl is satisfied.
     */
    public HellasElo() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        config = new EloConfig();
        eloManager = new EloManager(config);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(this::initGate);
    }

    private void initGate() {
        if (FMLEnvironment.dist != Dist.DEDICATED_SERVER) {
            ENABLED = true;
            DISABLE_REASON = "OK (non-dedicated)";
            return;
        }

        if (!ModList.get().isLoaded("hellascontrol")) {
            ENABLED = false;
            DISABLE_REASON = "HellasControl missing";
            LOGGER.warn("[HellasElo] disabled: {}", DISABLE_REASON);
            return;
        }

        try {
            CoreCheck.verifyCoreLoaded();
            CoreCheck.verifyEntitled(ENTITLEMENT_KEY);

            ENABLED = true;
            DISABLE_REASON = "OK";
            LOGGER.info("[HellasElo] enabled (license OK) entitlement='{}'", ENTITLEMENT_KEY);
        } catch (Exception e) {
            ENABLED = false;
            DISABLE_REASON = "License invalid";
            LOGGER.warn("[HellasElo] disabled: {} entitlement='{}'", DISABLE_REASON, ENTITLEMENT_KEY, e);
        }
    }

    /**
     * Loads configuration and rating data from disk once the logical server is ready.
     *
     * @param event Forge event fired when the dedicated or integrated server starts.
     */
    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent event) {
        if (!ENABLED) {
            return;
        }

        File serverRoot = event.getServer().getServerDirectory();
        config.loadConfig(serverRoot);
        eloManager.loadData(serverRoot);
    }

    /**
     * Registers the administrative Elo commands.
     *
     * @param event command registration event supplied by Forge during server init.
     */
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        if (!ENABLED) {
            return;
        }

        EloAddMatchCommand.register(event.getDispatcher());
        EloTableCommand.register(event.getDispatcher());
    }
}
