package com.voidcivilization.client;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.voidcivilization.data.civilization.Civilization;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class ForceFieldRenderer {

    public static void render(PoseStack matrixStack, float tickDelta) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        if (player == null) {
            return;
        }

        var civ = ClientCivilizationData.getCivilization();
        if (civ.isEmpty()) {
            return;
        }

        renderCivilizationForceField(matrixStack, civ.get());
    }

    private static void renderCivilizationForceField(PoseStack matrixStack, Civilization civilization) {
        int radius = ClientConfigData.getForceFieldRadius();

        if (radius == 0) {
            return;
        }

        if (civilization.getNucleus().isEmpty()) {
            return;
        }

        BlockPos nucleus = civilization.getNucleus().get();
        renderForceField(matrixStack, nucleus, radius);
    }

    private static void renderForceField(PoseStack matrixStack, BlockPos center, int radius) {
        var buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        var vertexConsumer = buffer.getBuffer(RenderType.lines());
        matrixStack.pushPose();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        matrixStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
        Matrix4f mat = matrixStack.last().pose();

        final int verticesPerCircle = 100;
        final int totalCircles = 10;

        for (int j = -(totalCircles / 2); j < (totalCircles / 2); j++) {
            for (int i = 0; i < verticesPerCircle; i++) {
                float val = (float) (i * 2 * Math.PI / verticesPerCircle);
                vertexConsumer
                        .vertex(
                                mat,
                                (float) (center.getX() + radius * Math.cos(val)),
                                (float) (center.getY() + (j * 6f)),
                                (float) (center.getZ() + radius * Math.sin(val))
                        )
                        .color(200, 200, 200, 200)
                        .normal(0, 0, 0)
                        .endVertex();

                val = (float) ((i + 1) * 2 * Math.PI / verticesPerCircle);

                vertexConsumer
                        .vertex(
                                mat,
                                (float) (center.getX() + radius * Math.cos(val)),
                                (float) (center.getY() + (j * 6f)),
                                (float) (center.getZ() + radius * Math.sin(val))
                        )
                        .color(200, 200, 200, 200)
                        .normal(0, 0, 0)
                        .endVertex();
            }
        }

        matrixStack.popPose();
        buffer.endBatch(RenderType.lines());
    }

}
