package com.voidcivilization.client.data;

import com.mojang.authlib.GameProfile;
import com.voidcivilization.data.kda.KDA;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ClientKDAData {

    private static HashMap<UUID, KDA> kdas = new HashMap<>();

    public static HashMap<UUID, KDA> getKdas() {
        return kdas;
    }

    public static void setKda(HashMap<UUID, KDA> kdas) {
        ClientKDAData.kdas = kdas;
    }

    public static Optional<KDA> getKda(GameProfile profile) {
        return Optional.ofNullable(kdas.get(profile.getId()));
    }

    public static Optional<KDA> getKda() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            return Optional.empty();
        }

        return getKda(mc.player.getGameProfile());
    }
}
