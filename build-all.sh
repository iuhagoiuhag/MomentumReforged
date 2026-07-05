#!/bin/bash
# Build MomentumReforged for all supported Minecraft versions

set -e

VERSION=$(grep '^mod_version=' gradle.properties | cut -d'=' -f2)
echo "Building MomentumReforged v${VERSION}..."

echo ""
echo "=== Building for MC 26.2 ==="
./gradlew clean build -Pminecraft_version=26.2 -Pfabric_version=0.154.0+26.2 -Pmodmenu_version=20.0.0-beta.2 2>&1
cp build/libs/momentumreforged-*.jar "build/libs/momentumreforged-${VERSION}-mc26.2.jar" 2>/dev/null || true

echo ""
echo "=== Building for MC 26.1.2 ==="
./gradlew clean build -Pminecraft_version=26.1.2 -Pfabric_version=0.151.0+26.1.2 -Pmodmenu_version=20.0.0-beta.2 2>&1
cp build/libs/momentumreforged-*.jar "build/libs/momentumreforged-${VERSION}-mc26.1.2.jar" 2>/dev/null || true

echo ""
echo "=== Build Complete ==="
echo "JARs available in build/libs/"
ls -la build/libs/*.jar 2>/dev/null
