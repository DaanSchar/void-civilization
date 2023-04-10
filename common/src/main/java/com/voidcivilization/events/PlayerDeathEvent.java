package com.voidcivilization.events;

import com.voidcivilization.VoidCivilization;
import com.voidcivilization.data.civilization.CivilizationManager;
import com.voidcivilization.data.death.DeathTracker;
import com.voidcivilization.networking.NetworkHandler;
import com.voidcivilization.networking.packets.MemberDeathPacket;
import com.voidcivilization.util.Messenger;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.item.ItemStack;

public class PlayerDeathEvent {

    public static void register() {
        EntityEvent.LIVING_DEATH.register((livingEntity, damageSource) -> {
            if (livingEntity instanceof ServerPlayer player) {
                var civManager = CivilizationManager.get(player.getLevel());
                var civ = civManager.getCivilization(player.getGameProfile());

                if (civ.isEmpty()) {
                    return EventResult.pass();
                }

                NetworkHandler.sendToAllPlayers(player.getLevel(), new MemberDeathPacket(civ.get()));

                civ.get().damage(VoidCivilization.config.damagePerPlayerDeath);
                civManager.setDirty();

                if (damageSource.getEntity() instanceof ServerPlayer killer) {
                    if (!civManager.areInSameCiv(player.getGameProfile(), killer.getGameProfile())) {
                        awardKiller(killer);
                    }
                }

                if (VoidCivilization.config.clearInventoryOnDeath) {
                    clearInventory(player);
                    Messenger.sendMessage(player, "You died! Some of your items have been lost, and your tools have taken a beating.");
                }

                if (VoidCivilization.config.banPlayerOnDeath || civ.get().isDead()) {
                    String banMessage;
                    if (civ.get().isDead()) {
                        banMessage = "Your civilization has fallen. Better luck next time!";
                    } else {
                        banMessage = "You died! You can no longer participate.";
                    }
                    player.connection.disconnect(Component.literal(banMessage));
                    banPlayer(player);
                }

                if (VoidCivilization.config.playerDeathCooldown > 0) {
                    DeathTracker.get(player.getLevel()).addPlayerDeath(player.getGameProfile().getId());
                }
            }

            return EventResult.pass();
        });
    }

    private static void banPlayer(ServerPlayer player) {
        UserBanListEntry entry = new UserBanListEntry(player.getGameProfile(), null, player.getName().getString(), null, "Died during event");
        player.getServer().getPlayerList().getBans().add(entry);
    }

    private static void clearInventory(ServerPlayer player) {
        double chance = VoidCivilization.config.chanceToDropItemOnDeath;

        if (chance <= 0) {
            return;
        }

        player.inventoryMenu.slots.forEach(slot -> {
            if (!slot.hasItem()) {
                return;
            }

            ItemStack item = slot.getItem();

            if (item.isDamageableItem()) {
                int damage = (int) (item.getMaxDamage() * VoidCivilization.config.damageDealtToToolsOnDeath);
                item.hurtAndBreak(damage, player, (playerEntity) -> {
                });
            } else if (Math.random() <= chance) {
                slot.set(ItemStack.EMPTY);
            }
        });
    }

    private static void awardKiller(ServerPlayer killer) {
        killer.giveExperiencePoints(VoidCivilization.config.expAwardedOnPlayerKill);
    }

}
