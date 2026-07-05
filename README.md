# MomentumReforged

Source engine style bunny hopping mod for Minecraft (Fabric).

## Features

- **Air Strafing**: Accelerate in the air while turning
- **Bhop Physics**: Source-engine accurate movement mechanics
- **Configurable**: All physics parameters adjustable

## Installation

1. Install Fabric Loader for Minecraft 26.1.2+
2. Download the latest MomentumReforged jar
3. Place in your `mods` folder

## Configuration

Edit `config/momentumreforged.json` to customize:

- `enabled` - Master toggle
- `bhopEnabled` - Enable bhop mechanics
- `airSpeedCap` - Maximum air strafe speed (default: 30.0)
- `groundSpeedCap` - Ground speed limit (default: 4.4)

## Building

```bash
./gradlew build
```

## License

Apache License 2.0
