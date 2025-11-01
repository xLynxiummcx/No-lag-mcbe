#!/bin/bash
set -e

echo "[1/3] Checking Python 3..."
# GitHub Windows runners already have Python, update pip
python -m pip install --upgrade pip

echo "[2/3] Installing lazurite 0.6.0..."
python -m pip install lazurite==0.6.0

echo "[3/3] Downloading shaderc for Windows..."
curl -L -o shaderc.exe "https://github.com/devendrn/newb-shader/releases/download/dev/shaderc-win-x64.exe"

# Rename for easier usage in Bash
mv shaderc.exe shaderc
chmod +x shaderc

echo "Setup complete!"