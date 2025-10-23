package com.xsasakihaise.hellaselo.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import com.xsasakihaise.hellaselo.EloManager;
import com.xsasakihaise.hellaselo.HellasElo;

public class EloAddMatchCommand {

    public static final SuggestionProvider<CommandSource> PLAYER_SUGGESTIONS = (ctx, builder) -> {
        EloManager manager = HellasElo.eloManager;
        return ISuggestionProvider.suggest(manager.getAllUsernames(), builder);
    };

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("hellas")
                        .then(Commands.literal("elo")
                                .then(Commands.literal("addmatch")
                                        .then(Commands.argument("winner", StringArgumentType.word())
                                                .suggests(PLAYER_SUGGESTIONS)
                                                .then(Commands.argument("loser", StringArgumentType.word())
                                                        .suggests(PLAYER_SUGGESTIONS)
                                                        .then(Commands.argument("remainingPokemon", IntegerArgumentType.integer(0))
                                                                .executes(ctx -> {
                                                                    String winner = StringArgumentType.getString(ctx, "winner");
                                                                    String loser = StringArgumentType.getString(ctx, "loser");
                                                                    int remaining = IntegerArgumentType.getInteger(ctx, "remainingPokemon");

                                                                    HellasElo.eloManager.addMatch(
                                                                            winner, loser, remaining,
                                                                            ctx.getSource().getServer().getServerDirectory()
                                                                    );

                                                                    ctx.getSource().sendSuccess(new StringTextComponent("Match recorded!"), false);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }
}
