# Configuration Guide

## Overview

This guide explains how to configure the Digi SDK for different use cases, including survey parameters, UI customization, and advanced settings.

## 🎯 Survey Configuration

### Basic Survey Parameters

```kotlin
// Essential parameters for every survey
DigiModule.show(
    surveyId = 162,                    // Required: Numeric survey identifier
    language = "en",                   // Required: Language code (ISO 639-1)
    context = this,                    // Required: Android context
    onResult = { result -> ... }       // Required: Result callback
)
```

### Optional Parameters

```kotlin
DigiModule.show(
    surveyId = 162,
    language = "en",
    context = this,
    params = customParams,             // Optional: Additional parameters
    margins = customMargins,           // Optional: Survey positioning
    cornerRadius = 16,                 // Optional: Corner radius in DP
    onResult = { result -> ... }
)
```

## 📱 Survey Positioning

### Margin System

The SDK uses a margin system to position surveys on screen:

```kotlin
data class Margins(
    val top: Int = 0,      // Top margin in DP
    val bottom: Int = 0,   // Bottom margin in DP
    val start: Int = 0,    // Start margin in DP (left in LTR)
    val end: Int = 0       // End margin in DP (right in LTR)
)
```

### Common Positioning Patterns

#### Full Screen
```kotlin
val fullScreenMargins = Margins(0, 0, 0, 0)
```

#### Centered (Middle Third)
```kotlin
val screenHeightDp = screenHeightPx / density
val marginDp = (screenHeightDp * 0.33).toInt()
val centeredMargins = Margins(marginDp, marginDp, 0, 0)
```

#### Bottom Positioned
```kotlin
val screenHeightDp = screenHeightPx / density
val topMarginDp = (screenHeightDp * 0.67).toInt()
val bottomMargins = Margins(topMarginDp, 0, 0, 0)
```

#### Custom Positioning
```kotlin
val customMargins = Margins(
    top = 100,      // 100dp from top
    bottom = 50,    // 50dp from bottom
    start = 20,     // 20dp from start
    end = 20        // 20dp from end
)
```

## 🎨 Visual Customization

### Corner Radius

```kotlin
DigiModule.show(
    // ... other parameters
    cornerRadius = 16,  // 16dp corner radius
    onResult = { result -> ... }
)
```

**Common corner radius values:**
- `0` - Sharp corners (rectangular)
- `8` - Subtle rounding
- `16` - Standard Material Design
- `24` - Pronounced rounding
- `32` - Very rounded

### Survey Size Calculation

```kotlin
private fun calculateMargins(): Margins {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
    
    return when (selectedSize) {
        R.id.size_full_screen -> Margins(0, 0, 0, 0)
        R.id.size_middle_third -> {
            val marginDp = (screenHeightDp * 0.33).toInt()
            Margins(marginDp, marginDp, 0, 0)
        }
        R.id.size_bottom_third -> {
            val topMarginDp = (screenHeightDp * 0.67).toInt()
            Margins(topMarginDp, 0, 0, 0)
        }
        else -> Margins(0, 0, 0, 0)
    }
}
```

## 🔧 Advanced Configuration

### Custom Parameters

Pass additional data to surveys:

```kotlin
val customParams = hashMapOf(
    "customerId" to "12345",
    "sessionId" to "abc123",
    "userType" to "premium",
    "metadata" to "custom_data",
    "timestamp" to System.currentTimeMillis()
)

DigiModule.show(
    surveyId = 162,
    language = "en",
    params = customParams,
    context = this,
    onResult = { result -> ... }
)
```

### Dynamic Configuration

```kotlin
class SurveyConfig {
    var apiUrl: String = "https://genie-survey.sandsiv.com/digi_runner.js"
    var surveyId: Int = 162
    var language: String = "en"
    var size: SurveySize = SurveySize.FULL_SCREEN
    var cornerRadius: Int = 16
    
    fun toMargins(screenHeightDp: Float): Margins {
        return when (size) {
            SurveySize.FULL_SCREEN -> Margins(0, 0, 0, 0)
            SurveySize.MIDDLE_THIRD -> {
                val marginDp = (screenHeightDp * 0.33).toInt()
                Margins(marginDp, marginDp, 0, 0)
            }
            SurveySize.BOTTOM_THIRD -> {
                val topMarginDp = (screenHeightDp * 0.67).toInt()
                Margins(topMarginDp, 0, 0, 0)
            }
        }
    }
}

enum class SurveySize {
    FULL_SCREEN, MIDDLE_THIRD, BOTTOM_THIRD
}
```

## 🌐 Network Configuration

### Script URL Configuration

```kotlin
// Initialize with your script URL
DigiModule.init(
    url = "https://your-domain.com/digi_runner.js",
    context = this
)
```

### Environment-Specific URLs

