package com.voidcivilization.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class WhiteListCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("whitelister").requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("add").then(Commands.literal("list").then(Commands.argument("targets", GameProfileArgument.gameProfile()).executes(context -> {
                            List<String> names = getNames(context);

                            for (String name : names) {
                                addPlayers(context.getSource(), getGameProfile(name, context));
                            }

                            return 0;
                        }))))
        );
    }

    private static List<String> getNames(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<GameProfile> gameProfiles = GameProfileArgument.getGameProfiles(context, "targets");

        String names = "";
        for (GameProfile profile : gameProfiles) {
            names = profile.getName();
        }
        return Arrays.stream(names.split(",")).toList();
    }

    private static Collection<GameProfile> getGameProfile(String playerName, CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return GameProfileArgument.gameProfile().parse(new StringReader(playerName)).getNames(context.getSource());
    }

    private static void addPlayers(CommandSourceStack sourceStack, Collection<GameProfile> gameProfiles) {
        UserWhiteList userwhitelist = sourceStack.getServer().getPlayerList().getWhiteList();

        for (GameProfile gameprofile : gameProfiles) {
            if (!userwhitelist.isWhiteListed(gameprofile)) {
                UserWhiteListEntry userwhitelistentry = new UserWhiteListEntry(gameprofile);
                userwhitelist.add(userwhitelistentry);
                sourceStack.sendSuccess(Component.literal("Succesfully added ").append(ComponentUtils.getDisplayName(gameprofile)), true);
            } else {
                sourceStack.sendFailure(Component.literal("Player is already whitelisted: " + ComponentUtils.getDisplayName(gameprofile).getString()));
            }
        }
    }

}
