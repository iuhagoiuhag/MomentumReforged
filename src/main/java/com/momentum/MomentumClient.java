package com.momentum;

import com.momentum.hud.SpeedHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class MomentumClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Momentum.LOGGER.info("Momentum client initializing...");

        HudElementRegistry.attachElementAfter(
            VanillaHudElements.HOTBAR,
            Identifier.fromNamespaceAndPath(Momentum.MOD_ID, "speed"),
            (graphics, tickCounter) -> SpeedHudRenderer.render(graphics)
        );

        Momentum.LOGGER.info("Momentum client initialized");
    }
}
