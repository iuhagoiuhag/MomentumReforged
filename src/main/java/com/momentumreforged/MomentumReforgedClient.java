package com.momentumreforged;

import com.momentumreforged.network.MomentumReforgedNetwork;
import net.fabricmc.api.ClientModInitializer;

public class MomentumReforgedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MomentumReforged.LOGGER.info("MomentumReforged client initializing...");
        MomentumReforgedNetwork.registerClientHandlers();
        MomentumReforged.LOGGER.info("MomentumReforged client initialized");
    }
}
