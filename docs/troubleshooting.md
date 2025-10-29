# Troubleshooting Guide

## Overview

This guide helps you diagnose and resolve common issues when integrating the Digi Android SDK into your application.

## 🔍 Common Issues and Solutions

### Build Issues

#### Issue: "Could not resolve com.sandsiv:digi"
**Symptoms**: Build fails with dependency resolution error
**Causes**: 
- Incorrect repository configuration
- Invalid access token
- Network connectivity issues

**Solutions**:
1. **Check Repository Configuration**
   ```gradle
   // settings.gradle
   maven {
       url "https://europe-maven.pkg.dev/sandsiv-infrastructure/digi-module"
       credentials {
           username = "oauth2accesstoken"
           password = "YOUR_ACCESS_TOKEN_HERE"
       }
   }
   ```

2. **Verify Access Token**
   ```bash
   gcloud auth application-default print-access-token
   ```

3. **Check Network Connectivity**
   ```bash
   curl -H "Authorization: Bearer YOUR_TOKEN" \
        "https://europe-maven.pkg.dev/sandsiv-infrastructure/digi-module/com/sandsiv/digi/1.0.5/digi-1.0.5.pom"
   ```

#### Issue: "Plugin with id 'dagger.hilt.android.plugin' not found"
**Symptoms**: Build fails with plugin not found error
**Causes**: Complex build configuration with unnecessary dependencies

**Solutions**:
1. **Simplify build.gradle**
   ```gradle
   // Remove unnecessary plugins
   apply plugin: 'com.android.application'
   apply plugin: 'org.jetbrains.kotlin.android'
   // Remove: apply plugin: 'dagger.hilt.android.plugin'
   ```

2. **Clean and rebuild**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

#### Issue: "Incompatible classes were found in dependencies"
**Symptoms**: Kotlin compilation errors with version conflicts
**Causes**: Kotlin version mismatch between SDK and app

**Solutions**:
1. **Update Kotlin Version**
   ```gradle
   // build.gradle
   plugins {
       id 'org.jetbrains.kotlin.android' version '1.9.10' apply false
   }
   ```

2. **Force Kotlin Version**
   ```gradle
   // app/build.gradle
   kotlinOptions {
       jvmTarget = '1.8'
       freeCompilerArgs += ["-Xskip-metadata-version-check"]
   }
   ```

### Runtime Issues

#### Issue: "Survey not loading" / "WebView shows spinner"
**Symptoms**: Survey launches but shows loading spinner indefinitely
**Causes**: 
- Network connectivity issues
- Script URL not accessible
- WebView configuration problems

**Solutions**:
1. **Check Network Permissions**
   ```xml
   <!-- AndroidManifest.xml -->
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
   ```

2. **Enable Cleartext Traffic**
   ```xml
   <application
       android:usesCleartextTraffic="true"
       ... >
   ```

3. **Verify Script URL**
   ```kotlin
   // Test URL accessibility
   val scriptUrl = "https://genie-survey.sandsiv.com/digi_runner.js"
   // Verify this URL is accessible from your device/emulator
   ```

4. **Enable WebView Debugging**
   ```kotlin
   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
       WebView.setWebContentsDebuggingEnabled(true)
   }
   ```

#### Issue: "Survey positioning wrong" / "Margins not working"
**Symptoms**: Survey appears in wrong position or size
**Causes**: 
- Incorrect margin calculation
- Wrong parameter order in Margins constructor
- Screen density issues

**Solutions**:
1. **Check Margin Parameter Order**
   ```kotlin
   // Correct order: (top, bottom, start, end)
   val margins = Margins(
       top = 100,      // Top margin
       bottom = 50,    // Bottom margin
       start = 0,      // Start margin (left in LTR)
       end = 0         // End margin (right in LTR)
   )
   ```

2. **Verify Screen Density Calculation**
   ```kotlin
   private fun calculateMargins(): Margins {
       val displayMetrics = DisplayMetrics()
       windowManager.defaultDisplay.getMetrics(displayMetrics)
       val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
       
       // Use DP values, not pixels
       val marginDp = (screenHeightDp * 0.33).toInt()
       return Margins(marginDp, marginDp, 0, 0)
   }
   ```

3. **Debug Margin Values**
   ```kotlin
   Log.d("Survey", "Screen height: ${screenHeightDp}dp")
   Log.d("Survey", "Calculated margins: $margins")
   ```

#### Issue: "Survey crashes on launch"
**Symptoms**: App crashes when launching survey
**Causes**: 
- Missing SDK initialization
- Invalid parameters
- Context issues

**Solutions**:
1. **Ensure SDK Initialization**
   ```kotlin
   // Initialize SDK before launching survey
   DigiModule.init(
       url = apiUrl,
       context = this
   )
   ```

2. **Validate Input Parameters**
   ```kotlin
   private fun validateInput(): Boolean {
       val apiUrl = apiUrlInput.text.toString().trim()
       val surveyIdText = surveyIdInput.text.toString().trim()
       val language = languageInput.text.toString().trim()
       
       return apiUrl.isNotEmpty() && 
              surveyIdText.isNotEmpty() && 
              language.isNotEmpty()
   }
   ```

