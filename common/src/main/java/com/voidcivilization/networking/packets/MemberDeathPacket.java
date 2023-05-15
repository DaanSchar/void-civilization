package com.voidcivilization.networking.packets;

import com.mojang.authlib.GameProfile;
import com.voidcivilization.client.data.ClientCivilizationData;
import com.voidcivilization.util.Messenger;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class MemberDeathPacket {

    private final GameProfile player;
    private final GameProfile attacker;

    public MemberDeathPacket(GameProfile player, DamageSource source) {
        this.player = player;

        if (source.getEntity() instanceof Player attacker) {
            this.attacker = attacker.getGameProfile();
        } else {
            this.attacker = null;
        }
    }

    public MemberDeathPacket(FriendlyByteBuf buf) {
        this.player = buf.readGameProfile();
        if (buf.readBoolean()) {
            this.attacker = buf.readGameProfile();
        } else {
            this.attacker = null;
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeGameProfile(this.player);

        boolean isNull = this.attacker == null;

        buf.writeBoolean(!isNull);
        if (!isNull) {
            buf.writeGameProfile(this.attacker);
        }
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();

        context.queue(() -> {
            Player player = supplier.get().getPlayer();

            var civilization = ClientCivilizationData.getCivilization();

            if (civilization.isEmpty()) {
                return;
            }

            var civOfPlayer = ClientCivilizationData.getCivilization(this.player);
            var civOfAttacker = ClientCivilizationData.getCivilization(this.attacker);

            if (civOfPlayer.isEmpty()) {
                return;
            }

            if (civOfPlayer.get().equals(civilization.get())) {
                player.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.1F, 1.0F);
                Messenger.sendClientError(player, "A member has died!");
            }

            if (civOfAttacker.isEmpty()) {
                return;
            }

            if (attacker.getId().equals(player.getGameProfile().getId())) {
                player.playSound(SoundEvents.PLAYER_LEVELUP, 0.4F, 1.0F);
            }

        });
    }

}
