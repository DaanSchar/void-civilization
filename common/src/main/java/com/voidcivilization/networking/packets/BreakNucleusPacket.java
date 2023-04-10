package com.voidcivilization.networking.packets;

import com.voidcivilization.data.civilization.Civilization;
import com.voidcivilization.client.ClientCivilizationData;
import com.voidcivilization.util.Messenger;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.function.Supplier;

public class BreakNucleusPacket {

    private final Civilization attackingCivilization;
    private final Civilization attackedCivilization;

    public BreakNucleusPacket(Civilization attackedCivilization, Civilization attackingCivilization) {
        this.attackingCivilization = attackingCivilization;
        this.attackedCivilization = attackedCivilization;
    }

    public BreakNucleusPacket(FriendlyByteBuf buf) {
        this.attackingCivilization = new Civilization(buf);
        this.attackedCivilization = new Civilization(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        this.attackingCivilization.write(buf);
        this.attackedCivilization.write(buf);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();

        context.queue(() -> {
            Player player = supplier.get().getPlayer();
            Level level = player.getLevel();

            if (attackedCivilization.isDead()) {
                handleCivDeath(player, level);
                return;
            }

            if (attackedCivilization.getHealth() % 5 == 0) {
                Messenger.sendMessage(player, this.attackedCivilization.getName() + " is under attack! [ " + this.attackedCivilization.getHealth() + " ]");
            }
            spawnBreakParticles(level, attackedCivilization.getNucleus().get());

            var playerCivilization = ClientCivilizationData.getCivilization();

            if (playerCivilization.isEmpty()) {
                return;
            }

            if (playerCivilization.get().equals(this.attackedCivilization)) {
                player.playSound(SoundEvents.ANVIL_PLACE, 0.7F, 1.0F);
                Messenger.sendClientError(player, "Your civilization is under attack! " +  "( " + this.attackedCivilization.getHealth() + " )");
            }

            if (playerCivilization.get().equals(this.attackingCivilization)) {
                player.playSound(SoundEvents.AMETHYST_CLUSTER_BREAK, 1.0F, 1.0F);
                Messenger.sendClientSuccess(player, "Attacking " + this.attackedCivilization.getName() + " ( " + this.attackedCivilization.getHealth() + " )");
            }

        });
    }

    private void handleCivDeath(Player player, Level level) {
        Messenger.sendMessage(player, this.attackedCivilization.getName() + " has been destroyed by " + this.attackingCivilization.getName() + "!");
        player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 1.0F);
        BlockPos nucleus = attackedCivilization.getNucleus().get();
        spawnBreakParticles(level, nucleus);
        level.explode(null, nucleus.getX(), nucleus.getY(), nucleus.getZ(), 3, true, Explosion.BlockInteraction.NONE);

        var playerCivilization = ClientCivilizationData.getCivilization();

        if (playerCivilization.isEmpty()) {
            return;
        }

        if (playerCivilization.get().equals(this.attackedCivilization)) {
            Messenger.sendClientError(player, "Your civilization has been destroyed !");
        }

        if (playerCivilization.get().equals(this.attackingCivilization)) {
            Messenger.sendClientSuccess(player, "You have destroyed " + this.attackedCivilization.getName() + "!");
            level.createFireworks(nucleus.getX(), nucleus.getY(), nucleus.getZ(), 0, 0, 0, null);
        }
    }

    private void spawnBreakParticles(Level level, BlockPos position) {
        for (int i = 0; i < 360; i++) {
            double distanceFromCenter = 3;

            if (i % 20 == 0) {
                level.addParticle(
                        ParticleTypes.ELECTRIC_SPARK,
                        position.getX() + 0.5d + rand() * distanceFromCenter,
                        position.getY() + 0.5d + rand() * distanceFromCenter,
                        position.getZ() + 0.5d + rand() * distanceFromCenter,
                        Math.cos(i) * (0.1d + rand() / 3d),
                        0.1 + rand() / 2.0,
                        Math.sin(i) * (0.1d + rand() / 3d)
                );
            }

            distanceFromCenter = 3;
            if (i % 30 == 0) {
                level.addParticle(
                        ParticleTypes.FIREWORK,
                        position.getX() + 0.5d + rand() * distanceFromCenter,
                        position.getY() + 0.5d + rand() * distanceFromCenter,
                        position.getZ() + 0.5d + rand() * distanceFromCenter,
                        Math.cos(i) * (0.1d + rand() / 3d),
                        0.1 + rand() / 2.0,
                        Math.sin(i) * (0.1d + rand() / 3d)
                );
            }
        }
    }

    private double rand() {
        Random rand = new Random();
        return (rand.nextDouble() * 2 - 1) / 3.0;
    }
}
