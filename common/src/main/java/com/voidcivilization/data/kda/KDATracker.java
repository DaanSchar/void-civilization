package com.voidcivilization.data.kda;

import com.mojang.authlib.GameProfile;
import com.voidcivilization.networking.NetworkHandler;
import com.voidcivilization.networking.packets.UpdateKDAPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;
import java.util.*;

public class KDATracker extends SavedData {

    private final int TICKS_TILL_ASSIST_EXPIRE = 20 * 10;
    private final Map<UUID, KDA> kdas = new HashMap<>();
    private final Map<UUID, Map<UUID, Integer>> assistTracker = new HashMap<>();

    public KDATracker() {
        super();
    }

    @Nonnull
    public static KDATracker get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            DimensionDataStorage storage = serverLevel.getDataStorage();
            return storage.computeIfAbsent(KDATracker::new, KDATracker::new, "kdatracker");
        }

        throw new RuntimeException("Can't access data from client side");
    }

    public KDATracker(CompoundTag savedTag) {
        ListTag kdaTagList = savedTag.getList("kda", Tag.TAG_COMPOUND);

        kdaTagList.forEach(tag -> {
            CompoundTag compoundTag = (CompoundTag) tag;
            UUID id = compoundTag.getUUID("uuid");
            KDA kda = new KDA(compoundTag.getCompound("kda"));
            kdas.put(id, kda);
        });
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();

        kdas.forEach((uuid, kda) -> {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("uuid", uuid);
            tag.put("kda", kda.save());
            listTag.add(tag);
        });

        compoundTag.put("kda", listTag);
        return compoundTag;
    }

    public KDA getKDA(GameProfile profile) {
        return kdas.getOrDefault(profile.getId(), new KDA(profile));
    }

    public void addKill(ServerPlayer player) {
        KDA kda = getKDA(player.getGameProfile());
        kda.addKill();

        // player gets assist for killing someone, so deduct one assist
        kda.deductAssist();

        kdas.put(player.getUUID(), kda);
        setDirty();
        ServerLevel level = player.getLevel();
        NetworkHandler.sendToAllPlayers(level, new UpdateKDAPacket(level));
    }

    public Map<UUID, KDA> getKDAS() {
        return this.kdas;
    }

    public void playerWasHit(UUID player, UUID attacker) {
        Map<UUID, Integer> assists = assistTracker.getOrDefault(player, new HashMap<>());
        assists.put(attacker, TICKS_TILL_ASSIST_EXPIRE);
        assistTracker.put(player, assists);
    }

    public void update() {
        List<UUID> playersToRemove = new ArrayList<>();

        for (Map.Entry<UUID, Map<UUID, Integer>> trackerEntry : assistTracker.entrySet()) {
            UUID player = trackerEntry.getKey();
            Map<UUID, Integer> assists = trackerEntry.getValue();

            List<UUID> expiredAssists = new ArrayList<>();

            for (Map.Entry<UUID, Integer> assistEntry : assists.entrySet()) {
                int ticks = assistEntry.getValue();
                UUID attacker = assistEntry.getKey();

                if (ticks <= 0) {
                    expiredAssists.add(attacker);
                } else {
                    assists.put(attacker, assistEntry.getValue() - 1);
                }
            }

            expiredAssists.forEach(assists::remove);

            if (assists.isEmpty()) {
                playersToRemove.add(player);
            }
        }

        playersToRemove.forEach(assistTracker::remove);
    }

    public void handleDeath(ServerPlayer player) {
        addDeath(player.getGameProfile());
        Map<UUID, Integer> assists = assistTracker.getOrDefault(player.getUUID(), new HashMap<>());
        assists.forEach((attacker, ticks) -> addAssist(Objects.requireNonNull(player.getLevel().getPlayerByUUID(attacker)).getGameProfile()));
        setDirty();

        ServerLevel level = player.getLevel();
        NetworkHandler.sendToAllPlayers(level, new UpdateKDAPacket(level));
    }

    private void addDeath(GameProfile profile) {
        KDA kda = getKDA(profile);
        kda.addDeath();
        kdas.put(profile.getId(), kda);
    }

    private void addAssist(GameProfile profile) {
        KDA kda = getKDA(profile);
        kda.addAssist();
        kdas.put(profile.getId(), kda);
    }
}
