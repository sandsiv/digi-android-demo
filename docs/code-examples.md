# Code Examples

## Overview

This guide provides practical code examples for integrating the Digi Android SDK into your application. All examples are based on the demo app implementation.

## 🚀 Basic Integration

### Complete MainActivity Implementation

```kotlin
// MainActivity.kt - Complete implementation
package com.sandsiv.surveytest

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.sandsiv.digi.DigiModule
import com.sandsiv.digi.Margins

class MainActivity : AppCompatActivity() {
    
    private lateinit var apiUrlInput: TextInputEditText
    private lateinit var surveyIdInput: TextInputEditText
    private lateinit var languageInput: TextInputEditText
    private lateinit var launchButton: MaterialButton
    private lateinit var sizeRadioGroup: android.widget.RadioGroup
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        apiUrlInput = findViewById(R.id.api_url_input)
        surveyIdInput = findViewById(R.id.survey_id_input)
        languageInput = findViewById(R.id.language_input)
        launchButton = findViewById(R.id.launch_survey_button)
        sizeRadioGroup = findViewById(R.id.size_radio_group)
    }
    
    private fun setupClickListeners() {
        launchButton.setOnClickListener {
            launchSurvey()
        }
    }
    
    private fun launchSurvey() {
        val apiUrl = apiUrlInput.text.toString().trim()
        val surveyIdText = surveyIdInput.text.toString().trim()
        val language = languageInput.text.toString().trim()
        
        if (apiUrl.isEmpty() || surveyIdText.isEmpty() || language.isEmpty()) {
            showError("Please fill in all fields")
            return
        }
        
        val surveyId = surveyIdText.toIntOrNull()
        if (surveyId == null) {
            showError("Survey ID must be a number")
            return
        }
        
        // Initialize SDK with user-provided API URL
        DigiModule.init(
            url = apiUrl,
            context = this
        )
        
        // Calculate margins based on selected size
        val margins = calculateMargins()
        
        // Launch survey
        DigiModule.show(
            surveyId = surveyId,
            language = language,
            context = this,
            margins = margins,
            cornerRadius = 16,
            onResult = { result ->
                when (result) {
                    is Result.Success -> {
                        showToast("Survey completed successfully!")
                        updateStatus("Survey completed")
                    }
                    is Result.Error -> {
                        showError("Survey failed: ${result.message}")
                        updateStatus("Survey failed")
                    }
                }
            }
        )
        
        updateStatus("Launching survey...")
    }
    
    private fun calculateMargins(): Margins {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
        
        val selectedRadioButtonId = sizeRadioGroup.checkedRadioButtonId
        
        return when (selectedRadioButtonId) {
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
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        updateStatus("Error: $message")
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun updateStatus(message: String) {
        findViewById<TextView>(R.id.status_text).text = message
    }
}
```

## 🎨 UI Layout Examples

### Complete Activity Layout

```xml
<!-- activity_main.xml - Complete layout implementation -->
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <!-- Header -->
        <TextView
            android:id="@+id/header_title"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:text="Digi SDK Demo"
            android:textSize="24sp"
            android:textStyle="bold"
            android:gravity="center"
            android:layout_marginBottom="24dp"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />

        <!-- Configuration Card -->
        <com.google.android.material.card.MaterialCardView
            android:id="@+id/config_card"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginBottom="24dp"
            app:cardCornerRadius="8dp"
            app:cardElevation="4dp"
            app:layout_constraintTop_toBottomOf="@id/header_title"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <!-- API URL Input -->
                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginBottom="16dp"
                    android:hint="API URL"
                    app:startIconDrawable="@android:drawable/ic_menu_edit"
                    style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/api_url_input"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="textUri"
                        android:text="https://genie-survey.sandsiv.com/digi_runner.js" />

                </com.google.android.material.textfield.TextInputLayout>

                <!-- Survey ID and Language in one line -->
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:layout_marginBottom="16dp">

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:layout_marginEnd="8dp"
                        android:hint="Survey ID"
                        style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/survey_id_input"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="number"
                            android:text="162" />

                    </com.google.android.material.textfield.TextInputLayout>

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:layout_marginStart="8dp"
                        android:hint="Language"
                        style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/language_input"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="text"
                            android:text="en" />

                    </com.google.android.material.textfield.TextInputLayout>

                </LinearLayout>

                <!-- Size Selectors -->
                <RadioGroup
                    android:id="@+id/size_radio_group"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:gravity="center">

                    <RadioButton
                        android:id="@+id/size_full_screen"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:button="@null"
                        android:drawableTop="@drawable/selector_survey_full_screen"
                        android:gravity="center"
                        android:padding="16dp"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        android:checked="true" />

                    <RadioButton
                        android:id="@+id/size_middle_third"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:button="@null"
                        android:drawableTop="@drawable/selector_survey_middle_third"
                        android:gravity="center"
                        android:padding="16dp"
                        android:background="?attr/selectableItemBackgroundBorderless" />

                    <RadioButton
                        android:id="@+id/size_bottom_third"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:button="@null"
                        android:drawableTop="@drawable/selector_survey_bottom_third"
                        android:gravity="center"
                        android:padding="16dp"
                        android:background="?attr/selectableItemBackgroundBorderless" />

                </RadioGroup>

            </LinearLayout>

        </com.google.android.material.card.MaterialCardView>

        <!-- Launch Button -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/launch_survey_button"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:text="🚀 SHOW SURVEY"
            android:textSize="16sp"
            android:textStyle="bold"
            app:cornerRadius="8dp"
            app:backgroundTint="@color/survey_primary"
            app:layout_constraintTop_toBottomOf="@id/config_card"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />

        <!-- Status Text -->
        <TextView
            android:id="@+id/status_text"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:text="Ready to launch survey"
            android:textAlignment="center"
            android:textColor="@android:color/darker_gray"
            android:textSize="14sp"
            android:layout_marginTop="16dp"
            app:layout_constraintTop_toBottomOf="@id/launch_survey_button"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</ScrollView>
```

