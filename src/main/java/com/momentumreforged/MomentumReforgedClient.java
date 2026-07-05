package com.momentumreforged;

import net.fabricmc.api.ClientModInitializer;

public class MomentumReforgedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MomentumReforged.LOGGER.info("MomentumReforged client initializing...");
        MomentumReforged.LOGGER.info("MomentumReforged client initialized");
    }
}
