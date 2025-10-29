#!/bin/bash
# Refresh Google Cloud access token for Artifact Registry

echo "🔄 Refreshing Google Cloud access token..."

# Get fresh token
TOKEN=$(gcloud auth application-default print-access-token 2>/dev/null)

if [ $? -eq 0 ] && [ ! -z "$TOKEN" ]; then
    # Update gradle.properties if it exists
    if [ -f "gradle.properties" ]; then
        # Check if GOOGLE_ACCESS_TOKEN already exists in gradle.properties
        if grep -q "GOOGLE_ACCESS_TOKEN=" gradle.properties; then
            sed -i "s/GOOGLE_ACCESS_TOKEN=.*/GOOGLE_ACCESS_TOKEN=$TOKEN/" gradle.properties
        else
            echo "" >> gradle.properties
            echo "# Google Cloud Access Token for Artifact Registry" >> gradle.properties
            echo "GOOGLE_ACCESS_TOKEN=$TOKEN" >> gradle.properties
        fi
        echo "✅ Token updated in gradle.properties"
    else
        echo "📝 Create gradle.properties with:"
        echo "GOOGLE_ACCESS_TOKEN=$TOKEN"
    fi
    
    # Also set environment variable for current session
    export GOOGLE_ACCESS_TOKEN="$TOKEN"
    echo "✅ Token set for current session"
    echo ""
    echo "📋 IMPORTANT: For other developers, they need to:"
    echo "   1. Run: gcloud auth application-default login"
    echo "   2. Run: ./refresh-token.sh"
    echo "   3. Run: export GOOGLE_ACCESS_TOKEN=\$(gcloud auth application-default print-access-token)"
    echo "   4. Then build: ./gradlew assembleDebug"
    echo ""
    echo "🎉 Token refresh completed successfully!"
else
    echo "❌ Failed to get access token. Please run:"
    echo "   gcloud auth application-default login"
    exit 1
fi
