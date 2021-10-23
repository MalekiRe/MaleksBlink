package net.malek.blink.mixin;

import net.malek.blink.ModInit;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method="onPlayerConnected", at = @At("RETURN"))
    public void onPlayerConnected(ServerPlayerEntity player, CallbackInfo ci) {
        ModInit.TIME_LAST_ARMOR_CHECK = 0;
        ModInit.distanceIncreaseAmount = 0;
        ModInit.timeoutMap.put(player.getUuid(), 0.0f);
    }
}
