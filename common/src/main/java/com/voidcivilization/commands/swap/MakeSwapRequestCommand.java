package com.voidcivilization.commands.swap;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.commands.CivilizationCommandUtil;
import com.voidcivilization.util.Styles;
import com.voidcivilization.data.swap.CivilizationSwapRequest;
import com.voidcivilization.data.swap.CivilizationSwapRequestManager;
import com.voidcivilization.util.Messenger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class MakeSwapRequestCommand {

    /**
     * /civ swap <player>
     * Sends a swap request to a player to swap civilizations
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("swap")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .executes(MakeSwapRequestCommand::makeRequest)
                                )
                        )
        );
    }

    private static int makeRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer requestSender = context.getSource().getPlayerOrException();

        if (!ToggleSwapRequestsCommand.getAllowSwapRequests()) {
            Messenger.sendError(requestSender, "Swap requests are disabled");
            return 0;
        }

        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");
        if (profiles.size() != 1) {
            Messenger.sendError(requestSender, "You must specify one player");
            return 0;
        }

        GameProfile profile = profiles.iterator().next();

        if (requestSender.getGameProfile().equals(profile)) {
            Messenger.sendError(requestSender, "You cannot swap with yourself");
            return 0;
        }

        var civManager = CivilizationManager.get(context.getSource().getLevel());
        var civ = civManager.getCivilization(requestSender.getGameProfile());

        if (civ.isEmpty()) {
            Messenger.sendError(requestSender, "You are not in a civilization");
            return 0;
        }

        var targetPlayer = context.getSource().getServer().getPlayerList().getPlayer(profile.getId());
        if (targetPlayer == null) {
            Messenger.sendError(requestSender, "Player not found");
            return 0;
        }

        if (!civManager.isInCivilization(targetPlayer.getGameProfile())) {
            Messenger.sendError(requestSender, "Player is not in a civilization");
            return 0;
        }

        if (isInSameCivilization(requestSender, targetPlayer)) {
            Messenger.sendError(requestSender, "Player is already in your civilization");
            return 0;
        }

        CivilizationSwapRequest swapRequest = new CivilizationSwapRequest(
                requestSender.getGameProfile(),
                targetPlayer.getGameProfile()
        );
        CivilizationSwapRequestManager.addRequest(swapRequest);

        // confirmation message
        MutableComponent receiverName = Component.literal(targetPlayer.getGameProfile().getName()).withStyle(Styles.PLAYER_NAME);
        Messenger.sendMessage(requestSender, Component.literal("Swap request sent to ").append(receiverName));

        // notification message
        MutableComponent name = Component.literal(requestSender.getGameProfile().getName()).withStyle(Styles.PLAYER_NAME);
        MutableComponent civName = Component.literal(civ.get().getName()).withStyle(civ.get().getColor());
        MutableComponent command = Component.literal("/civ acceptswap " + requestSender.getGameProfile().getName()).withStyle(Styles.COMMAND);
        MutableComponent message = name.append(" ").append("has sent you a swap request to swap civilizations with ")
                .append(civName)
                .append(". Use ")
                .append(command)
                .append(" to accept the swap request.");
        Messenger.sendMessage(targetPlayer, message);

        return 0;
    }

    private static boolean isInSameCivilization(ServerPlayer player1, ServerPlayer player2) {
        var civManager = CivilizationManager.get(player1.getLevel());
        var civ1 = civManager.getCivilization(player1.getGameProfile());
        var civ2 = civManager.getCivilization(player2.getGameProfile());

        if (civ1.isEmpty() || civ2.isEmpty()) {
            return false;
        }

        return civ1.get().equals(civ2.get());
    }

}
