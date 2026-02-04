#!/bin/bash
# ========================================
#   Lake Logger - Unix Release Script
# ========================================

echo ""
echo "========================================"
echo "      LAKE LOGGER RELEASE BUILDER"
echo "========================================"
echo ""

# Navigate to project root
cd "$(dirname "$0")/.."

# Get version from pom.xml
VERSION=$(grep -m1 '<version>' pom.xml | sed 's/.*<version>\(.*\)<\/version>.*/\1/')
if [ -z "$VERSION" ]; then
    VERSION="1.0.0"
fi

echo "[INFO] Building Lake Logger v$VERSION"
echo ""

# Check prerequisites
if ! command -v mvn &> /dev/null; then
    echo "[ERROR] Maven is not installed"
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed"
    exit 1
fi

# Clean and build
echo "[INFO] Cleaning previous build..."
mvn clean -q

echo "[INFO] Building release JAR..."
mvn package -DskipTests -q

if [ $? -ne 0 ]; then
    echo "[ERROR] Build failed!"
    exit 1
fi

# Create release directory
RELEASE_DIR="releases/LakeLogger-v$VERSION"
echo "[INFO] Creating release directory: $RELEASE_DIR"

rm -rf "$RELEASE_DIR"
mkdir -p "$RELEASE_DIR"

# Copy files
echo "[INFO] Copying release files..."
cp "target/LakeLogger.jar" "$RELEASE_DIR/LakeLogger.jar"
cp "README.md" "$RELEASE_DIR/README.md"
cp "LICENSE" "$RELEASE_DIR/LICENSE"

# Create run scripts for release
cat > "$RELEASE_DIR/run.bat" << 'EOF'
@echo off
java --enable-native-access=ALL-UNNAMED -jar LakeLogger.jar
EOF

cat > "$RELEASE_DIR/run.sh" << 'EOF'
#!/bin/bash
java --enable-native-access=ALL-UNNAMED -jar LakeLogger.jar
EOF
chmod +x "$RELEASE_DIR/run.sh"

# Create zip archive
echo "[INFO] Creating zip archive..."
cd releases
zip -rq "LakeLogger-v$VERSION.zip" "LakeLogger-v$VERSION"
cd ..
echo "       Created: releases/LakeLogger-v$VERSION.zip"

# Create tar.gz archive
echo "[INFO] Creating tar.gz archive..."
cd releases
tar -czf "LakeLogger-v$VERSION.tar.gz" "LakeLogger-v$VERSION"
cd ..
echo "       Created: releases/LakeLogger-v$VERSION.tar.gz"

echo ""
echo "========================================"
echo "      RELEASE BUILD COMPLETE!"
echo "========================================"
echo ""
echo "  Version:  $VERSION"
echo "  Location: $RELEASE_DIR/"
echo ""
echo "  Contents:"
echo "    - LakeLogger.jar"
echo "    - run.bat (Windows)"
echo "    - run.sh (Mac/Linux)"
echo "    - README.md"
echo "    - LICENSE"
echo ""
echo "  Archives:"
echo "    - releases/LakeLogger-v$VERSION.zip"
echo "    - releases/LakeLogger-v$VERSION.tar.gz"
echo ""
