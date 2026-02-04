#!/bin/bash
# ========================================
#   Lake Logger - Unix Run Script
# ========================================

# Navigate to project root
cd "$(dirname "$0")/.."

# Check for JAR in dist folder first, then target
JAR_PATH="dist/LakeLogger.jar"
if [ ! -f "$JAR_PATH" ]; then
    JAR_PATH="target/LakeLogger.jar"
fi

if [ ! -f "$JAR_PATH" ]; then
    echo "[ERROR] LakeLogger.jar not found!"
    echo "        Please run build.sh first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed or not in PATH"
    echo "        Please install Java 17 or later"
    exit 1
fi

echo "Starting Lake Logger..."
echo ""

# Run with native access enabled to suppress SQLite warnings
java --enable-native-access=ALL-UNNAMED -jar "$JAR_PATH"
