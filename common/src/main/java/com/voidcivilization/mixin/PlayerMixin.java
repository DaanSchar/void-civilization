package com.voidcivilization.mixin;

import com.voidcivilization.util.DisplayNameDecorator;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public class PlayerMixin {

    @Inject(method = "getDisplayName", at = @At("TAIL"), cancellable = true)
    public void getDisplayName(CallbackInfoReturnable info) {
        Player player = (Player) (Object) this;
        info.setReturnValue(DisplayNameDecorator.getPlayerDisplayName(player, false));
    }

}
