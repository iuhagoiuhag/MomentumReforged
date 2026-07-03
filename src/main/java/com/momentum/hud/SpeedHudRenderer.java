package com.momentum.hud;

import com.momentum.Momentum;
import com.momentum.config.MomentumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

public class SpeedHudRenderer {
    private static final Minecraft client = Minecraft.getInstance();

    public static void render(GuiGraphicsExtractor graphics) {
        MomentumConfig config = Momentum.getConfig();
        if (!config.isShowSpeedHud()) return;

        LocalPlayer player = client.player;
        if (player == null) return;

        double horizontalSpeed = Math.sqrt(
            player.getDeltaMovement().x * player.getDeltaMovement().x +
            player.getDeltaMovement().z * player.getDeltaMovement().z
        );

        double verticalSpeed = player.getDeltaMovement().y;

        int x = config.getHudX();
        int y = config.getHudY();

        String speedText = String.format("Speed: %.2f", horizontalSpeed * 20);
        graphics.text(client.font, speedText, x, y, 0xFFFFFF, true);

        y += 12;
        String verticalText = String.format("Vertical: %.2f", verticalSpeed * 20);
        graphics.text(client.font, verticalText, x, y, 0xAAAAFF, true);

        if (config.isShowMaxSpeed()) {
            y += 12;
            double maxSpeed = horizontalSpeed * 20;
            String maxText = String.format("Max: %.2f", maxSpeed);
            graphics.text(client.font, maxText, x, y, 0xFFAA00, true);
        }

        y += 12;
        String bhoppingText = player.onGround() ? "Ground" : "Air";
        int bhoppingColor = player.onGround() ? 0x55FF55 : 0x55FFFF;
        graphics.text(client.font, bhoppingText, x, y, bhoppingColor, true);

        if (config.isAutoBhop()) {
            y += 12;
            graphics.text(client.font, "Auto-Bhop", x, y, 0xFFD700, true);
        }
    }
}
