#!/bin/bash
# ========================================
#   Lake Logger - Unix Build Script
# ========================================

echo ""
echo "  ___       _         _                            "
echo " / __\ __ _| | _____ / /  ___   __ _  __ _  ___ _ __ "
echo "/ /   / _\` | |/ / _ \ /  / _ \ / _\` |/ _\` |/ _ \ '__|"
echo "/ /___| (_| |   <  __/ /__\ (_) | (_| | (_| |  __/ |   "
echo "\____/ \__,_|_|\_\___\____/\___/ \__, |\__, |\___|_|   "
echo "                                 |___/ |___/           "
echo ""
echo "========================================"
echo "         BUILD SCRIPT v1.0.0"
echo "========================================"
echo ""

# Navigate to project root
cd "$(dirname "$0")/.."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "[ERROR] Maven is not installed or not in PATH"
    echo "        Please install Maven: brew install maven (macOS)"
    echo "                              sudo apt install maven (Ubuntu/Debian)"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed or not in PATH"
    echo "        Please install Java 17 or later"
    exit 1
fi

# Display versions
echo "[INFO] Checking environment..."
echo "       Java version: $(java -version 2>&1 | head -n 1 | cut -d'"' -f2)"
echo "       Maven version: $(mvn -version 2>&1 | head -n 1 | cut -d' ' -f3)"
echo ""

echo "[INFO] Cleaning previous build..."
mvn clean -q

echo "[INFO] Building Lake Logger..."
mvn package -DskipTests -q

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Build failed! Check the output above for details."
    exit 1
fi

# Create dist folder
mkdir -p dist
cp target/LakeLogger.jar dist/LakeLogger.jar

echo ""
echo "========================================"
echo "       BUILD SUCCESSFUL!"
echo "========================================"
echo ""
echo "  Output: dist/LakeLogger.jar"
echo ""
echo "  To run: ./scripts/run.sh"
echo "          -or-"
echo "          java -jar dist/LakeLogger.jar"
echo ""
