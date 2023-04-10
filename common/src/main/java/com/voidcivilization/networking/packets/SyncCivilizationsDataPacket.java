package com.voidcivilization.networking.packets;

import com.voidcivilization.client.ClientCivilizationData;
import com.voidcivilization.data.civilization.Civilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncCivilizationsDataPacket {

    private final Map<String, Civilization> civilizations;

    public SyncCivilizationsDataPacket(ServerLevel level) {
        this.civilizations = CivilizationManager.get(level).getCivilizations();
    }

    public SyncCivilizationsDataPacket(FriendlyByteBuf buf) {
        this.civilizations = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            Civilization civilization = new Civilization(buf);
            this.civilizations.put(civilization.getName(), civilization);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.civilizations.size());
        for (Civilization civilization : this.civilizations.values()) {
            civilization.write(buf);
        }
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        NetworkManager.PacketContext context = supplier.get();

        context.queue(() -> {
            ClientCivilizationData.setCivilizations(this.civilizations);
        });
    }

}
