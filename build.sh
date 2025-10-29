#!/bin/bash
# Build script for Digi Android Demo App
# This script ensures proper authentication before building

echo "🚀 Building Digi Android Demo App..."

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    echo "❌ Google Cloud SDK not found. Please install it first:"
    echo "   https://cloud.google.com/sdk/docs/install"
    exit 1
fi

# Check if authenticated
if ! gcloud auth application-default print-access-token &> /dev/null; then
    echo "🔐 Not authenticated. Please run:"
    echo "   gcloud auth application-default login"
    exit 1
fi

# Set up authentication
echo "🔑 Setting up authentication..."
./refresh-token.sh
export GOOGLE_ACCESS_TOKEN=$(gcloud auth application-default print-access-token)

# Build the project
echo "🔨 Building project..."
./gradlew clean assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "To install on device:"
    echo "   adb install -r app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Build failed!"
    exit 1
fi

