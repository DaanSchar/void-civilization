package com.voidcivilization.commands.swap;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.commands.CivilizationCommandUtil;
import com.voidcivilization.util.Styles;
import com.voidcivilization.data.swap.CivilizationSwapRequestManager;
import com.voidcivilization.util.Messenger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class AcceptSwapRequestCommand {

    /**
     * /civ acceptswap <player>
     * /civ rejectswap <player>
     *
     * responds to a swap request from a player to swap civilizations
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("acceptswap")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .executes(AcceptSwapRequestCommand::acceptRequest)
                                )
                        )
        );
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("rejectswap")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .executes(AcceptSwapRequestCommand::rejectRequest)
                                )
                        )
        );
    }

    private static int acceptRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return respondToRequest(context, true);
    }

    private static int rejectRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return respondToRequest(context, false);
    }

    private static int respondToRequest(CommandContext<CommandSourceStack> context, boolean accept) throws CommandSyntaxException {
        ServerPlayer requestReceiver = context.getSource().getPlayerOrException();

        if (!ToggleSwapRequestsCommand.getAllowSwapRequests()) {
            Messenger.sendError(requestReceiver, "Swap requests are disabled");
            return 0;
        }

        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");
        ServerLevel level = requestReceiver.getLevel();

        if (profiles.size() > 1) {
            Messenger.sendError(requestReceiver, "Too many players");
            return 0;
        }

        if (profiles.isEmpty()) {
            Messenger.sendError(requestReceiver, "No player specified");
            return 0;
        }

        GameProfile profile = profiles.iterator().next();
        ServerPlayer requestSender = (ServerPlayer) requestReceiver.getLevel().getPlayerByUUID(profile.getId());

        if (requestSender == null) {
            Messenger.sendError(requestReceiver, "Player not found");
            return 0;
        }

        if (requestSender.getGameProfile().equals(requestReceiver.getGameProfile())) {
            Messenger.sendError(requestReceiver, "You cannot swap with yourself");
            return 0;
        }

        var request = CivilizationSwapRequestManager.getRequest(requestSender.getGameProfile(), requestReceiver.getGameProfile());

        if (request.isEmpty()) {
            Messenger.sendError(requestReceiver, "No swap request from " + requestSender.getName().getString());
            return 0;
        }

        if (request.get().isExpired()) {
            Messenger.sendError(requestReceiver, "Swap request from " + requestSender.getName().getString() + " has expired");
            return 0;
        }

        MutableComponent senderName = Component.literal(requestSender.getName().getString()).withStyle(Styles.PLAYER_NAME);
        MutableComponent receiverName = Component.literal(requestReceiver.getName().getString()).withStyle(Styles.PLAYER_NAME);

        if (accept) {
            var civManager = CivilizationManager.get(level);
            var senderCiv = civManager.getCivilization(requestSender.getGameProfile());
            var receiverCiv = civManager.getCivilization(requestReceiver.getGameProfile());

            if (senderCiv.isEmpty() || receiverCiv.isEmpty()) {
                Messenger.sendError(requestReceiver, "You or " + requestSender.getName().getString() + " are not in a civilization");
                return 0;
            }

            Messenger.sendMessage(requestReceiver, Component.literal("You have accepted ").append(senderName).append("'s swap request"));
            Messenger.sendMessage(requestSender, receiverName.append(" has accepted your swap request"));

            senderCiv.get().removeMember(requestSender.getGameProfile());
            receiverCiv.get().removeMember(requestReceiver.getGameProfile());

            senderCiv.get().addMember(requestReceiver.getGameProfile());
            receiverCiv.get().addMember(requestSender.getGameProfile());

            Messenger.sendMessage(requestReceiver, "You have joined " + receiverCiv.get().getName());
            Messenger.sendMessage(requestSender, "You have joined " + senderCiv.get().getName());

            civManager.updatePlayerDisplayName(requestReceiver);
            civManager.updatePlayerDisplayName(requestSender);
            civManager.syncClientCivilizationData(requestReceiver.getLevel());

            civManager.setDirty();
        } else {
            Messenger.sendMessage(requestReceiver, Component.literal("You have rejected ").append(senderName).append("'s swap request"));
            Messenger.sendMessage(requestSender, requestReceiver.getName() + " has rejected your swap request");
        }

        CivilizationSwapRequestManager.deleteRequest(requestSender.getGameProfile(), requestReceiver.getGameProfile());
        return 0;
    }

}
