#!/bin/bash

BUILD_DIR=build/Android
ZIP_FILE=build/Android.zip

echo "Creating zip archive of build files..."

# Ensure the build directory exists
if [ -d "$BUILD_DIR" ]; then
  cd $BUILD_DIR
  zip -r ../Android.zip ./*
  cd -
  echo "Zip archive created at $ZIP_FILE"
else
  echo "Error: Build directory $BUILD_DIR does not exist"
  exit 1
fi
