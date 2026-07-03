package com.momentum;

import com.momentum.config.MomentumConfig;
import com.momentum.engine.MomentumPlayerData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Momentum implements ModInitializer {
    public static final String MOD_ID = "momentum";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MomentumConfig config;
    private static final Map<UUID, MomentumPlayerData> playerDataMap = new ConcurrentHashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Momentum initializing...");

        config = MomentumConfig.load();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LOGGER.info("Player joined: {}", handler.getPlayer().getName().getString());
        });

        LOGGER.info("Momentum v{} initialized", config.getVersion());
    }

    public static MomentumPlayerData getPlayerData(ServerPlayer player) {
        return playerDataMap.computeIfAbsent(player.getUUID(), k -> new MomentumPlayerData(player));
    }

    public static MomentumConfig getConfig() {
        return config;
    }
}
