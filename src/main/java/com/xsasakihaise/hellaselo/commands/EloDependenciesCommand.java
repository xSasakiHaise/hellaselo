package com.xsasakihaise.hellaselo.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import com.xsasakihaise.hellaselo.HellasEloInfoConfig;

public class EloDependenciesCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher, HellasEloInfoConfig infoConfig) {
        dispatcher.register(
                Commands.literal("hellas")
                        .then(Commands.literal("elo")
                                .then(Commands.literal("dependencies")
                                        .executes(ctx -> {
                                            if (!infoConfig.isValid()) {
                                                ctx.getSource().sendSuccess(new StringTextComponent("Fehler: HellasElo-Info nicht geladen (fehlende oder ungültige JSON)."), false);
                                                return 0;
                                            }
                                            ctx.getSource().sendSuccess(new StringTextComponent("-----------------------------------"), false);
                                            for (String dep : infoConfig.getDependencies()) {
                                                ctx.getSource().sendSuccess(new StringTextComponent(dep), false);
                                            }
                                            ctx.getSource().sendSuccess(new StringTextComponent("-----------------------------------"), false);
                                            return 1;
                                        })
                                )
                        )
        );
    }
}