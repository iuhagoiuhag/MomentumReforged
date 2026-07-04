package com.momentumreforged;

import com.momentumreforged.config.MomentumReforgedConfig;
import com.momentumreforged.engine.MomentumReforgedPlayerData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MomentumReforged implements ModInitializer {
    public static final String MOD_ID = "momentumreforged";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MomentumReforgedConfig config;
    private static final Map<UUID, MomentumReforgedPlayerData> playerDataMap = new ConcurrentHashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.info("MomentumReforged initializing...");

        config = MomentumReforgedConfig.load();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LOGGER.info("Player joined: {}", handler.getPlayer().getName().getString());
        });

        LOGGER.info("MomentumReforged v{} initialized", config.getVersion());
    }

    public static MomentumReforgedPlayerData getPlayerData(ServerPlayer player) {
        return playerDataMap.computeIfAbsent(player.getUUID(), k -> new MomentumReforgedPlayerData(player));
    }

    public static MomentumReforgedConfig getConfig() {
        return config;
    }
}
