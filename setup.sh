#!/bin/bash

MBT_VERSION="0.8.2"
DATA_VERSION="1.20.20.21"

MBT_JAR=env/jar/MaterialBinTool-$MBT_VERSION-all.jar
SHADERC=env/bin/shaderc
DATA_DIR=data/$DATA_VERSION

MBT_JAR_URL="https://github.com/ddf8196/MaterialBinTool/releases/download/v$MBT_VERSION/MaterialBinTool-$MBT_VERSION-all.jar"

CPU_ARCH=$(uname -m)
if [ $CPU_ARCH == "x86_64" ]; then
  CPU_ARCH="x86_64"
elif [ $CPU_ARCH == "aarch64" ]; then
  CPU_ARCH="arm64"
elif [ $CPU_ARCH == "armv7l" ] || [ $CPU_ARCH == "armv8l" ]; then
  CPU_ARCH="arm32"
else
  echo "Error: No shaderc binary found for $CPU_ARCH"
  exit 1;
fi

SHADERC_URL="https://github.com/devendrn/RenderDragonSourceCodeInv/releases/download/v1/shaderc.$CPU_ARCH"

if [ "$1" == "-f" ]; then
  # clean
  rm -rf env data build
fi

if [ ! -f "$MBT_JAR" ]; then
  mkdir -p env/jar
  echo "Downloading MaterialBinTool-$MBT_VERSION-all.jar"
  curl -Lo $MBT_JAR $MBT_JAR_URL
fi

if [ ! -f "$SHADERC" ]; then
  mkdir -p env/bin
  echo "Downloading shaderc $CPU_ARCH"
  curl -Lo $SHADERC $SHADERC_URL
  chmod +x $SHADERC
fi

if [ ! -d "data" ]; then
  echo "Cloning RenderDragonData"
  git clone https://github.com/ddf8196/RenderDragonData.git data
elif [ ! -d "$DATA_DIR" ]; then
  cd data
  git pull
fi
