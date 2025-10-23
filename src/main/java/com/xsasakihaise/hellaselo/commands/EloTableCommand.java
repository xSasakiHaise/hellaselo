package com.xsasakihaise.hellaselo.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import com.xsasakihaise.hellaselo.HellasElo;

import java.util.Comparator;
import java.util.Map;

public class EloTableCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("hellas")
                        .then(Commands.literal("elo")
                                .then(Commands.literal("table")
                                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                                .executes(ctx -> {
                                                    int page = IntegerArgumentType.getInteger(ctx, "page");
                                                    return sendLeaderboardPage(ctx.getSource(), page);
                                                })
                                        )
                                        .executes(ctx -> sendLeaderboardPage(ctx.getSource(), 1))
                                )
                        )
        );
    }

    private static int sendLeaderboardPage(CommandSource src, int page) {
        Map<String, Integer> leaderboard = HellasElo.eloManager.getLeaderboard();

        src.sendSuccess(new StringTextComponent("-----------------------------------"), false);

        int startIndex = (page - 1) * 10;

        if (leaderboard.isEmpty() || startIndex >= leaderboard.size()) {
            src.sendSuccess(new StringTextComponent("No entries on this page."), false);
        } else {
            leaderboard.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                    .skip(startIndex)
                    .limit(10)
                    .forEach(entry -> src.sendSuccess(
                            new StringTextComponent(entry.getKey() + " | " + entry.getValue()), false
                    ));
        }

        src.sendSuccess(new StringTextComponent("-----------------------------------"), false);
        return 1;
    }
}
