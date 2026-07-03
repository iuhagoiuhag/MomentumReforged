package com.momentum.mixin;

import com.momentum.Momentum;
import com.momentum.config.MomentumConfig;
import com.momentum.engine.BhopEngine;
import com.momentum.engine.MomentumPlayerData;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Unique
    private static final Map<UUID, MomentumPlayerData> momentum$playerDataMap = new ConcurrentHashMap<>();

    @Unique
    private MomentumPlayerData momentum$getPlayerData() {
        ServerPlayer player = ((ServerGamePacketListenerImpl) (Object) this).getPlayer();
        return momentum$playerDataMap.computeIfAbsent(player.getUUID(),
            k -> new MomentumPlayerData(player));
    }

    @Inject(method = "handlePlayerInput", at = @At("HEAD"))
    private void momentumCaptureInput(ServerboundPlayerInputPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl self = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = self.getPlayer();

        MomentumConfig config = Momentum.getConfig();
        if (!config.isEnabled() || !config.isBhopEnabled()) return;

        Input input = packet.input();
        MomentumPlayerData data = momentum$getPlayerData();
        BhopEngine engine = data.getBhopEngine();

        double leftRight = 0;
        if (input.left()) leftRight += 1;
        if (input.right()) leftRight -= 1;

        double forwardBack = 0;
        if (input.forward()) forwardBack += 1;
        if (input.backward()) forwardBack -= 1;

        engine.setInput(leftRight, forwardBack);

        if (input.jump()) {
            engine.onJump();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void momentumTick(CallbackInfo ci) {
        ServerGamePacketListenerImpl self = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = self.getPlayer();

        MomentumConfig config = Momentum.getConfig();
        if (!config.isEnabled() || !config.isBhopEnabled()) return;

        MomentumPlayerData data = momentum$getPlayerData();
        data.tick();

        BhopEngine engine = data.getBhopEngine();
        Vec3 input = engine.getLastInput();

        if (input != null && (input.x != 0 || input.z != 0)) {
            Vec3 velocity = player.getDeltaMovement();
            Vec3 newVelocity = engine.processMovement(
                input,
                player.getYRot(),
                player.onGround(),
                player.isJumping(),
                player.isSprinting()
            );

            if (newVelocity != null) {
                player.setDeltaMovement(newVelocity);
            }
        }
    }
}
