#!/bin/bash

BUILD_SCRIPT="./Build.sh"
BUILD_DIR="build"
ANDROID_BUILD_DIR="$BUILD_DIR/Android"
ZIP_FILE="$BUILD_DIR/Android.zip"

# Run the build script
echo "Running build script..."
$BUILD_SCRIPT -p Android -m your_material_file -t 4

# Ensure the build directory exists
if [ -d "$ANDROID_BUILD_DIR" ]; then
  echo "Creating zip archive of build files..."
  zip -r $ZIP_FILE $ANDROID_BUILD_DIR/*
  echo "Zip archive created at $ZIP_FILE"
else
  echo "Error: Build directory $ANDROID_BUILD_DIR does not exist"
  exit 1
fi
