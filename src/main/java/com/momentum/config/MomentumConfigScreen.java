package com.momentum.config;

import com.momentum.Momentum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MomentumConfigScreen extends Screen {
    private final Screen parent;
    private final MomentumConfig config;

    private boolean enabled;
    private boolean bhopEnabled;
    private boolean autoBhop;
    private boolean showSpeedHud;
    private boolean showMaxSpeed;
    private double airSpeedCap;
    private double groundSpeedCap;

    public MomentumConfigScreen(Screen parent) {
        super(Component.literal("Momentum Settings"));
        this.parent = parent;
        this.config = Momentum.getConfig();
    }

    @Override
    protected void init() {
        enabled = config.isEnabled();
        bhopEnabled = config.isBhopEnabled();
        autoBhop = config.isAutoBhop();
        showSpeedHud = config.isShowSpeedHud();
        showMaxSpeed = config.isShowMaxSpeed();
        airSpeedCap = config.getAirSpeedCap();
        groundSpeedCap = config.getGroundSpeedCap();

        int centerX = this.width / 2;
        int startY = 40;
        int rowHeight = 24;

        this.addRenderableWidget(
            Button.builder(Component.literal("Enabled: " + (enabled ? "ON" : "OFF")), button -> {
                enabled = !enabled;
                button.setMessage(Component.literal("Enabled: " + (enabled ? "ON" : "OFF")));
            }).bounds(centerX - 100, startY, 200, 20).build()
        );

        this.addRenderableWidget(
            Button.builder(Component.literal("Bhop: " + (bhopEnabled ? "ON" : "OFF")), button -> {
                bhopEnabled = !bhopEnabled;
                button.setMessage(Component.literal("Bhop: " + (bhopEnabled ? "ON" : "OFF")));
            }).bounds(centerX - 100, startY + rowHeight, 200, 20).build()
        );

        this.addRenderableWidget(
            Button.builder(Component.literal("Auto-Bhop: " + (autoBhop ? "ON" : "OFF")), button -> {
                autoBhop = !autoBhop;
                button.setMessage(Component.literal("Auto-Bhop: " + (autoBhop ? "ON" : "OFF")));
            }).bounds(centerX - 100, startY + rowHeight * 2, 200, 20).build()
        );

        this.addRenderableWidget(
            Button.builder(Component.literal("Speed HUD: " + (showSpeedHud ? "ON" : "OFF")), button -> {
                showSpeedHud = !showSpeedHud;
                button.setMessage(Component.literal("Speed HUD: " + (showSpeedHud ? "ON" : "OFF")));
            }).bounds(centerX - 100, startY + rowHeight * 3, 200, 20).build()
        );

        this.addRenderableWidget(
            Button.builder(Component.literal("Show Max Speed: " + (showMaxSpeed ? "ON" : "OFF")), button -> {
                showMaxSpeed = !showMaxSpeed;
                button.setMessage(Component.literal("Show Max Speed: " + (showMaxSpeed ? "ON" : "OFF")));
            }).bounds(centerX - 100, startY + rowHeight * 4, 200, 20).build()
        );

        this.addRenderableWidget(
            Button.builder(Component.literal("Air Speed Cap: " + String.format("%.1f", airSpeedCap)), button -> {
                airSpeedCap = airSpeedCap >= 50.0 ? 10.0 : airSpeedCap + 5.0;
                button.setMessage(Component.literal("Air Speed Cap: " + String.format("%.1f", airSpeedCap)));
            }).bounds(centerX - 100, startY + rowHeight * 5, 200, 20).build()
        );

        this.addRenderableWidget(
            Button.builder(Component.literal("Ground Speed Cap: " + String.format("%.1f", groundSpeedCap)), button -> {
                groundSpeedCap = groundSpeedCap >= 10.0 ? 2.0 : groundSpeedCap + 0.5;
                button.setMessage(Component.literal("Ground Speed Cap: " + String.format("%.1f", groundSpeedCap)));
            }).bounds(centerX - 100, startY + rowHeight * 6, 200, 20).build()
        );

        this.addRenderableWidget(
            Button.builder(Component.literal("Save & Close"), button -> {
                save();
                this.minecraft.gui.setScreen(parent);
            }).bounds(centerX - 100, startY + rowHeight * 7 + 10, 200, 20).build()
        );
    }

    private void save() {
        config.setEnabled(enabled);
        config.setBhopEnabled(bhopEnabled);
        config.setAutoBhop(autoBhop);
        config.setShowSpeedHud(showSpeedHud);
        config.setShowMaxSpeed(showMaxSpeed);
        config.setAirSpeedCap(airSpeedCap);
        config.setGroundSpeedCap(groundSpeedCap);
        config.save();
        Momentum.LOGGER.info("Momentum config saved");
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(parent);
    }
}
