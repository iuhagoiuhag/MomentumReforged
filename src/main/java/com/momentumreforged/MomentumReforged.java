package com.momentumreforged;

import com.momentumreforged.config.MomentumReforgedConfig;
import com.momentumreforged.engine.MomentumReforgedPlayerData;
import com.momentumreforged.network.HandshakePayload;
import com.momentumreforged.network.MomentumReforgedNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

        MomentumReforgedNetwork.register();

        config = MomentumReforgedConfig.load();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            LOGGER.info("Player joined: {}", player.getName().getString());
            ServerPlayNetworking.send(player, HandshakePayload.INSTANCE);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            playerDataMap.remove(player.getUUID());
        });

        ServerPlayNetworking.registerGlobalReceiver(
                MomentumReforgedNetwork.HANDSHAKE_TYPE,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    context.server().execute(() -> {
                        MomentumReforgedPlayerData data = getPlayerData(player);
                        data.setHasMod(true);
                        LOGGER.info("Player {} has MomentumReforged installed", player.getName().getString());
                    });
                }
        );

        LOGGER.info("MomentumReforged v{} initialized", config.getVersion());
    }

    public static MomentumReforgedPlayerData getPlayerData(ServerPlayer player) {
        return playerDataMap.computeIfAbsent(player.getUUID(), k -> new MomentumReforgedPlayerData(player));
    }

    public static MomentumReforgedConfig getConfig() {
        return config;
    }
}
