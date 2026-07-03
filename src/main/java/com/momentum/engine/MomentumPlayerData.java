package com.momentum.engine;

import net.minecraft.world.entity.player.Player;

public class MomentumPlayerData {
    private final Player player;
    private final BhopEngine bhopEngine;
    private double bestSpeed;
    private boolean isBhopActive;

    public MomentumPlayerData(Player player) {
        this.player = player;
        this.bhopEngine = new BhopEngine(player);
        this.bestSpeed = 0;
        this.isBhopActive = false;
    }

    public void tick() {
        double currentSpeed = bhopEngine.getCurrentHorizontalSpeed();
        if (currentSpeed > bestSpeed) {
            bestSpeed = currentSpeed;
        }
    }

    public BhopEngine getBhopEngine() {
        return bhopEngine;
    }

    public double getBestSpeed() {
        return bestSpeed;
    }

    public void setBhopActive(boolean active) {
        this.isBhopActive = active;
    }

    public boolean isBhopActive() {
        return isBhopActive;
    }

    public void resetStats() {
        this.bestSpeed = 0;
        this.bhopEngine.resetMaxSpeed();
    }
}
