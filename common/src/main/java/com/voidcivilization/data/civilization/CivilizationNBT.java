package com.voidcivilization.data.civilization;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.HashSet;
import java.util.Set;

public class CivilizationNBT {

    public static ListTag save(Civilization civilization) {
        ListTag listTag = new ListTag();

        CompoundTag infoTag = new CompoundTag();
        infoTag.putString("name", civilization.getName());
        infoTag.putString("color", civilization.getColor().getName());
        infoTag.putInt("lives", civilization.getHealth());

        var nucleus = civilization.getNucleus();
        infoTag.putBoolean("hasNucleus", nucleus.isPresent());

        if (nucleus.isPresent()) {
            infoTag.putInt("nucleusX", nucleus.get().getX());
            infoTag.putInt("nucleusY", nucleus.get().getY());
            infoTag.putInt("nucleusZ", nucleus.get().getZ());
        }

        var spawn = civilization.getSpawn();
        infoTag.putBoolean("hasSpawn", spawn.isPresent());

        if (spawn.isPresent()) {
            infoTag.putInt("spawnX", spawn.get().getX());
            infoTag.putInt("spawnY", spawn.get().getY());
            infoTag.putInt("spawnZ", spawn.get().getZ());
        }

        listTag.add(infoTag);
        listTag.addAll(saveMembers(civilization));

        return listTag;
    }

    public static Civilization load(ListTag listTag) {
        CompoundTag infoTag = (CompoundTag) listTag.get(0);
        Civilization.Builder civBuilder = new Civilization.Builder(infoTag.getString("name"))
                .color(ChatFormatting.getByName(infoTag.getString("color")))
                .lives(infoTag.getInt("lives"))
                .members(loadMembers(listTag));

        if (infoTag.getBoolean("hasNucleus")) {
            int x = infoTag.getInt("nucleusX");
            int y = infoTag.getInt("nucleusY");
            int z = infoTag.getInt("nucleusZ");

            civBuilder.nucleus(new BlockPos(x, y, z));
        }

        if (infoTag.getBoolean("hasSpawn")) {
            int x = infoTag.getInt("spawnX");
            int y = infoTag.getInt("spawnY");
            int z = infoTag.getInt("spawnZ");

            civBuilder.spawn(new BlockPos(x, y, z));
        }

        return civBuilder.build();
    }

    public static ListTag saveMembers(Civilization civilization) {
        ListTag listTag = new ListTag();

        for (GameProfile member : civilization.getMembers()) {
            CompoundTag memberTag = new CompoundTag();
            memberTag.putString("name", member.getName());
            memberTag.putUUID("uuid", member.getId());

            listTag.add(memberTag);
        }

        return listTag;
    }

    private static Set<GameProfile> loadMembers(ListTag tag) {
        Set<GameProfile> members = new HashSet<>();

        for (int i = 1; i < tag.size(); i++) {
            CompoundTag memberTag = (CompoundTag) tag.get(i);
            members.add(
                    new GameProfile(
                            memberTag.getUUID("uuid"),
                            memberTag.getString("name")
                    )
            );
        }

        return members;
    }
}
