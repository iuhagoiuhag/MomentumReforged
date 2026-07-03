package com.momentum.mixin;

import com.momentum.Momentum;
import com.momentum.config.MomentumConfig;
import com.momentum.engine.MomentumPlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow
    private int noJumpDelay;

    @Shadow
    private boolean jumping;

    private boolean momentum$wasJumping;

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(Vec3 travelVector, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return;
        if (player.isPassenger() || player.isSpectator()) return;
        if (player.getAbilities().flying) return;
        if (player.isFallFlying()) return;
        if (player.isInWater() || player.isInLava()) return;
        if (player.onClimbable()) return;

        MomentumConfig config = Momentum.getConfig();
        if (!config.isEnabled()) return;

        float forward;
        float strafe;
        boolean jump;

        if (player instanceof ServerPlayer serverPlayer) {
            MomentumPlayerData data = Momentum.getPlayerData(serverPlayer);
            Input input = data.getLastInput();
            if (input != null) {
                forward = (input.forward() ? 1f : 0f) - (input.backward() ? 1f : 0f);
                strafe = (input.left() ? 1f : 0f) - (input.right() ? 1f : 0f);
                jump = input.jump();
            } else {
                forward = player.zza;
                strafe = player.xxa;
                jump = this.jumping;
            }
        } else {
            forward = player.zza;
            strafe = player.xxa;
            jump = this.jumping;
        }

        Vec3 velocity = player.getDeltaMovement();

        boolean onGround = player.onGround();
        boolean fullGrounded = onGround;

        if (player instanceof ServerPlayer serverPlayer) {
            MomentumPlayerData data = Momentum.getPlayerData(serverPlayer);
            fullGrounded = data.isFullGrounded();
            data.setWasOnGround(onGround);
        }

        ci.cancel();

        if (onGround) {
            velocity = groundMove(velocity, forward, strafe, player, fullGrounded, player.getYRot());
        } else {
            velocity = airMove(velocity, forward, strafe, player.getYRot());
        }

        velocity = velocity.add(0, -(config.getGravity() / 400.0), 0);
        player.setDeltaMovement(velocity);

        if (config.isAutoBhop()) {
            noJumpDelay = 0;
            if (jump && onGround) {
                Vec3 current = player.getDeltaMovement();
                player.setDeltaMovement(current.x, config.getJumpVelocity() / 20.0, current.z);
            }
        } else {
            boolean jumpPressed;
            if (player instanceof ServerPlayer serverPlayer) {
                MomentumPlayerData data = Momentum.getPlayerData(serverPlayer);
                jumpPressed = !data.wasJumping();
                data.setWasJumping(jump);
            } else {
                jumpPressed = !momentum$wasJumping;
                momentum$wasJumping = jump;
            }
            if (jumpPressed && jump && onGround) {
                Vec3 current = player.getDeltaMovement();
                player.setDeltaMovement(current.x, config.getJumpVelocity() / 20.0, current.z);
            }
        }

        player.move(net.minecraft.world.entity.MoverType.SELF, player.getDeltaMovement());
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        if (!(((Object) this) instanceof Player player)) return;
        MomentumConfig config = Momentum.getConfig();
        if (!config.isEnabled()) return;
        if (player.isPassenger() || player.isSpectator()) return;
        if (player.getAbilities().flying) return;
        if (player.isFallFlying()) return;
        if (player.isInWater() || player.isInLava()) return;
        if (player.onClimbable()) return;
        ci.cancel();
    }

    private Vec3 groundMove(Vec3 velocity, float forward, float strafe,
                             LivingEntity player, boolean fullGrounded, float yRot) {
        double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

        MomentumConfig config = Momentum.getConfig();

        if (fullGrounded && speed > 0) {
            float slipperiness = 0.6f;
            double frictionValue = 1.0 - (slipperiness * slipperiness);
            double drop = speed * (config.getFriction() / 20.0) * frictionValue;
            double newSpeed = Math.max(speed - drop, 0);
            if (newSpeed < config.getStopSpeed() / 20.0) {
                newSpeed = 0;
            }
            if (speed > 0) {
                velocity = velocity.scale(newSpeed / speed);
            }
        }

        Vec3 wishDir = getWishDir(forward, strafe, yRot);
        if (wishDir == null) return velocity;

        double maxVel = config.getGroundSpeedCap() / 20.0;
        double projVel = velocity.x * wishDir.x + velocity.z * wishDir.z;
        double accel = config.getGroundAcceleration() / 20.0;

        if (projVel + accel > maxVel) {
            accel = maxVel - projVel;
        }
        if (accel > 0) {
            velocity = velocity.add(wishDir.x * accel, 0, wishDir.z * accel);
        }

        return velocity;
    }

    private Vec3 airMove(Vec3 velocity, float forward, float strafe, float yRot) {
        Vec3 wishDir = getWishDir(forward, strafe, yRot);
        if (wishDir == null) return velocity;

        MomentumConfig config = Momentum.getConfig();
        double maxAirSpeed = config.getAirSpeedCap() / 20.0;

        double baseWish = maxAirSpeed * 0.1;

        double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        double projVel = velocity.x * wishDir.x + velocity.z * wishDir.z;

        double wishSpeed = baseWish;
        if (speed > 0.001) {
            double velDotWish = projVel / speed;
            velDotWish = Math.max(-1.0, Math.min(1.0, velDotWish));
            double angleBetween = Math.acos(velDotWish);
            wishSpeed = baseWish * (angleBetween * angleBetween * angleBetween);
            if (wishSpeed < 0.001) wishSpeed = 0.001;
        }

        double addSpeed = wishSpeed - projVel;
        if (addSpeed <= 0) return velocity;

        double accelSpeed = config.getAirAcceleration() * 0.05;
        if (accelSpeed > addSpeed) accelSpeed = addSpeed;

        velocity = velocity.add(wishDir.x * accelSpeed, 0, wishDir.z * accelSpeed);

        speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (speed > maxAirSpeed) {
            velocity = new Vec3(
                velocity.x / speed * maxAirSpeed,
                velocity.y,
                velocity.z / speed * maxAirSpeed
            );
        }

        return velocity;
    }

    private static Vec3 getWishDir(float forward, float strafe, float yRot) {
        if (forward == 0 && strafe == 0) return null;

        double len = Math.sqrt(forward * forward + strafe * strafe);
        if (len > 1.0) {
            forward /= (float) len;
            strafe /= (float) len;
        }

        double yawRad = Math.toRadians(yRot);
        double sin = Math.sin(yawRad);
        double cos = Math.cos(yawRad);

        double x = strafe * cos - forward * sin;
        double z = forward * cos + strafe * sin;

        double wishLen = Math.sqrt(x * x + z * z);
        if (wishLen > 0) {
            x /= wishLen;
            z /= wishLen;
        }
        return new Vec3(x, 0, z);
    }
}
