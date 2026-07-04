package com.momentumreforged.config;

import com.momentumreforged.MomentumReforged;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MomentumReforgedConfigScreen extends Screen {
    private final Screen parent;
    private final MomentumReforgedConfig config;

    private boolean enabled;
    private boolean autoBhop;
    private double airSpeedCap;
    private double airAcceleration;
    private double groundSpeedCap;
    private double groundAcceleration;
    private double friction;
    private double gravity;
    private double jumpVelocity;
    private double stopSpeed;

    public MomentumReforgedConfigScreen(Screen parent) {
        super(Component.literal("MomentumReforged Settings"));
        this.parent = parent;
        this.config = MomentumReforged.getConfig();
    }

    @Override
    protected void init() {
        enabled = config.isEnabled();
        autoBhop = config.isAutoBhop();
        airSpeedCap = config.getAirSpeedCap();
        airAcceleration = config.getAirAcceleration();
        groundSpeedCap = config.getGroundSpeedCap();
        groundAcceleration = config.getGroundAcceleration();
        friction = config.getFriction();
        gravity = config.getGravity();
        jumpVelocity = config.getJumpVelocity();
        stopSpeed = config.getStopSpeed();

        int cx = this.width / 2;
        int rh = 22;

        // Toggles row
        int toggleY = 35;
        int toggleW = 100;
        int gap = 8;
        int totalToggleW = toggleW * 2 + gap;
        int toggleStartX = cx - totalToggleW / 2;

        addToggle(toggleStartX, toggleY, toggleW, "Enabled", () -> enabled,
            "Toggles all MomentumReforged physics on/off", v -> enabled = v);
        addToggle(toggleStartX + toggleW + gap, toggleY, toggleW, "Auto-Bhop", () -> autoBhop,
            "Automatically re-jumps when holding space", v -> autoBhop = v);

        // Physics values - two columns
        int colW = 150;
        int colGap = 10;
        int col1X = cx - colW - colGap / 2;
        int col2X = cx + colGap / 2;
        int physStartY = 68;

        this.addRenderableWidget(new AbstractSliderButton(col1X, physStartY, colW, 20,
                Component.literal("Air Speed Cap: " + String.format("%.0f", airSpeedCap)),
                (airSpeedCap - 10) / 90.0) {
            @Override
            protected void updateMessage() {
                double val = Math.round(10 + this.value * 90);
                this.setMessage(Component.literal("Air Speed Cap: " + String.format("%.0f", val)));
            }
            @Override
            protected void applyValue() {
                airSpeedCap = Math.round(10 + this.value * 90);
            }
        });
        addCycle(col1X, physStartY + rh, colW, "Air Accel", () -> airAcceleration,
            "Acceleration rate while in the air",
            new double[]{1, 2, 3, 4, 5, 6, 8, 10}, v -> airAcceleration = v);
        addCycle(col1X, physStartY + rh * 2, colW, "Ground Speed", () -> groundSpeedCap,
            "Maximum speed while on the ground (blocks/sec)",
            new double[]{2, 2.5, 3, 3.5, 4, 4.3, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10},
            v -> groundSpeedCap = v);
        addCycle(col1X, physStartY + rh * 3, colW, "Ground Accel", () -> groundAcceleration,
            "Acceleration rate while on the ground",
            new double[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20}, v -> groundAcceleration = v);

        addCycle(col2X, physStartY, colW, "Friction", () -> friction,
            "Ground friction applied when moving",
            new double[]{0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6},
            v -> friction = v);
        addCycle(col2X, physStartY + rh, colW, "Gravity", () -> gravity,
            "Gravity acceleration (blocks/sec\u00B2)",
            new double[]{8, 12, 16, 20, 24, 28, 32, 36, 40},
            v -> gravity = v);
        addCycle(col2X, physStartY + rh * 2, colW, "Jump Vel", () -> jumpVelocity,
            "Initial jump velocity (blocks/sec)",
            new double[]{5, 6, 7, 8, 8.4, 9, 10, 11, 12},
            v -> jumpVelocity = v);
        addCycle(col2X, physStartY + rh * 3, colW, "Stop Speed", () -> stopSpeed,
            "Speed threshold below which the player stops completely",
            new double[]{0.5, 1, 1.5, 2, 2.5, 3},
            v -> stopSpeed = v);

        // Bottom buttons
        int bottomY = physStartY + rh * 4 + 12;

        this.addRenderableWidget(
            Button.builder(Component.literal("Reset to Defaults"), button -> {
                MomentumReforgedConfig defaults = MomentumReforgedConfig.loadDefaults();
                config.setEnabled(defaults.isEnabled());
                config.setAutoBhop(defaults.isAutoBhop());
                config.setAirSpeedCap(defaults.getAirSpeedCap());
                config.setAirAcceleration(defaults.getAirAcceleration());
                config.setGroundSpeedCap(defaults.getGroundSpeedCap());
                config.setGroundAcceleration(defaults.getGroundAcceleration());
                config.setFriction(defaults.getFriction());
                config.setGravity(defaults.getGravity());
                config.setJumpVelocity(defaults.getJumpVelocity());
                config.setStopSpeed(defaults.getStopSpeed());
                this.rebuildWidgets();
            }).tooltip(Tooltip.create(Component.literal("Resets all settings to their default values")))
                .bounds(cx - 155, bottomY, 145, 20).build()
        );

        this.addRenderableWidget(
            Button.builder(Component.literal("Save & Close"), button -> {
                save();
                this.minecraft.gui.setScreen(parent);
            }).tooltip(Tooltip.create(Component.literal("Saves changes and returns to the previous screen")))
                .bounds(cx + 10, bottomY, 145, 20).build()
        );
    }

    private void addToggle(int x, int y, int w, String label,
                           java.util.function.Supplier<Boolean> getter,
                           String tooltip, java.util.function.Consumer<Boolean> setter) {
        this.addRenderableWidget(
            Button.builder(Component.literal(label + ": " + (getter.get() ? "ON" : "OFF")), button -> {
                boolean next = !getter.get();
                setter.accept(next);
                button.setMessage(Component.literal(label + ": " + (next ? "ON" : "OFF")));
            }).tooltip(Tooltip.create(Component.literal(tooltip)))
                .bounds(x, y, w, 20).build()
        );
    }

    private void addCycle(int x, int y, int w, String label,
                          java.util.function.Supplier<Double> getter,
                          String tooltip, double[] values,
                          java.util.function.Consumer<Double> setter) {
        this.addRenderableWidget(
            Button.builder(
                Component.literal(label + ": " + String.format("%.1f", getter.get())), button -> {
                    double next = cycleValue(getter.get(), values);
                    setter.accept(next);
                    button.setMessage(Component.literal(label + ": " + String.format("%.1f", next)));
                }).tooltip(Tooltip.create(Component.literal(tooltip)))
                .bounds(x, y, w, 20).build()
        );
    }

    private static double cycleValue(double current, double[] values) {
        for (int i = 0; i < values.length; i++) {
            if (Math.abs(values[i] - current) < 0.01) {
                return values[(i + 1) % values.length];
            }
        }
        return values[0];
    }

    private void save() {
        config.setEnabled(enabled);
        config.setAutoBhop(autoBhop);
        config.setAirSpeedCap(airSpeedCap);
        config.setAirAcceleration(airAcceleration);
        config.setGroundSpeedCap(groundSpeedCap);
        config.setGroundAcceleration(groundAcceleration);
        config.setFriction(friction);
        config.setGravity(gravity);
        config.setJumpVelocity(jumpVelocity);
        config.setStopSpeed(stopSpeed);
        config.save();
        MomentumReforged.LOGGER.info("MomentumReforged config saved");
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        String version = "v" + config.getVersion();
        graphics.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        graphics.text(this.font, version, this.width - this.font.width(version) - 5, 5, 0x888888, true);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(parent);
    }
}
