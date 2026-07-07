package com.momentumreforged.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.momentumreforged.MomentumReforged;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MomentumReforgedConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("momentumreforged.json");

    private boolean enabled = true;
    private boolean autoBhop = true;

    private double groundSpeedCap = 4.3;
    private double airSpeedCap = 20.0;
    private double airAcceleration = 3.0;
    private double groundAcceleration = 8.0;
    private double friction = 4.0;
    private double gravity = 32.0;
    private double jumpVelocity = 8.4;
    private double stopSpeed = 1.0;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAutoBhop() {
        return autoBhop;
    }

    public void setAutoBhop(boolean autoBhop) {
        this.autoBhop = autoBhop;
    }

    public double getGroundSpeedCap() {
        return groundSpeedCap;
    }

    public void setGroundSpeedCap(double groundSpeedCap) {
        this.groundSpeedCap = groundSpeedCap;
    }

    public double getAirSpeedCap() {
        return airSpeedCap;
    }

    public void setAirSpeedCap(double airSpeedCap) {
        this.airSpeedCap = airSpeedCap;
    }

    public double getAirAcceleration() {
        return airAcceleration;
    }

    public void setAirAcceleration(double airAcceleration) {
        this.airAcceleration = airAcceleration;
    }

    public double getGroundAcceleration() {
        return groundAcceleration;
    }

    public void setGroundAcceleration(double groundAcceleration) {
        this.groundAcceleration = groundAcceleration;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public double getJumpVelocity() {
        return jumpVelocity;
    }

    public void setJumpVelocity(double jumpVelocity) {
        this.jumpVelocity = jumpVelocity;
    }

    public double getStopSpeed() {
        return stopSpeed;
    }

    public void setStopSpeed(double stopSpeed) {
        this.stopSpeed = stopSpeed;
    }

    public String getVersion() {
        return FabricLoader.getInstance().getModContainer(MomentumReforged.MOD_ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }

    public static MomentumReforgedConfig loadDefaults() {
        try {
            var stream = MomentumReforgedConfig.class.getClassLoader()
                .getResourceAsStream("defaultconfig.json");
            if (stream != null) {
                String json = new String(stream.readAllBytes());
                return GSON.fromJson(json, MomentumReforgedConfig.class);
            }
        } catch (IOException e) {
            MomentumReforged.LOGGER.error("Failed to load default config", e);
        }
        return new MomentumReforgedConfig();
    }

    public static MomentumReforgedConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, MomentumReforgedConfig.class);
            } catch (IOException e) {
                MomentumReforged.LOGGER.error("Failed to load config", e);
            }
        }
        return loadDefaults();
    }

    public void save() {
        try {
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            MomentumReforged.LOGGER.error("Failed to save config", e);
        }
    }
}
