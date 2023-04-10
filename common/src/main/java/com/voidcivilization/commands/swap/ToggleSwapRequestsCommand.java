package com.voidcivilization.commands.swap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.commands.CivilizationCommandUtil;
import com.voidcivilization.util.Messenger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ToggleSwapRequestsCommand {

    private static boolean allowSwapRequests = false;

    /**
     * /civ allowswap <boolean>
     * Toggles if players can send swap requests
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("allowswap")
                                .then(Commands.argument("boolean", StringArgumentType.string())
                                        .suggests(CivilizationCommandUtil::recommendBoolean)
                                        .executes(ToggleSwapRequestsCommand::toggleSwap)
                                ))
        );
    }

    private static int toggleSwap(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String bool = StringArgumentType.getString(context, "boolean");
        allowSwapRequests = bool.equals("true");

        String message = allowSwapRequests ? "Players can now send swap requests. use /civ swap [player] to send one!" : "Players can no longer send swap requests";

        player.getLevel()
                .players()
                .forEach(p -> Messenger.sendSuccess(p, message));

        return 0;
    }

    public static boolean getAllowSwapRequests() {
        return allowSwapRequests;
    }

}
