package com.momentumreforged.engine;

import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;

public class MomentumReforgedPlayerData {
    private final Player player;
    private Input lastInput;
    private double bestSpeed;
    private boolean wasOnGround;
    private boolean wasJumping;
    private boolean hasMod;

    public MomentumReforgedPlayerData(Player player) {
        this.player = player;
        this.lastInput = null;
        this.bestSpeed = 0;
        this.wasOnGround = false;
        this.wasJumping = false;
        this.hasMod = false;
    }

    public boolean hasMod() {
        return hasMod;
    }

    public void setHasMod(boolean hasMod) {
        this.hasMod = hasMod;
    }

    public void setLastInput(Input input) {
        this.lastInput = input;
    }

    public Input getLastInput() {
        return lastInput;
    }

    public void setWasOnGround(boolean onGround) {
        this.wasOnGround = onGround;
    }

    public boolean isFullGrounded() {
        return wasOnGround && player.onGround();
    }

    public boolean wasJumping() {
        return wasJumping;
    }

    public void setWasJumping(boolean wasJumping) {
        this.wasJumping = wasJumping;
    }

    public double getCurrentHorizontalSpeed() {
        var vel = player.getDeltaMovement();
        return Math.sqrt(vel.x * vel.x + vel.z * vel.z);
    }

    public double getBestSpeed() {
        double current = getCurrentHorizontalSpeed();
        if (current > bestSpeed) bestSpeed = current;
        return bestSpeed;
    }

    public void resetStats() {
        this.bestSpeed = 0;
    }
}
