package com.voidcivilization.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "server")
public class ServerConfig implements ConfigData {

    @Comment("if true, players will be assigned to a civilization when they join for the first time")
    public boolean autoAssignPlayersToCivilization = true;

    @Comment("strategies: round-robin, random")
    public String assignStrategy = "round-robin";

    @Comment("If true, players will be banned when they die")
    public boolean banPlayerOnDeath = false;

    @Comment("time in seconds where players will be put into spectator mode when they die before respawning again")
    public int playerDeathCooldown = 15;

    @Comment("if true, players will respawn at the spawn of their civilization (if it exists)")
    public boolean spawnAtCivilizationSpawn = true;

    @Comment("max health of a civilization")
    public int maxNucleusHealth = 150;

    @Comment("damage dealt to a civilization when a nucleus is broken")
    public int damagePerNucleusBreak = 1;

    @Comment("damage dealt to a civilization when a player dies")
    public int damagePerPlayerDeath = 0;

    @Comment("radius of the protection sphere around a nucleus (in blocks)")
    public int nucleusProtectionRadius = 13;

    @Comment("if true, members of the same civilization can attack each other")
    public boolean allowFriendlyFire = false;

    @Comment("total exp points awarded to a player when they kill another player from a different civilization. " +
            "55 is roughly equivalent to leveling up from level 0 to level 5")
    public int expAwardedOnPlayerKill = 55;

    @Comment("items are deleted when a player dies. No items will be dropped")
    public boolean clearInventoryOnDeath = false;

    @Comment("probability that an item will be deleted on death (0.0 - 1.0)")
    public double chanceToDropItemOnDeath = 0.4;

    @Comment("percentage of max durability that a tool will be damaged on death (0.0 - 1.0)")
    public double damageDealtToToolsOnDeath = 0.333;

    @Comment("if true, players will be able to see each other's chat messages only within a certain radius")
    public boolean localizeChat = true;

    @Comment("distance between two players where they can see each other's chat messages (in blocks)")
    public int chatRadius = 48;
}
