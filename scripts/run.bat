@echo off
REM ========================================
REM   Lake Logger - Windows Run Script
REM ========================================

setlocal

cd /d "%~dp0.."

REM Check for JAR in dist folder first, then target
set JAR_PATH=dist\LakeLogger.jar
if not exist "%JAR_PATH%" (
    set JAR_PATH=target\LakeLogger.jar
)

if not exist "%JAR_PATH%" (
    echo [ERROR] LakeLogger.jar not found!
    echo         Please run build.bat first.
    echo.
    pause
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo         Please install Java 17 or later
    echo.
    pause
    exit /b 1
)

echo Starting Lake Logger...
echo.

REM Run with native access enabled to suppress SQLite warnings
java --enable-native-access=ALL-UNNAMED -jar "%JAR_PATH%"
