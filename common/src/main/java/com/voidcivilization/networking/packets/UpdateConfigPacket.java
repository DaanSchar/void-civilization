package com.voidcivilization.networking.packets;

import com.voidcivilization.client.data.ClientConfigData;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class UpdateConfigPacket {

    private final int nucleusProtectionRadius;
    private final int forceFieldRadius;
    private final int maxNucleusHealth;

    public UpdateConfigPacket(int nucleusProtectionRadius, int forceFieldRadius, int maxNucleusHealth) {
        this.nucleusProtectionRadius = nucleusProtectionRadius;
        this.forceFieldRadius = forceFieldRadius;
        this.maxNucleusHealth = maxNucleusHealth;
    }

    public UpdateConfigPacket(FriendlyByteBuf buf) {
        this.nucleusProtectionRadius = buf.readInt();
        this.forceFieldRadius = buf.readInt();
        this.maxNucleusHealth = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.nucleusProtectionRadius);
        buf.writeInt(this.forceFieldRadius);
        buf.writeInt(this.maxNucleusHealth);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();

        context.queue(() -> {
            ClientConfigData.setNucleusProtectionRadius(this.nucleusProtectionRadius);
            ClientConfigData.setForceFieldRadius(this.forceFieldRadius);
            ClientConfigData.setMaxNucleusHealth(this.maxNucleusHealth);
        });
    }

}
