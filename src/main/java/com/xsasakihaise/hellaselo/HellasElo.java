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
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

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

    /** Shared runtime configuration that defines base K-factor values. */
    public static EloConfig config;
    /** Tracks player ratings and persists them to disk. */
    public static EloManager eloManager;

    /**
     * Instantiates the configuration/manager pair and registers event handlers if the
     * dependency on HellasControl is satisfied.
     */
    public HellasElo() {
        CoreCheck.verifyCoreLoaded();

        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            CoreCheck.verifyEntitled("hellaselo");
        }

        if (!ModList.get().isLoaded("hellascontrol")) {
            return;
        }

        config = new EloConfig();
        eloManager = new EloManager(config);

        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Loads configuration and rating data from disk once the logical server is ready.
     *
     * @param event Forge event fired when the dedicated or integrated server starts.
     */
    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent event) {
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
        EloAddMatchCommand.register(event.getDispatcher());
        EloTableCommand.register(event.getDispatcher());
    }
}
