package com.voidcivilization.registry.blocks.custom.entity;

import com.voidcivilization.client.data.ClientConfigData;
import com.voidcivilization.registry.blocks.VCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import java.util.Random;

public class NucleusBlockEntity extends BlockEntity {

    private int tick;

    public NucleusBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(VCBlockEntities.NUCLEUS_BLOCK_ENTITY.get(), blockPos, blockState);
        this.tick = 0;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, NucleusBlockEntity entity) {
        if (!level.isClientSide) {
            return;
        }

        Random random = new Random();

        entity.setTick(entity.getTick() + 1);


        int duration = (int) (random.nextDouble() * 2) + 1;

        if (entity.getTick() % duration != 0) {
            return;
        }

        entity.setTick(0);

        spawnParticles(ClientConfigData.getNucleusProtectionRadius(), level, blockPos);
        spawnParticles(ClientConfigData.getForceFieldRadius(), level, blockPos);
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    private static void spawnParticles(int range, Level level, BlockPos blockPos) {
        Random random = new Random();

        SimpleParticleType[] particles = new SimpleParticleType[]{
                ParticleTypes.END_ROD,
                ParticleTypes.END_ROD,
                ParticleTypes.FIREWORK,
        };

        // as the range increase, we need to spawn more particles relative to the surface of the cylinder
        int cylinderSurface = (int) (2 * Math.PI * Math.pow(range/10f, 2));

        for (int i = 0; i < cylinderSurface / 10f; i++) {
            for (SimpleParticleType type : particles) {

                Vec2 randomDirection2D = new Vec2(
                        (float)(random.nextDouble() * 2) - 1,
                        (float)(random.nextDouble() * 2) - 1
                ).normalized();
                Vec2 randomOffset2D = randomDirection2D.scale((float)getOffsetedRange(range, 1));
                double randomYPosition = random.nextGaussian(-Math.sqrt(range/2f), Math.sqrt(range/2f) * 2);
                level.addParticle(
                        type,
                        blockPos.getX() + 0.5d + randomOffset2D.x,
                        blockPos.getY() + 1.5d + randomYPosition,
                        blockPos.getZ() + 0.5d + randomOffset2D.y,
                        0,
                        -0.02f,
                        0
                );
            }
        }
    }

    private static double getOffsetedRange(double range, double stdev) {
        return range + new Random().nextGaussian(0, stdev);
    }
}
