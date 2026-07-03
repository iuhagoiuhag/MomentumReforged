package com.momentum.engine;

import com.momentum.Momentum;
import com.momentum.config.MomentumConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class BhopEngine {
    private static final double AIR_ACCELERATE = 12.0;
    private static final double ACCELERATE = 5.6;
    private static final double FRICTION = 4.0;
    private static final double STOPSPEED = 1.0;
    private static final double MAX_AIR_WISH_SPEED = 30.0;
    private static final double MAX_SPEED = 4.4;
    private static final double SURFACE_FRICTION = 1.0;
    private static final double JUMP_VELOCITY = 8.42;

    private final Player player;
    private double maxHorizontalSpeed;
    private boolean wasOnGround;
    private boolean isBhopping;
    private int airTicks;
    private int groundTicks;
    private Vec3 lastInput;

    public BhopEngine(Player player) {
        this.player = player;
        this.maxHorizontalSpeed = 0;
        this.wasOnGround = false;
        this.isBhopping = false;
        this.airTicks = 0;
        this.groundTicks = 0;
        this.lastInput = Vec3.ZERO;
    }

    public void tick() {
        boolean onGround = player.onGround();
        Vec3 velocity = player.getDeltaMovement();
        double currentSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

        if (onGround) {
            airTicks = 0;
            groundTicks++;
            wasOnGround = true;
        } else {
            groundTicks = 0;
            airTicks++;
            maxHorizontalSpeed = Math.max(maxHorizontalSpeed, currentSpeed);
            wasOnGround = false;
        }
    }

    public void setInput(double leftRight, double forwardBack) {
        this.lastInput = new Vec3(leftRight, 0, forwardBack);
    }

    public Vec3 getLastInput() {
        return lastInput;
    }

    public void onJump() {
        MomentumConfig config = Momentum.getConfig();
        if (!config.isEnabled() || !config.isBhopEnabled()) return;
        isBhopping = true;
    }

    public Vec3 processMovement(Vec3 input, float yRot, boolean onGround, boolean jumping, boolean sprinting) {
        MomentumConfig config = Momentum.getConfig();
        if (!config.isEnabled() || !config.isBhopEnabled()) return null;

        Vec3 velocity = player.getDeltaMovement();

        if (onGround) {
            velocity = groundMove(velocity, input, yRot, jumping, sprinting);
        } else {
            velocity = airMove(velocity, input, yRot);
        }

        return velocity;
    }

    private Vec3 groundMove(Vec3 velocity, Vec3 input, float yRot, boolean jumping, boolean sprinting) {
        // Apply friction
        double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        velocity = applyFriction(velocity, speed);

        // Calculate wish direction
        Vec3 wishDir = calculateWishDir(input, yRot);
        double wishSpeed = calculateWishSpeed(input, MAX_SPEED);

        // Accelerate
        velocity = accelerate(velocity, wishDir, wishSpeed, ACCELERATE);

        // Handle jump
        if (jumping) {
            velocity = new Vec3(velocity.x, JUMP_VELOCITY * (sprinting ? 1.05 : 1.0), velocity.z);
            isBhopping = true;
        }

        return velocity;
    }

    private Vec3 airMove(Vec3 velocity, Vec3 input, float yRot) {
        Vec3 wishDir = calculateWishDir(input, yRot);
        double wishSpeed = calculateWishSpeed(input, MAX_AIR_WISH_SPEED);

        return airAccelerate(velocity, wishDir, wishSpeed);
    }

    private Vec3 calculateWishDir(Vec3 input, float yRot) {
        double forward = input.z;
        double strafe = input.x;

        double yawRad = Math.toRadians(-yRot);
        double forwardX = Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);
        double rightX = Math.cos(yawRad);
        double rightZ = -Math.sin(yawRad);

        double wishX = forward * forwardX + strafe * rightX;
        double wishZ = forward * forwardZ + strafe * rightZ;

        double length = Math.sqrt(wishX * wishX + wishZ * wishZ);
        if (length > 0) {
            wishX /= length;
            wishZ /= length;
        }

        return new Vec3(wishX, 0, wishZ);
    }

    private double calculateWishSpeed(Vec3 input, double maxSpeed) {
        double forward = Math.abs(input.z);
        double strafe = Math.abs(input.x);

        if (forward > 0 && strafe > 0) {
            forward *= 0.7071;
            strafe *= 0.7071;
        }

        return Math.max(forward, strafe) * maxSpeed;
    }

    private Vec3 applyFriction(Vec3 velocity, double speed) {
        if (speed < 0.1) return Vec3.ZERO;

        double control = Math.max(speed, STOPSPEED);
        double drop = control * FRICTION * SURFACE_FRICTION * getTickDelta();

        double newSpeed = Math.max(speed - drop, 0) / speed;

        return new Vec3(
            velocity.x * newSpeed,
            velocity.y,
            velocity.z * newSpeed
        );
    }

    private Vec3 accelerate(Vec3 velocity, Vec3 wishDir, double wishSpeed, double accel) {
        double currentSpeed = velocity.x * wishDir.x + velocity.z * wishDir.z;
        double addSpeed = wishSpeed - currentSpeed;

        if (addSpeed <= 0) return velocity;

        double accelSpeed = accel * getTickDelta() * wishSpeed * SURFACE_FRICTION;
        if (accelSpeed > addSpeed) accelSpeed = addSpeed;

        return new Vec3(
            velocity.x + accelSpeed * wishDir.x,
            velocity.y,
            velocity.z + accelSpeed * wishDir.z
        );
    }

    private Vec3 airAccelerate(Vec3 velocity, Vec3 wishDir, double wishSpeed) {
        if (wishSpeed > MAX_AIR_WISH_SPEED) {
            wishSpeed = MAX_AIR_WISH_SPEED;
        }

        double currentSpeed = velocity.x * wishDir.x + velocity.z * wishDir.z;
        double addSpeed = wishSpeed - currentSpeed;

        if (addSpeed <= 0) return velocity;

        double accelSpeed = AIR_ACCELERATE * getTickDelta() * wishSpeed;
        if (accelSpeed > addSpeed) accelSpeed = addSpeed;

        return new Vec3(
            velocity.x + accelSpeed * wishDir.x,
            velocity.y,
            velocity.z + accelSpeed * wishDir.z
        );
    }

    private double getTickDelta() {
        return 0.05;
    }

    public double getCurrentHorizontalSpeed() {
        Vec3 velocity = player.getDeltaMovement();
        return Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
    }

    public double getMaxHorizontalSpeed() {
        return maxHorizontalSpeed;
    }

    public boolean isBhopping() {
        return isBhopping;
    }

    public int getAirTicks() {
        return airTicks;
    }

    public int getGroundTicks() {
        return groundTicks;
    }

    public void resetMaxSpeed() {
        this.maxHorizontalSpeed = 0;
    }
}
