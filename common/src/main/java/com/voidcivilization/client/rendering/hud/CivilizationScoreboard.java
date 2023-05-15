package com.voidcivilization.client.rendering.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.voidcivilization.client.data.ClientCivilizationData;
import com.voidcivilization.client.data.ClientConfigData;
import com.voidcivilization.client.rendering.ColorUtil;
import com.voidcivilization.data.civilization.Civilization;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;

public class CivilizationScoreboard {

    public static void render(PoseStack matrixStack, float tickDelta) {
        Minecraft client = Minecraft.getInstance();

        if (!client.options.keyPlayerList.isDown()) {
            return;
        }

        Player player = client.player;

        if (player == null) {
            return;
        }

        var playerCiv = ClientCivilizationData.getCivilization();
        if (playerCiv.isEmpty()) {
            return;
        }

        int maxHealth = ClientConfigData.getMaxNucleusHealth();
        int i = 0;
        List<Civilization> civilizations = ClientCivilizationData.getCivilizations()
                .values()
                .stream()
                .sorted(Comparator.comparingInt(Civilization::getHealth).reversed())
                .toList();

        for (Civilization civilization : civilizations) {
            ChatFormatting color = civilization.getColor();
            String name = civilization.getName();
            int health = civilization.getHealth();

            int x = 10;
            int y = 10 * i + 30;

            client.font.drawShadow(matrixStack, name, x, y, ColorUtil.toHex(civilization.isDead() ? ChatFormatting.DARK_GRAY : color));
            if (!civilization.isDead()) {
                client.font.drawShadow(matrixStack, "[" + health + "/" + maxHealth + "]", x + 70, y, ColorUtil.toHex(ChatFormatting.WHITE));
            }

            i++;
        }
    }

}