```kotlin
class EnvironmentConfig {
    companion object {
        const val PRODUCTION_URL = "https://survey.yourcompany.com/digi_runner.js"
        const val STAGING_URL = "https://staging-survey.yourcompany.com/digi_runner.js"
        const val DEVELOPMENT_URL = "https://dev-survey.yourcompany.com/digi_runner.js"
    }
}

// Use based on build variant
val scriptUrl = when (BuildConfig.BUILD_TYPE) {
    "release" -> EnvironmentConfig.PRODUCTION_URL
    "staging" -> EnvironmentConfig.STAGING_URL
    else -> EnvironmentConfig.DEVELOPMENT_URL
}
```

## 🔍 Debug Configuration

### Enable Debug Logging

```kotlin
// Enable WebView debugging
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
    WebView.setWebContentsDebuggingEnabled(true)
}
```

### Console Message Handling

```kotlin
webView.webChromeClient = object : WebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        Log.d("SurveyWebView", "Console: ${consoleMessage.message()}")
        return true
    }
}
```

### Network Logging

```kotlin
// Add OkHttp logging interceptor for debugging
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
```

## 📱 Device-Specific Configuration

### Screen Size Adaptation

```kotlin
private fun getOptimalMargins(): Margins {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    
    return when {
        screenHeightDp > 800 -> {
            // Large screens - more generous margins
            val marginDp = (screenHeightDp * 0.25).toInt()
            Margins(marginDp, marginDp, 0, 0)
        }
        screenHeightDp > 600 -> {
            // Medium screens - standard margins
            val marginDp = (screenHeightDp * 0.33).toInt()
            Margins(marginDp, marginDp, 0, 0)
        }
        else -> {
            // Small screens - minimal margins
            val marginDp = (screenHeightDp * 0.1).toInt()
            Margins(marginDp, marginDp, 0, 0)
        }
    }
}
```

### Orientation Handling

```kotlin
override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    
    // Recalculate margins for new orientation
    val newMargins = calculateMargins()
    // Update survey if currently showing
}
```

## 🎯 Result Configuration

### Success Handling

```kotlin
onResult = { result ->
    when (result) {
        is Result.Success -> {
            // Extract survey data
            val surveyData = result.data
            val completionTime = result.timestamp
            
            // Update analytics
            analytics.trackSurveyCompleted(surveyId, completionTime)
            
            // Show success message
            showSuccessMessage("Survey completed successfully!")
            
            // Navigate to next screen
            navigateToNextStep()
        }
        is Result.Error -> {
            // Handle error
            handleSurveyError(result)
        }
    }
}
```

### Error Handling

```kotlin
private fun handleSurveyError(result: Result.Error) {
    when (result.status) {
        401 -> {
            // Authentication error
            showError("Please log in again")
            navigateToLogin()
        }
        404 -> {
            // Survey not found
            showError("Survey not available")
        }
        500 -> {
            // Server error
            showError("Server error. Please try again later.")
        }
        else -> {
            // Generic error
            showError("Survey failed: ${result.message}")
        }
    }
    
    // Log error for debugging
    Log.e("Survey", "Error ${result.status}: ${result.message}")
}
```

## 🔧 Performance Configuration

### Memory Management

```kotlin
override fun onDestroy() {
    super.onDestroy()
    // Clean up WebView resources
    webView?.destroy()
}
```

### Network Optimization

```kotlin
// Configure OkHttp for optimal performance
val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()
```

## 📋 Configuration Checklist

### Required Configuration
- [ ] SDK dependency added
- [ ] Repository configured
- [ ] Permissions added to manifest
- [ ] Network security configured
- [ ] Context passed correctly

### Optional Configuration
- [ ] Custom margins calculated
- [ ] Corner radius set
- [ ] Custom parameters added
- [ ] Error handling implemented
- [ ] Debug logging enabled

### Testing Configuration
- [ ] Different screen sizes tested
- [ ] Various orientations tested
- [ ] Network conditions tested
- [ ] Error scenarios tested
- [ ] Performance validated

## 🚨 Common Configuration Issues

### Issue: Margins not working
**Solution**: Ensure margins are in DP, not pixels, and use correct parameter order

### Issue: Survey not loading
**Solution**: Check network permissions and script URL accessibility

### Issue: Wrong positioning
**Solution**: Verify margin calculations and screen density handling

### Issue: Performance issues
**Solution**: Optimize WebView settings and memory management

## 📖 Next Steps

- Review [UI Customization Guide](ui-customization.md) for visual customization
- Check [Code Examples](code-examples.md) for implementation patterns
- Read [Best Practices](best-practices.md) for production recommendations
- Explore [Troubleshooting Guide](troubleshooting.md) for common issues

---

*This configuration guide covers all aspects of SDK configuration. For specific implementation details, refer to the code examples and integration guide.*
