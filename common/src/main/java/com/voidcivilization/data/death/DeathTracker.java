package com.voidcivilization.data.death;

import com.voidcivilization.VoidCivilization;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DeathTracker extends SavedData {

    private final HashMap<UUID, Integer> deathTimers = new HashMap<>();

    public DeathTracker() {
        super();
    }

    @Nonnull
    public static DeathTracker get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            DimensionDataStorage storage = serverLevel.getDataStorage();
            return storage.computeIfAbsent(DeathTracker::new, DeathTracker::new, "deathtracker");
        }

        throw new RuntimeException("Can't access data from client side");
    }

    public DeathTracker(CompoundTag savedTag) {
        ListTag deathTimersTagList = savedTag.getList("deathTimers", Tag.TAG_COMPOUND);

        deathTimersTagList.forEach(tag -> {
            CompoundTag compoundTag = (CompoundTag) tag;
            UUID id = compoundTag.getUUID("uuid");
            int remainingTicks = compoundTag.getInt("remainingTicks");
            deathTimers.put(id, remainingTicks);
        });
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();

        deathTimers.forEach((uuid, remainingTicks) -> {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("uuid", uuid);
            tag.putInt("remainingTicks", remainingTicks);
            listTag.add(tag);
        });

        compoundTag.put("deathTimers", listTag);
        return compoundTag;
    }

    public void update() {
        deathTimers.forEach((gameProfile, remainingTicks) -> {
            if (remainingTicks > 0) {
                deathTimers.put(gameProfile, remainingTicks - 1);
            }
        });
        setDirty();
    }

    public void addPlayerDeath(UUID playerId) {
        final int ticksPerSecond = 20;
        deathTimers.put(playerId, VoidCivilization.config.playerDeathCooldown * ticksPerSecond);
        setDirty();
    }

    public void removePlayerDeath(UUID playerId) {
        deathTimers.remove(playerId);
        setDirty();
    }

    public HashMap<UUID, Integer> getDeathTimers() {
        return deathTimers;
    }

    public List<UUID> getPlayersToBeRevived() {
        return deathTimers.entrySet().stream()
                .filter(entry -> entry.getValue() <= 0)
                .map(HashMap.Entry::getKey)
                .toList();
    }

    public boolean isPlayerDead(UUID id) {
        return deathTimers.containsKey(id) && deathTimers.get(id) > 0;
    }

    public int getDeathTimer(UUID id) {
        return deathTimers.get(id);
    }
}