3. **Check Context Usage**
   ```kotlin
   // Use Activity context, not Application context
   DigiModule.show(
       surveyId = surveyId,
       language = language,
       context = this, // Activity context
       onResult = { result -> ... }
   )
   ```

### Advanced Settings Issues

#### Issue: "ClassCastException: FrameLayout cannot be cast to TextInputEditText"
**Symptoms**: App crashes when accessing metadata input fields
**Causes**: 
- Incorrect casting of TextInputLayout children
- TextInputLayout structure has FrameLayout wrapper

**Solutions**:
1. **Use Correct Casting Chain**
   ```kotlin
   // WRONG - Direct casting
   val nameInput = (nameLayout.getChildAt(0) as TextInputEditText)
   
   // CORRECT - Through FrameLayout wrapper
   val nameInput = (nameLayout.getChildAt(0) as FrameLayout).getChildAt(0) as TextInputEditText
   ```

2. **Apply to All Methods**
   ```kotlin
   // In saveAdvancedSettings, loadAdvancedSettings, getAdvancedSettings
   val nameInput = (nameLayout.getChildAt(0) as FrameLayout).getChildAt(0) as TextInputEditText
   val valueInput = (valueLayout.getChildAt(0) as FrameLayout).getChildAt(0) as TextInputEditText
   ```

#### Issue: "Metadata not saving" / "Settings not persisting"
**Symptoms**: Advanced settings lost on app restart
**Causes**: 
- SharedPreferences not properly configured
- Save/load methods not called

**Solutions**:
1. **Initialize SharedPreferences**
   ```kotlin
   private lateinit var sharedPreferences: SharedPreferences
   
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       sharedPreferences = getSharedPreferences("survey_settings", Context.MODE_PRIVATE)
   }
   ```

2. **Call Save Methods**
   ```kotlin
   private fun launchSurvey() {
       // Save settings before launching
       saveSettings()
       saveAdvancedSettings()
       
       // Launch survey...
   }
   ```

#### Issue: "Button shadows appearing" / "MaterialButton elevation"
**Symptoms**: Add/delete buttons have unwanted shadows
**Causes**: 
- MaterialButton default elevation
- State list animator effects

**Solutions**:
1. **Remove Elevation**
   ```xml
   <com.google.android.material.button.MaterialButton
       android:id="@+id/add_metadata_button"
       app:elevation="0dp"
       android:stateListAnimator="@null" />
   ```

2. **Use Transparent Background**
   ```xml
   android:backgroundTint="@android:color/transparent"
   ```

### UI Issues

#### Issue: "Icons not showing" / "Size selectors not visible"
**Symptoms**: Size selector icons not displayed
**Causes**: 
- Missing drawable resources
- Incorrect selector configuration
- Layout issues

**Solutions**:
1. **Check Drawable Resources**
   ```xml
   <!-- Ensure all icon files exist -->
   ic_survey_full_screen.xml
   ic_survey_middle_third.xml
   ic_survey_bottom_third.xml
   ic_survey_full_screen_selected.xml
   ic_survey_middle_third_selected.xml
   ic_survey_bottom_third_selected.xml
   ```

2. **Verify Selector Configuration**
   ```xml
   <!-- selector_survey_full_screen.xml -->
   <selector xmlns:android="http://schemas.android.com/apk/res/android">
       <item android:state_checked="true" 
             android:drawable="@drawable/ic_survey_full_screen_selected" />
       <item android:drawable="@drawable/ic_survey_full_screen" />
   </selector>
   ```

3. **Check RadioButton Configuration**
   ```xml
   <RadioButton
       android:id="@+id/size_full_screen"
       android:button="@null"
       android:drawableTop="@drawable/selector_survey_full_screen"
       android:gravity="center"
       android:padding="16dp" />
   ```

#### Issue: "Selection states not working"
**Symptoms**: Icons don't change when selected
**Causes**: 
- Incorrect selector configuration
- RadioButton state handling issues

**Solutions**:
1. **Check Selector States**
   ```xml
   <!-- Ensure both normal and checked states are defined -->
   <selector xmlns:android="http://schemas.android.com/apk/res/android">
       <item android:state_checked="true" 
             android:drawable="@drawable/ic_survey_full_screen_selected" />
       <item android:drawable="@drawable/ic_survey_full_screen" />
   </selector>
   ```

2. **Verify RadioButton State**
   ```kotlin
   // Check if RadioButton is properly checked
   val selectedId = sizeRadioGroup.checkedRadioButtonId
   Log.d("Survey", "Selected ID: $selectedId")
   ```

### Network Issues

#### Issue: "Network request failed" / "401 Unauthorized"
**Symptoms**: Network requests fail with authentication errors
**Causes**: 
- Invalid access token
- Network security restrictions
- CORS issues

