package com.voidcivilization.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class Messenger {

    private Messenger(){
    }

    public static void sendError(Player player, String message) {
        sendMessage(player, message, ChatFormatting.RED);
    }

    public static void sendSuccess(Player player, String message) {
        sendMessage(player, message, ChatFormatting.GREEN);
    }

    public static void sendMessage(Player player, String message) {
        sendMessage(player, message, ChatFormatting.WHITE);
    }

    public static void sendMessage(Player player, String message, ChatFormatting format) {
        player.displayClientMessage(
                Component.literal("[Civ] ").withStyle(ChatFormatting.DARK_GREEN).append(
                        Component.literal(message).withStyle(format)),
                false
        );
    }

    public static void sendMessage(Player player, Component message) {
        player.displayClientMessage(
                Component.literal("[Civ] ").withStyle(ChatFormatting.DARK_GREEN).append(message),
                false
        );
    }

    public static void sendClientError(Player player, String message) {
        sendClientMessage(player, message, ChatFormatting.RED);
    }

    public static void sendClientSuccess(Player player, String message) {
        sendClientMessage(player, message, ChatFormatting.GREEN);
    }

    public static void sendClientMessage(Player player, String message) {
        sendClientMessage(player, message, ChatFormatting.WHITE);
    }

    public static void sendClientMessage(Player player, String message, ChatFormatting format) {
        player.displayClientMessage(
                Component.literal(message).withStyle(format),
                true
        );
    }

    public static void sendMessageToEveryoneExcept(Player player, String message) {
        player.getLevel()
                .players()
                .forEach(p -> {
                    if (p != player) {
                        sendMessage(p, message);
                    }
                });
    }



}
