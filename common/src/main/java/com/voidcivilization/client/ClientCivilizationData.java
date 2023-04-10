package com.voidcivilization.client;

import com.mojang.authlib.GameProfile;
import com.voidcivilization.data.civilization.Civilization;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientCivilizationData {

    private static Map<String, Civilization> civilizations = new HashMap<>();

    public static void setCivilizations(Map<String, Civilization> civilizations) {
        ClientCivilizationData.civilizations = civilizations;
    }

    public static Civilization getCivilization(String name) {
        return civilizations.get(name);
    }

    public static Optional<Civilization> getCivilization(GameProfile profile) {
        return civilizations.values().stream()
                .filter(civilization -> civilization.getMembers().contains(profile))
                .findFirst();
    }

    public static Optional<Civilization> getCivilization() {
        Player player = Minecraft.getInstance().player;

        if (player == null) {
            return Optional.empty();
        }

        return getCivilization(player.getGameProfile());
    }

    public static Map<String, Civilization> getCivilizations() {
        return civilizations;
    }
}
