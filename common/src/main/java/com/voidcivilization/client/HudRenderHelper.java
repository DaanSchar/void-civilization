package com.voidcivilization.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.voidcivilization.data.civilization.Civilization;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

public class HudRenderHelper {

    public static void render(PoseStack matrixStack, float tickDelta) {
        Minecraft client = Minecraft.getInstance();
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

            client.font.drawShadow(matrixStack, name, x, y, getColor(civilization.isDead() ? ChatFormatting.DARK_GRAY : color));
            if (!civilization.isDead()) {
                client.font.drawShadow(matrixStack, "[" + health + "/" + maxHealth + "]", x + 70, y, getColor(ChatFormatting.WHITE));
            }

            i++;
        }
    }


    private static int getColor(ChatFormatting formatting) {
        if (formatting == ChatFormatting.BLACK) {
            return 0x000000;
        }
        if (formatting == ChatFormatting.DARK_BLUE) {
            return 0x0000AA;
        }
        if (formatting == ChatFormatting.DARK_GREEN) {
            return 0x00AA00;
        }
        if (formatting == ChatFormatting.DARK_AQUA) {
            return 0x00AAAA;
        }
        if (formatting == ChatFormatting.DARK_RED) {
            return 0xAA0000;
        }
        if (formatting == ChatFormatting.DARK_PURPLE) {
            return 0xAA00AA;
        }
        if (formatting == ChatFormatting.GOLD) {
            return 0xFFAA00;
        }
        if (formatting == ChatFormatting.GRAY) {
            return 0xAAAAAA;
        }
        if (formatting == ChatFormatting.DARK_GRAY) {
            return 0x555555;
        }
        if (formatting == ChatFormatting.BLUE) {
            return 0x5555FF;
        }
        if (formatting == ChatFormatting.GREEN) {
            return 0x55FF55;
        }
        if (formatting == ChatFormatting.AQUA) {
            return 0x55FFFF;
        }
        if (formatting == ChatFormatting.RED) {
            return 0xFF5555;
        }
        if (formatting == ChatFormatting.LIGHT_PURPLE) {
            return 0xFF55FF;
        }
        if (formatting == ChatFormatting.YELLOW) {
            return 0xFFFF55;
        }

        return 0xFFFFFF;
    }

    private static int getShadowColor(ChatFormatting formatting) {
        if (formatting == ChatFormatting.BLACK) {
            return 0x000000;
        }
        if (formatting == ChatFormatting.DARK_BLUE) {
            return 0x01011c;
        }
        if (formatting == ChatFormatting.DARK_GREEN) {
            return 0x001700;
        }
        if (formatting == ChatFormatting.DARK_AQUA) {
            return 0x002424;
        }
        if (formatting == ChatFormatting.DARK_RED) {
            return 0x300000;
        }
        if (formatting == ChatFormatting.DARK_PURPLE) {
            return 0x2b002b;
        }
        if (formatting == ChatFormatting.GOLD) {
            return 0x303000;
        }
        if (formatting == ChatFormatting.GRAY) {
            return 0x292929;
        }
        if (formatting == ChatFormatting.DARK_GRAY) {
            return 0x1c1c1c;
        }
        if (formatting == ChatFormatting.BLUE) {
            return 0x141470;
        }
        if (formatting == ChatFormatting.GREEN) {
            return 0x2AFF2A;
        }
        if (formatting == ChatFormatting.AQUA) {
            return 0x0b400b;
        }
        if (formatting == ChatFormatting.RED) {
            return 0xFF2A2A;
        }
        if (formatting == ChatFormatting.LIGHT_PURPLE) {
            return 0x590e0e;
        }
        if (formatting == ChatFormatting.YELLOW) {
            return 0x4a4a0c;
        }

        return 0x8c8c8c;
    }

}
