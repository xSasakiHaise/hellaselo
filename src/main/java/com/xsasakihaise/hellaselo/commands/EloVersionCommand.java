package com.xsasakihaise.hellaselo.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import com.xsasakihaise.hellaselo.HellasEloInfoConfig;

public class EloVersionCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher, HellasEloInfoConfig infoConfig) {
        dispatcher.register(
                Commands.literal("hellas")
                        .then(Commands.literal("elo")
                                .then(Commands.literal("version")
                                        .executes(ctx -> {
                                            if (!infoConfig.isValid()) {
                                                ctx.getSource().sendSuccess(new StringTextComponent("Fehler: HellasElo-Info nicht geladen (fehlende oder ungültige JSON)."), false);
                                                return 0;
                                            }
                                            ctx.getSource().sendSuccess(new StringTextComponent("-----------------------------------"), false);
                                            ctx.getSource().sendSuccess(new StringTextComponent("Version: " + infoConfig.getVersion()), false);
                                            ctx.getSource().sendSuccess(new StringTextComponent("-----------------------------------"), false);
                                            return 1;
                                        })
                                )
                        )
        );
    }
}