## 🎯 Size Selector Icons

### Full Screen Icon

```xml
<!-- ic_survey_full_screen.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#4285F4"
        android:strokeColor="#000000"
        android:strokeWidth="1"
        android:pathData="M3,3h18v18H3z" />
</vector>
```

### Middle Third Icon

```xml
<!-- ic_survey_middle_third.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <!-- Outline of the screen -->
    <path
        android:fillColor="#FFFFFF"
        android:strokeColor="#000000"
        android:strokeWidth="1"
        android:pathData="M3,3h18v18H3z" />
    <!-- Middle third filled -->
    <path
        android:fillColor="#4285F4"
        android:pathData="M3,8h18v8H3z" />
</vector>
```

### Bottom Third Icon

```xml
<!-- ic_survey_bottom_third.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <!-- Outline of the screen -->
    <path
        android:fillColor="#FFFFFF"
        android:strokeColor="#000000"
        android:strokeWidth="1"
        android:pathData="M3,3h18v18H3z" />
    <!-- Bottom third filled -->
    <path
        android:fillColor="#4285F4"
        android:pathData="M3,16h18v5H3z" />
</vector>
```

### Selected State Icons

```xml
<!-- ic_survey_full_screen_selected.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <!-- Full screen filled with purple border -->
    <path
        android:fillColor="#FF6200EE"
        android:strokeColor="#FF6200EE"
        android:strokeWidth="2"
        android:pathData="M3,3h18v18H3z" />
</vector>
```

### Selector States

```xml
<!-- selector_survey_full_screen.xml -->
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_checked="true" 
          android:drawable="@drawable/ic_survey_full_screen_selected" />
    <item android:drawable="@drawable/ic_survey_full_screen" />
</selector>
```

## 🔧 Advanced Examples

### Custom Survey Configuration

```kotlin
// Custom survey configuration class
class SurveyConfiguration {
    var apiUrl: String = "https://genie-survey.sandsiv.com/digi_runner.js"
    var surveyId: Int = 162
    var language: String = "en"
    var size: SurveySize = SurveySize.FULL_SCREEN
    var cornerRadius: Int = 16
    var customParams: Map<String, Any> = emptyMap()
    
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

### Survey Manager Implementation

```kotlin
// Survey manager for handling multiple surveys
class SurveyManager(private val context: Context) {
    private var currentSurveyId: Int? = null
    
    fun launchSurvey(config: SurveyConfiguration, onResult: (Result) -> Unit) {
        // Initialize SDK
        DigiModule.init(
            url = config.apiUrl,
            context = context
        )
        
        // Calculate margins
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
        val margins = config.toMargins(screenHeightDp)
        
        // Launch survey
        DigiModule.show(
            surveyId = config.surveyId,
            language = config.language,
            context = context,
            params = config.customParams,
            margins = margins,
            cornerRadius = config.cornerRadius,
            onResult = { result ->
                currentSurveyId = null
                onResult(result)
            }
        )
        
        currentSurveyId = config.surveyId
    }
    
