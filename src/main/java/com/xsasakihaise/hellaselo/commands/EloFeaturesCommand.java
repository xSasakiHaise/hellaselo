package com.xsasakihaise.hellaselo.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import com.xsasakihaise.hellaselo.HellasEloInfoConfig;

public class EloFeaturesCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher, HellasEloInfoConfig infoConfig) {
        dispatcher.register(
                Commands.literal("hellas")
                        .then(Commands.literal("elo")
                                .then(Commands.literal("features")
                                        .executes(ctx -> {
                                            if (!infoConfig.isValid()) {
                                                ctx.getSource().sendSuccess(new StringTextComponent("Fehler: HellasElo-Info nicht geladen (fehlende oder ungültige JSON)."), false);
                                                return 0;
                                            }
                                            ctx.getSource().sendSuccess(new StringTextComponent("-----------------------------------"), false);
                                            for (String feature : infoConfig.getFeatures()) {
                                                ctx.getSource().sendSuccess(new StringTextComponent(feature), false);
                                            }
                                            ctx.getSource().sendSuccess(new StringTextComponent("-----------------------------------"), false);
                                            return 1;
                                        })
                                )
                        )
        );
    }
}