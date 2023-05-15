package com.voidcivilization.networking.packets;

import com.voidcivilization.client.data.ClientKDAData;
import com.voidcivilization.data.kda.KDA;
import com.voidcivilization.data.kda.KDATracker;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class UpdateKDAPacket {

    private Map<UUID, KDA> kdas = new HashMap<>();

    public UpdateKDAPacket(ServerLevel level) {
        KDATracker tracker = KDATracker.get(level);
        this.kdas = tracker.getKDAS();
    }

    public UpdateKDAPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            kdas.put(buf.readUUID(), new KDA(buf.readInt(), buf.readInt(), buf.readInt(), buf.readGameProfile()));
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(kdas.size());
        for (Map.Entry<UUID, KDA> entry : kdas.entrySet()) {
            buf.writeUUID(entry.getKey());
            buf.writeInt(entry.getValue().getKills());
            buf.writeInt(entry.getValue().getDeaths());
            buf.writeInt(entry.getValue().getAssists());
            buf.writeGameProfile(entry.getValue().getGameProfile());
        }
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();

        context.queue(() -> {
            ClientKDAData.setKda((HashMap<UUID, KDA>) kdas);
        });
    }

}