**Solutions**:
1. **Refresh Access Token**
   ```bash
   gcloud auth application-default print-access-token
   ```

2. **Check Network Security**
   ```xml
   <application
       android:usesCleartextTraffic="true"
       android:networkSecurityConfig="@xml/network_security_config"
       ... >
   ```

3. **Verify Script URL**
   ```kotlin
   // Test script URL accessibility
   val scriptUrl = "https://genie-survey.sandsiv.com/digi_runner.js"
   // Ensure this URL is accessible from your device
   ```

#### Issue: "Script not loading" / "External script blocked"
**Symptoms**: WebView shows error or blank page
**Causes**: 
- Network security restrictions
- Mixed content issues
- Script URL accessibility

**Solutions**:
1. **Enable Mixed Content**
   ```kotlin
   webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
   ```

2. **Allow File Access**
   ```kotlin
   webView.settings.allowFileAccess = true
   webView.settings.allowFileAccessFromFileURLs = true
   webView.settings.allowUniversalAccessFromFileURLs = true
   ```

3. **Check Script URL**
   ```kotlin
   // Verify script URL is accessible
   val scriptUrl = "https://genie-survey.sandsiv.com/digi_runner.js"
   // Test in browser first
   ```

## 🔧 Debugging Techniques

### Enable Debug Logging

**1. WebView Console Logging**
```kotlin
webView.webChromeClient = object : WebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        Log.d("WebView", "Console: ${consoleMessage.message()}")
        return true
    }
}
```

**2. Network Logging**
```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
```

**3. SDK Debug Logging**
```kotlin
// Add debug logs throughout the flow
Log.d("Survey", "Initializing SDK with URL: $apiUrl")
Log.d("Survey", "Launching survey: ID=$surveyId, Language=$language")
Log.d("Survey", "Calculated margins: $margins")
```

### Common Debug Commands

**1. Check Device Logs**
```bash
adb logcat -s SurveyTest:D
```

**2. Check WebView Logs**
```bash
adb logcat -s WebView
```

**3. Check Network Logs**
```bash
adb logcat -s OkHttp
```

### Debug Checklist

**Build Issues**:
- [ ] Repository configuration correct
- [ ] Access token valid and current
- [ ] Kotlin version compatible
- [ ] Dependencies resolved

**Runtime Issues**:
- [ ] Network permissions added
- [ ] Cleartext traffic enabled
- [ ] Script URL accessible
- [ ] WebView debugging enabled

**UI Issues**:
- [ ] Drawable resources exist
- [ ] Selector states configured
- [ ] RadioButton state handling
- [ ] Layout constraints correct

**Network Issues**:
- [ ] Access token refreshed
- [ ] Network security configured
- [ ] Script URL accessible
- [ ] Mixed content allowed

## 🚨 Emergency Troubleshooting

### Quick Fixes

**1. Clean and Rebuild**
```bash
./gradlew clean
./gradlew build
```

**2. Clear Gradle Cache**
```bash
./gradlew clean
rm -rf ~/.gradle/caches
./gradlew build
```

**3. Reset Emulator/Device**
```bash
adb shell pm clear com.sandsiv.surveytest
```

**4. Check Network Connectivity**
```bash
adb shell ping genie-survey.sandsiv.com
```

### Fallback Solutions

**1. Use Local AAR**
```gradle
// If artifact registry fails, use local AAR
implementation files('libs/digi-1.0.5.aar')
```

**2. Disable Proguard**
```gradle
buildTypes {
    release {
        minifyEnabled false
        // Disable proguard temporarily
    }
}
```

**3. Use Debug Build**
```gradle
buildTypes {
    debug {
        debuggable true
        minifyEnabled false
    }
}
```

## 📋 Troubleshooting Checklist

### Pre-Integration
- [ ] Repository access configured
- [ ] Access token valid
- [ ] Network permissions added
- [ ] Build configuration correct

### During Integration
- [ ] SDK initializes without errors
- [ ] Survey loads correctly
- [ ] Margins calculated properly
- [ ] Results handled appropriately

### Post-Integration
- [ ] All scenarios tested
- [ ] Error handling verified
- [ ] Performance acceptable
- [ ] User experience smooth

## 📖 Additional Resources

### Documentation
- [SDK Integration Guide](sdk-integration.md)
- [Configuration Guide](configuration.md)
- [Code Examples](code-examples.md)
- [Best Practices](best-practices.md)

### Support
- Check logs for specific error messages
- Verify network connectivity
- Test with different devices/emulators
- Review SDK documentation

## 🚨 When to Contact Support

Contact support if you encounter:
- Persistent build failures after following all solutions
- Runtime crashes that can't be resolved
- Network issues that persist across devices
- SDK behavior that doesn't match documentation

**Include in your support request**:
- Complete error logs
- Device/emulator information
- Build configuration
- Steps to reproduce the issue

---

*This troubleshooting guide covers the most common issues and their solutions. If you encounter issues not covered here, check the logs and follow the debugging techniques outlined.*

