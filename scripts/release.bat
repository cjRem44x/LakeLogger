@echo off
REM ========================================
REM   Lake Logger - Windows Release Script
REM ========================================

setlocal enabledelayedexpansion

echo.
echo ========================================
echo       LAKE LOGGER RELEASE BUILDER
echo ========================================
echo.

cd /d "%~dp0.."

REM Get version from pom.xml
for /f "tokens=2 delims=<>" %%i in ('findstr /r "<version>.*</version>" pom.xml ^| findstr /v "parent" ^| head -1') do (
    set VERSION=%%i
    goto :found_version
)
:found_version
if "%VERSION%"=="" set VERSION=1.0.0

echo [INFO] Building Lake Logger v%VERSION%
echo.

REM Check prerequisites
where mvn >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven is not installed
    pause
    exit /b 1
)

where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not installed
    pause
    exit /b 1
)

REM Clean and build
echo [INFO] Cleaning previous build...
call mvn clean -q

echo [INFO] Building release JAR...
call mvn package -DskipTests -q

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)

REM Create release directory
set RELEASE_DIR=releases\LakeLogger-v%VERSION%
echo [INFO] Creating release directory: %RELEASE_DIR%

if exist "%RELEASE_DIR%" rmdir /s /q "%RELEASE_DIR%"
mkdir "%RELEASE_DIR%"

REM Copy files
echo [INFO] Copying release files...
copy "target\LakeLogger.jar" "%RELEASE_DIR%\LakeLogger.jar" >nul
copy "README.md" "%RELEASE_DIR%\README.md" >nul
copy "LICENSE" "%RELEASE_DIR%\LICENSE" >nul

REM Create run scripts for release
echo @echo off > "%RELEASE_DIR%\run.bat"
echo java --enable-native-access=ALL-UNNAMED -jar LakeLogger.jar >> "%RELEASE_DIR%\run.bat"

echo #!/bin/bash > "%RELEASE_DIR%\run.sh"
echo java --enable-native-access=ALL-UNNAMED -jar LakeLogger.jar >> "%RELEASE_DIR%\run.sh"

REM Create zip if 7zip or tar available
where tar >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo [INFO] Creating zip archive...
    cd releases
    tar -acf "LakeLogger-v%VERSION%.zip" "LakeLogger-v%VERSION%"
    cd ..
    echo        Created: releases\LakeLogger-v%VERSION%.zip
)

echo.
echo ========================================
echo       RELEASE BUILD COMPLETE!
echo ========================================
echo.
echo   Version:  %VERSION%
echo   Location: %RELEASE_DIR%\
echo.
echo   Contents:
echo     - LakeLogger.jar
echo     - run.bat (Windows)
echo     - run.sh (Mac/Linux)
echo     - README.md
echo     - LICENSE
echo.

pause
