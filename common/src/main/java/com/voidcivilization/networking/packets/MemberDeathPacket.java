package com.voidcivilization.networking.packets;

import com.voidcivilization.data.civilization.Civilization;
import com.voidcivilization.client.ClientCivilizationData;
import com.voidcivilization.util.Messenger;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class MemberDeathPacket {

    private final Civilization civilization;

    public MemberDeathPacket(Civilization civilization) {
        this.civilization = civilization;
    }

    public MemberDeathPacket(FriendlyByteBuf buf) {
        this.civilization = new Civilization(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        this.civilization.write(buf);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();

        context.queue(() -> {
            Player player = supplier.get().getPlayer();

            var civilization = ClientCivilizationData.getCivilization();

            if (civilization.isEmpty()) {
                return;
            }

            if (civilization.get().getId().equals(this.civilization.getId())) {
                player.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.1F, 1.0F);
                Messenger.sendClientError(player, "A member has died!");
            }
        });
    }

}
