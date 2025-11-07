package com.xsasakihaise.hellaselo;

import com.xsasakihaise.hellascontrol.api.CoreCheck;
import com.xsasakihaise.hellaselo.commands.EloAddMatchCommand;
import com.xsasakihaise.hellaselo.commands.EloDependenciesCommand;
import com.xsasakihaise.hellaselo.commands.EloFeaturesCommand;
import com.xsasakihaise.hellaselo.commands.EloTableCommand;
import com.xsasakihaise.hellaselo.commands.EloVersionCommand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.File;

@Mod("hellaselo")
public class HellasElo {

    public static EloConfig config;
    public static EloManager eloManager;
    public static HellasEloInfoConfig infoConfig;

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
        infoConfig = new HellasEloInfoConfig();

        // Lade gebündelte defaults sofort, damit Commands beim Registrieren korrekte Daten haben.
        infoConfig.loadDefaultsFromResource();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent event) {
        File serverRoot = event.getServer().getServerDirectory();
        config.loadConfig(serverRoot);
        eloManager.loadData(serverRoot);
        infoConfig.load(serverRoot); // überschreibt ggf. mit Serverdatei
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        EloAddMatchCommand.register(event.getDispatcher());
        EloTableCommand.register(event.getDispatcher());
        EloVersionCommand.register(event.getDispatcher(), infoConfig);
        EloDependenciesCommand.register(event.getDispatcher(), infoConfig);
        EloFeaturesCommand.register(event.getDispatcher(), infoConfig);
    }
}
