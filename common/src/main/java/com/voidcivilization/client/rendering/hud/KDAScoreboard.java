package com.voidcivilization.client.rendering.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.voidcivilization.client.data.ClientCivilizationData;
import com.voidcivilization.client.data.ClientKDAData;
import com.voidcivilization.client.rendering.ColorUtil;
import com.voidcivilization.data.kda.KDA;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KDAScoreboard {

    private static final int x = 10;

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

        KDA playerKDA = ClientKDAData.getKda().orElse(new KDA(player.getGameProfile()));

        int y = 10;

        client.font.drawShadow(matrixStack, "KDA: " + playerKDA + "", x, y, ColorUtil.toHex(ChatFormatting.WHITE));

        var kdas = ClientKDAData.getKdas();
        if (kdas.isEmpty()) {
            return;
        }

        drawTop3Players(kdas, matrixStack);
    }

    private static void drawTop3Players(Map<UUID, KDA> kdas, PoseStack matrixStack) {
        Minecraft client = Minecraft.getInstance();
        List<UUID> sortedKeys = kdas.keySet()
                .stream()
                .sorted(Comparator.comparingInt(k -> kdas.get(k).getKills()).reversed())
                .limit(3)
                .toList();

        int y = 10;

        for (UUID id : sortedKeys) {
            KDA kda = kdas.get(id);

            if (client.level == null) {
                continue;
            }

            y += 10;
            client.font.drawShadow(matrixStack,  kda.getGameProfile().getName() + ": " + kda + "", x, y, ColorUtil.toHex(ChatFormatting.WHITE));
        }
    }

}
