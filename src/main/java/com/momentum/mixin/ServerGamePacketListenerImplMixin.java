package com.momentum.mixin;

import com.momentum.Momentum;
import com.momentum.config.MomentumConfig;
import com.momentum.engine.MomentumPlayerData;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public abstract ServerPlayer getPlayer();

    @Inject(method = "handlePlayerInput", at = @At("TAIL"))
    private void onHandlePlayerInput(ServerboundPlayerInputPacket packet, CallbackInfo ci) {
        ServerPlayer player = getPlayer();
        MomentumConfig config = Momentum.getConfig();
        if (!config.isEnabled() || !config.isBhopEnabled()) return;

        Input input = packet.input();
        MomentumPlayerData data = Momentum.getPlayerData(player);
        data.setLastInput(input);

        float forward = 0;
        if (input.forward()) forward += 1;
        if (input.backward()) forward -= 1;

        float strafe = 0;
        if (input.left()) strafe += 1;
        if (input.right()) strafe -= 1;

        player.zza = forward;
        player.xxa = strafe;
    }
}
