package com.momentumreforged;

import com.momentumreforged.hud.SpeedHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class MomentumReforgedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MomentumReforged.LOGGER.info("MomentumReforged client initializing...");

        HudElementRegistry.attachElementAfter(
            VanillaHudElements.HOTBAR,
            Identifier.fromNamespaceAndPath(MomentumReforged.MOD_ID, "speed"),
            (graphics, tickCounter) -> SpeedHudRenderer.render(graphics)
        );

        MomentumReforged.LOGGER.info("MomentumReforged client initialized");
    }
}
