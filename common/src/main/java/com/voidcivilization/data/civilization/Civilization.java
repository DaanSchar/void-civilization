package com.voidcivilization.data.civilization;

import com.mojang.authlib.GameProfile;
import com.voidcivilization.VoidCivilization;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class Civilization {

    private final UUID id;
    private final String name;
    private final Set<GameProfile> members;

    private int health;
    private BlockPos spawn;
    private BlockPos nucleus;
    private ChatFormatting color;

    public Civilization(String name) {
        this(name, ChatFormatting.WHITE, new HashSet<>());
    }

    public Civilization(String name, ChatFormatting color, Set<GameProfile> members) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.color = color;
        this.members = members;
        this.health = VoidCivilization.config.maxNucleusHealth;
    }

    public void teleportMembersToSpawn(ServerLevel level) {
        if (getSpawn().isEmpty()) {
            return;
        }

        BlockPos spawn = this.getSpawn().get();

        for (GameProfile member : this.getMembers()) {
            Player player = level.getPlayerByUUID(member.getId());

            if (player != null) {
                player.teleportTo(spawn.getX() + 0.5d, spawn.getY(), spawn.getZ() + 0.5d);
            }
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public void setColor(ChatFormatting color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Set<GameProfile> getMembers() {
        return members;
    }

    public List<ServerPlayer> getMembers(ServerLevel level) {
        return level.getPlayers(player -> this.members.contains(player.getGameProfile()));
    }

    public boolean addMember(GameProfile member) {
        return members.add(member);
    }

    public boolean removeMember(GameProfile member) {
        return members.remove(member);
    }

    public boolean memberExists(GameProfile member) {
        return members.contains(member);
    }

    public void setSpawn(BlockPos blockPosition) {
        this.spawn = blockPosition;
    }

    public Optional<BlockPos> getSpawn() {
        return Optional.ofNullable(spawn);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setNucleus(BlockPos pos) {
        this.nucleus = pos;
    }

    public Optional<BlockPos> getNucleus() {
        return Optional.ofNullable(nucleus);
    }

    public UUID getId() {
        return id;
    }

    public boolean hasSpawn() {
        return spawn != null;
    }

    public void damage(int damage) {
        this.health -= damage;

        if (this.health < 0) {
            this.health = 0;
        }
    }

    /**
     * Encode the data to a {@link FriendlyByteBuf}
     * @param buf The buffer to write to
     */
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.id);
        buf.writeUtf(this.name);
        buf.writeInt(this.health);
        buf.writeUtf(this.color.getName());
        buf.writeBoolean(this.spawn != null);
        if (this.spawn != null) {
            buf.writeBlockPos(this.spawn);
        }
        buf.writeBoolean(this.nucleus != null);
        if (this.nucleus != null) {
            buf.writeBlockPos(this.nucleus);
        }
        buf.writeInt(this.members.size());
        for (GameProfile member : this.members) {
            buf.writeUtf(member.getName());
            buf.writeUtf(member.getId().toString());
        }
    }

    /**
     * Decode the data from a {@link FriendlyByteBuf}
     * @param buf The buffer to read from
     */
    public Civilization(FriendlyByteBuf buf) {
        this.id = buf.readUUID();
        this.name = buf.readUtf();
        this.health = buf.readInt();
        this.color = ChatFormatting.getByName(buf.readUtf());
        if (buf.readBoolean()) {
            this.spawn = buf.readBlockPos();
        }
        if (buf.readBoolean()) {
            this.nucleus = buf.readBlockPos();
        }
        this.members = new HashSet<>();
        int memberCount = buf.readInt();
        for (int i = 0; i < memberCount; i++) {
            String name = buf.readUtf();
            UUID id = UUID.fromString(buf.readUtf());
            this.members.add(new GameProfile(id, name));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Civilization other) {
            return this.id.equals(other.id);
        }

        return false;
    }

    public static class Builder {
        private final String name;
        private ChatFormatting color;
        private Set<GameProfile> members;
        private int lives;
        private BlockPos spawn;
        private BlockPos nucleus;

        public Builder(String name) {
            this.name = name;
        }

        public Builder color(ChatFormatting color) {
            this.color = color;
            return this;
        }

        public Builder members(Set<GameProfile> members) {
            this.members = members;
            return this;
        }

        public Builder lives(int lives) {
            this.lives = lives;
            return this;
        }

        public Builder spawn(BlockPos spawn) {
            this.spawn = spawn;
            return this;
        }

        public Builder nucleus(BlockPos nucleus) {
            this.nucleus = nucleus;
            return this;
        }

        public Civilization build() {
            Civilization civ = new Civilization(name, color, members);
            civ.setHealth(lives);
            civ.setSpawn(spawn);
            civ.setNucleus(nucleus);
            return civ;
        }
    }

}
