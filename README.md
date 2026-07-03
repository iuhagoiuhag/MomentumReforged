# Momentum

Source engine style bunny hopping mod for Minecraft (Fabric).

## Features

- **Air Strafing**: Accelerate in the air while turning
- **Bhop Physics**: Source-engine accurate movement mechanics  
- **Speed HUD**: Real-time speed display with max speed tracking
- **Configurable**: All physics parameters adjustable

## Installation

1. Install Fabric Loader for Minecraft 26.1.2+
2. Download the latest Momentum jar
3. Place in your `mods` folder

## Configuration

Edit `config/momentum.json` to customize:

- `enabled` - Master toggle
- `bhopEnabled` - Enable bhop mechanics
- `airSpeedCap` - Maximum air strafe speed (default: 30.0)
- `groundSpeedCap` - Ground speed limit (default: 4.4)
- `showSpeedHud` - Show speed overlay

## Building

```bash
./gradlew build
```

## License

Apache License 2.0
