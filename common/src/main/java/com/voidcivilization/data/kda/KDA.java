package com.voidcivilization.data.kda;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;

public class KDA {

    private int kills;
    private int deaths;
    private int assists;
    private GameProfile gameProfile;

    public KDA(GameProfile gameProfile) {
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        this.gameProfile = gameProfile;
    }

    public KDA(int kills, int deaths, int assists, GameProfile gameProfile) {
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.gameProfile = gameProfile;
    }

    public KDA(CompoundTag compoundTag) {
        this.kills = compoundTag.getInt("kills");
        this.deaths = compoundTag.getInt("deaths");
        this.assists = compoundTag.getInt("assists");
        this.gameProfile = new GameProfile(compoundTag.getUUID("uuid"), compoundTag.getString("name"));
    }

    public CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putInt("kills", kills);
        compoundTag.putInt("deaths", deaths);
        compoundTag.putInt("assists", assists);
        compoundTag.putUUID("uuid", gameProfile.getId());
        compoundTag.putString("name", gameProfile.getName());

        return compoundTag;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }

    public void addAssist() {
        this.assists++;
    }

    public void deductAssist() {
        this.assists--;
    }

    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    @Override
    public String toString() {
        return "" + kills + "/" + deaths + "/" + assists;
    }
}

