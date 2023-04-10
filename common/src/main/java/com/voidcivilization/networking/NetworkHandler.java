package com.voidcivilization.networking;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.networking.packets.BreakNucleusPacket;
import com.voidcivilization.networking.packets.MemberDeathPacket;
import com.voidcivilization.networking.packets.SyncCivilizationsDataPacket;
import com.voidcivilization.networking.packets.UpdateConfigPacket;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(VoidCivilization.MOD_ID, "networking_channel"));

    public static void register() {
        CHANNEL.register(
                BreakNucleusPacket.class,
                BreakNucleusPacket::encode,
                BreakNucleusPacket::new,
                BreakNucleusPacket::handle
        );

        CHANNEL.register(
                MemberDeathPacket.class,
                MemberDeathPacket::encode,
                MemberDeathPacket::new,
                MemberDeathPacket::handle
        );

        CHANNEL.register(
                SyncCivilizationsDataPacket.class,
                SyncCivilizationsDataPacket::encode,
                SyncCivilizationsDataPacket::new,
                SyncCivilizationsDataPacket::handle
        );

        CHANNEL.register(
                UpdateConfigPacket.class,
                UpdateConfigPacket::encode,
                UpdateConfigPacket::new,
                UpdateConfigPacket::handle
        );
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        CHANNEL.sendToPlayer(player, message);
    }

    public static void sendToAllPlayers(ServerLevel level, Object packet) {
        CHANNEL.sendToPlayers(level.players(), packet);
    }


}