    fun isSurveyActive(): Boolean = currentSurveyId != null
    
    fun getCurrentSurveyId(): Int? = currentSurveyId
}
```

### Result Handling Examples

```kotlin
// Comprehensive result handling
private fun handleSurveyResult(result: Result) {
    when (result) {
        is Result.Success -> {
            // Extract survey data
            val surveyData = result.data
            val completionTime = result.timestamp
            
            // Log success
            Log.d("Survey", "Survey completed successfully")
            
            // Update analytics
            analytics.trackSurveyCompleted(surveyId, completionTime)
            
            // Show success message
            showSuccessMessage("Survey completed successfully!")
            
            // Navigate to next step
            navigateToNextStep()
        }
        is Result.Error -> {
            // Handle different error types
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
    }
}
```

### Screen Size Adaptation

```kotlin
// Responsive margin calculation
private fun getResponsiveMargins(screenHeightDp: Float, screenWidthDp: Float): Margins {
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

## 🔍 Debug Examples

### WebView Debugging

```kotlin
// Enable WebView debugging
private fun setupWebViewDebugging() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
        WebView.setWebContentsDebuggingEnabled(true)
    }
}

// Console message handling
private fun setupConsoleLogging() {
    webView.webChromeClient = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            Log.d("WebView", "Console: ${consoleMessage.message()}")
            return true
        }
    }
}
```

### Network Logging

```kotlin
// OkHttp logging interceptor
private fun setupNetworkLogging() {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}
```

## 📱 Build Configuration

### App Build.gradle

```gradle
// app/build.gradle - Complete configuration
apply plugin: 'com.android.application'
apply plugin: 'org.jetbrains.kotlin.android'

android {
    compileSdk 34
    namespace 'com.sandsiv.surveytest'

    defaultConfig {
        applicationId "com.sandsiv.surveytest"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = '1.8'
    }
    
    buildFeatures {
        viewBinding true
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.8.0'
    implementation 'androidx.appcompat:appcompat:1.5.0'
    implementation 'com.google.android.material:material:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Digi SDK
    implementation "com.sandsiv:digi:1.0.21"
    
    // OkHttp dependencies
    implementation "com.squareup.okhttp3:okhttp:4.12.0"
    implementation "com.squareup.okhttp3:logging-interceptor:4.12.0"
}
```

### Project Build.gradle

```gradle
// build.gradle - Project level
plugins {
    id 'com.android.application' version '7.3.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.10' apply false
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

### Settings.gradle

```gradle
// settings.gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url "https://europe-maven.pkg.dev/sandsiv-infrastructure/digi-module"
            credentials {
                username = "oauth2accesstoken"
                password = "YOUR_ACCESS_TOKEN_HERE"
            }
        }
    }
}

rootProject.name = "digi-android-demo"
include ':app'
```

## 📋 Complete Implementation Checklist

### Required Files
- [ ] MainActivity.kt with survey logic
- [ ] activity_main.xml with UI layout
- [ ] Size selector icons (6 files)
- [ ] Selector state drawables (3 files)
- [ ] build.gradle with SDK dependency
- [ ] settings.gradle with repository configuration

### Key Implementation Points
- [ ] SDK initialization with user-provided URL
- [ ] Dynamic margin calculation based on screen size
- [ ] Icon-based size selectors with visual feedback
- [ ] Comprehensive result handling
- [ ] Error handling and user feedback
- [ ] Debug logging configuration

### Testing Checklist
- [ ] Different survey sizes work correctly
- [ ] Margins calculated properly for different screen sizes
- [ ] Icons show correct selection states
- [ ] Survey results handled appropriately
- [ ] Error scenarios handled gracefully

## 🚨 Common Implementation Issues

### Issue: Margins not working
**Solution**: Ensure margins are calculated in DP and use correct parameter order

### Issue: Icons not showing
**Solution**: Check drawable resources and selector configuration

### Issue: Selection states not visible
**Solution**: Verify selector drawable states and RadioButton configuration

### Issue: Build errors
**Solution**: Check Kotlin version compatibility and SDK dependency configuration

## 📖 Next Steps

- Review [Best Practices](best-practices.md) for production recommendations
- Check [Troubleshooting Guide](troubleshooting.md) for common issues
- Read [API Reference](api-reference.md) for detailed method documentation
- Explore [Architecture Overview](architecture.md) for app structure

---

*These code examples provide complete implementation patterns for integrating the Digi Android SDK. Use them as reference for your own implementation.*
