@echo off
REM ========================================
REM   Lake Logger - Windows Build Script
REM ========================================

setlocal enabledelayedexpansion

echo.
echo   ___       _         _
echo  / __\ __ _| | _____ / /  ___   __ _  __ _  ___ _ __
echo / /   / _` | |/ / _ \ /  / _ \ / _` |/ _` |/ _ \ '__|
echo / /___| (_| |   <  __/ /__\ (_) | (_| | (_| |  __/ |
echo \____/ \__,_|_|\_\___\____/\___/ \__, |\__, |\___|_|
echo                                  |___/ |___/
echo.
echo ========================================
echo          BUILD SCRIPT v1.0.0
echo ========================================
echo.

cd /d "%~dp0.."

REM Check if Maven is installed
where mvn >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven is not installed or not in PATH
    echo         Please install Maven from: https://maven.apache.org/download.cgi
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

REM Display versions
echo [INFO] Checking environment...
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    echo        Java version: %%~i
)
for /f "tokens=3" %%i in ('mvn -version 2^>^&1 ^| findstr /i "Apache Maven"') do (
    echo        Maven version: %%i
)
echo.

echo [INFO] Cleaning previous build...
call mvn clean -q

echo [INFO] Building Lake Logger...
call mvn package -DskipTests -q

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Build failed! Check the output above for details.
    echo.
    pause
    exit /b 1
)

REM Create dist folder
if not exist "dist" mkdir "dist"
copy "target\LakeLogger.jar" "dist\LakeLogger.jar" >nul 2>&1

echo.
echo ========================================
echo        BUILD SUCCESSFUL!
echo ========================================
echo.
echo   Output: dist\LakeLogger.jar
echo.
echo   To run: scripts\run.bat
echo           -or-
echo           java -jar dist\LakeLogger.jar
echo.
pause
