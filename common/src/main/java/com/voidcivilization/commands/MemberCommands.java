package com.voidcivilization.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.voidcivilization.util.Messenger;
import com.voidcivilization.data.civilization.CivilizationManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.stream.Collectors;

public class MemberCommands {

    /**
     * /civ members
     * lists all members of the civilization the player is in
     * <p>
     * /civ members <name>
     * lists all members of the civilization with the given name
     * <p>
     * /civ members <name> add <player>
     * Adds a player to a civilization
     * <p>
     * /civ members <name> remove <player>
     * Removes a player from a civilization
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(CivilizationCommandUtil.COMMAND_PREFIX)
                        .then(Commands.literal("members")
                                .requires(sourceStack -> sourceStack.hasPermission(3))
                                .executes(MemberCommands::listOwnMembers)
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests(CivilizationCommandUtil::recommendCivilizations)
                                        .executes(MemberCommands::listMembers)
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                                        .executes(MemberCommands::addMember)
                                                )
                                        )
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                                        .executes(MemberCommands::removeMember)
                                                )
                                        )
                                )
                        )
        );
    }

    private static int addMember(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sourcePlayer = context.getSource().getPlayerOrException();
        String civName = StringArgumentType.getString(context, "name");
        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");

        var civManager = CivilizationManager.get(sourcePlayer.level);
        var civ = civManager.getCivilization(civName);

        if (civ.isEmpty()) {
            Messenger.sendError(sourcePlayer, civName + " does not exist");
            return 0;
        }

        for (GameProfile profile : profiles) {
            var playerCiv = civManager.getCivilization(profile);

            if (playerCiv.isPresent()) {
                Messenger.sendError(sourcePlayer, profile.getName() + " has already joined " + playerCiv.get().getName());
                return 0;
            }

            if (!civ.get().addMember(profile)) {
                Messenger.sendError(sourcePlayer, "Failed to add " + profile.getName() + " to " + civName);
                return 0;
            }

            ServerPlayer addedMember = (ServerPlayer) sourcePlayer.getLevel().getPlayerByUUID(profile.getId());
            if (addedMember == null) {
                Messenger.sendError(sourcePlayer, profile.getName() + " does not exist");
                return 0;
            }

            civManager.updatePlayerDisplayName(addedMember);
            civManager.syncClientCivilizationData(sourcePlayer.getLevel());
            Messenger.sendSuccess(sourcePlayer, "Successfully added " + profile.getName() + " to " + civName);

            if (!sourcePlayer.getUUID().equals(addedMember.getUUID())) {
                Messenger.sendMessage(addedMember, "You have been added to " + civName);
            }

            civManager.setDirty();
        }

        return 0;
    }

    private static int removeMember(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String civName = StringArgumentType.getString(context, "name");
        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");
        var civManager = CivilizationManager.get(player.level);

        var civ = civManager.getCivilization(civName);

        if (civ.isEmpty()) {
            Messenger.sendError(player, civName + " does not exist");
            return 0;
        }

        for (GameProfile profile : profiles) {
            if (!civ.get().memberExists(profile)) {
                Messenger.sendError(player, profile.getName() + " is not a member of " + civName);
                return 0;
            }

            if (!civ.get().removeMember(profile)) {
                Messenger.sendError(player, "Failed to remove " + profile.getName() + " from " + civName);
                return 0;
            }

            Messenger.sendSuccess(player, "Removed " + profile.getName() + " from " + civName);

            ServerPlayer removedMember = (ServerPlayer) player.getLevel().getPlayerByUUID(profile.getId());

            civManager.setDirty();

            if (removedMember != null) {
                civManager.updatePlayerDisplayName(removedMember);
                civManager.syncClientCivilizationData(removedMember.getLevel());
                if (!player.getUUID().equals(removedMember.getUUID())) {
                    Messenger.sendMessage(removedMember, "You have been removed from " + civName);
                }
            }
        }

        return 0;
    }

    private static int listOwnMembers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        var civ = CivilizationManager.get(player.getLevel()).getCivilization(player.getGameProfile());

        if (civ.isEmpty()) {
            Messenger.sendError(player, "You are not in a civilization");
            return 0;
        }

        Messenger.sendMessage(player, memberListToString(civ.get().getMembers()));

        return 0;
    }

    private static int listMembers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String civName = StringArgumentType.getString(context, "name");

        var civ = CivilizationManager.get(player.getLevel()).getCivilization(civName);

        if (civ.isEmpty()) {
            Messenger.sendError(player, civName + " Does not exist");
            return 0;
        }

        Messenger.sendMessage(player, memberListToString(civ.get().getMembers()));

        return 0;
    }


    private static String memberListToString(Collection<GameProfile> members) {
        if (members.isEmpty()) {
            return "No members";
        }

        return members.stream()
                .map(GameProfile::getName)
                .collect(Collectors.joining(", "));
    }


}
