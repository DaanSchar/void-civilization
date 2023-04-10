package com.voidcivilization.data.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public class PlayerManager extends SavedData {

    /**
     * Map of players that have joined the server before
     *
     * Key: UUID of the player
     * Value: if it's the first time the player has joined the server
     */
    private static final Map<UUID, Boolean> joinedPlayers = new HashMap<>();

    public boolean isPersisted(UUID uuid) {
        return joinedPlayers.containsKey(uuid);
    }

    public boolean isFirstJoin(UUID uuid) {
        if (isPersisted(uuid)) {
            return joinedPlayers.get(uuid);
        }

        return true;
    }

    public void join(UUID uuid) {
        if (!isFirstJoin(uuid)) {
            return;
        }

        joinedPlayers.put(uuid, !isPersisted(uuid));
        setDirty();
    }

    public PlayerManager() {
        super();
    }

    public PlayerManager(CompoundTag tag) {
        ListTag listTag = tag.getList("joinedPlayers", Tag.TAG_LIST);

        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag playerTag = listTag.getCompound(i);

            joinedPlayers.put(
                    playerTag.getUUID("uuid"),
                    playerTag.getBoolean("firstJoin")
            );
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        ListTag listTag = new ListTag();

        for (var entry : joinedPlayers.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("uuid", entry.getKey());
            playerTag.putBoolean("firstJoin", entry.getValue());
            listTag.add(playerTag);
        }

        compoundTag.put("joinedPlayers", listTag);

        return compoundTag;
    }

    @Nonnull
    public static PlayerManager get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            DimensionDataStorage storage = serverLevel.getDataStorage();
            return storage.computeIfAbsent(PlayerManager::new, PlayerManager::new, "playerfirstjoinmanager");
        }

        throw new RuntimeException("Can't access data from client side");
    }
}
