package com.voidcivilization.mixin;

import com.voidcivilization.util.DisplayNameDecorator;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ ServerPlayer.class })
public abstract class ServerPlayerMixin {

    @Inject(method = "getTabListDisplayName", at = @At("TAIL"), cancellable = true)
    private void getTabListDisplayName(CallbackInfoReturnable info) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        info.setReturnValue(DisplayNameDecorator.getPlayerDisplayName(player, true));
    }

}
