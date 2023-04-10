package com.voidcivilization.data.civilization;

import com.mojang.authlib.GameProfile;
import com.voidcivilization.networking.NetworkHandler;
import com.voidcivilization.networking.packets.SyncCivilizationsDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

public class CivilizationManager extends SavedData {

    private final HashMap<String, Civilization> civilizations = new HashMap<>();

    public CivilizationManager() {
        super();
    }

    @Nonnull
    public static CivilizationManager get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            DimensionDataStorage storage = serverLevel.getDataStorage();
            return storage.computeIfAbsent(CivilizationManager::new, CivilizationManager::new, "civilizationmanager");
        }

        throw new RuntimeException("Can't access data from client side");
    }

    public Optional<Civilization> getCivilization(String name) {
        return Optional.ofNullable(civilizations.get(name));
    }

    public Optional<Civilization> getCivilization(GameProfile profile) {
        return civilizations.values().stream()
                .filter(civilization -> civilization.getMembers().contains(profile))
                .findFirst();
    }

    public Optional<Civilization> getCivilizationByNucleus(BlockPos pos) {
        return civilizations.values().stream()
                .filter(civilization -> civilization.getNucleus().filter(pos::equals).isPresent())
                .findFirst();
    }

    public CivilizationManager(CompoundTag savedTag) {
        ListTag civilizationTagList = savedTag.getList("civilizations", Tag.TAG_LIST);

        civilizationTagList.forEach(tag -> {
            Civilization civilization = CivilizationNBT.load((ListTag) tag);
            civilizations.put(civilization.getName(), civilization);
        });
    }

    public boolean isInCivilization(GameProfile profile) {
        return getCivilization(profile).isPresent();
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();

        civilizations.forEach((name, kingdom) -> {
            listTag.add(CivilizationNBT.save(kingdom));
        });

        compoundTag.put("civilizations", listTag);
        return compoundTag;
    }

    public void createCivilization(String name) {
        civilizations.put(name, new Civilization(name));
        setDirty();
    }

    public Map<String, Civilization> getCivilizations() {
        return civilizations;
    }

    public Optional<BlockPos> getNucleusInRange(BlockPos pos, float range) {
        return civilizations.values()
                .stream()
                .map(Civilization::getNucleus)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(nucleus -> planeDistance(pos, nucleus) < range)
                .findFirst();
    }

    public boolean removeCivilization(String name) {
        Optional<String> civName = civilizations.values().stream()
                .map(Civilization::getName)
                .filter(name::equalsIgnoreCase)
                .findFirst();

        if (civName.isEmpty()) {
            return false;
        }

        setDirty();
        return civilizations.remove(civName.get()) != null;
    }

    public boolean areInSameCiv(GameProfile... players) {
        if (players.length < 2) {
            return true;
        }

        Optional<Civilization> firstCiv = getCivilization(players[0]);
        if (firstCiv.isEmpty()) {
            return false;
        }

        for (int i = 1; i < players.length; i++) {
            Optional<Civilization> civ = getCivilization(players[i]);
            if (civ.isEmpty() || !civ.get().equals(firstCiv.get())) {
                return false;
            }
        }

        return true;
    }

    public void updatePlayerDisplayName(ServerPlayer player) {
        ClientboundPlayerInfoPacket packet = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_DISPLAY_NAME, player);
        player.getLevel().players().forEach(p -> p.connection.send(packet));
    }

    public void syncClientCivilizationData(ServerLevel level) {
        NetworkHandler.sendToAllPlayers(level, new SyncCivilizationsDataPacket(level));
    }

    private float planeDistance(BlockPos a, BlockPos b) {
        return (float) Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
    }
}
