package com.sandsiv.surveytest

import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences
    
    companion object {
        private const val PREFS_NAME = "survey_settings"
        private const val KEY_API_URL = "api_url"
        private const val KEY_SURVEY_ID = "survey_id"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_SIZE_SELECTION = "size_selection"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        
        initializeViews()
        loadSavedSettings()
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
    
    private fun loadSavedSettings() {
        // Load saved API URL
        val savedApiUrl = sharedPreferences.getString(KEY_API_URL, "https://genie-survey.sandsiv.com/digi_runner.js")
        apiUrlInput.setText(savedApiUrl)
        
        // Load saved Survey ID
        val savedSurveyId = sharedPreferences.getString(KEY_SURVEY_ID, "162")
        surveyIdInput.setText(savedSurveyId)
        
        // Load saved Language
        val savedLanguage = sharedPreferences.getString(KEY_LANGUAGE, "en")
        languageInput.setText(savedLanguage)
        
        // Load saved size selection
        val savedSizeSelection = sharedPreferences.getInt(KEY_SIZE_SELECTION, R.id.size_full_screen)
        sizeRadioGroup.check(savedSizeSelection)
        
        android.util.Log.d("SurveyTest", "Loaded saved settings: API=$savedApiUrl, Survey=$savedSurveyId, Lang=$savedLanguage, Size=$savedSizeSelection")
    }
    
    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        
        // Save current values
        editor.putString(KEY_API_URL, apiUrlInput.text.toString().trim())
        editor.putString(KEY_SURVEY_ID, surveyIdInput.text.toString().trim())
        editor.putString(KEY_LANGUAGE, languageInput.text.toString().trim())
        editor.putInt(KEY_SIZE_SELECTION, sizeRadioGroup.checkedRadioButtonId)
        
        editor.apply()
        
        android.util.Log.d("SurveyTest", "Settings saved successfully")
    }

    private fun launchSurvey() {
        // Validate inputs
        val apiUrl = apiUrlInput.text.toString().trim()
        val surveyIdText = surveyIdInput.text.toString().trim()
        val language = languageInput.text.toString().trim()
        
        if (apiUrl.isEmpty()) {
            showError("Please enter API URL")
            return
        }
        
        if (surveyIdText.isEmpty()) {
            showError("Please enter Survey ID")
            return
        }
        
        val surveyId = try {
            surveyIdText.toInt()
        } catch (e: NumberFormatException) {
            showError("Survey ID must be a number")
            return
        }
        
        if (language.isEmpty()) {
            showError("Please enter Language")
            return
        }
        
        // Save settings before launching survey
        saveSettings()
        
        // Update status
        updateStatus("Initializing survey...")
        
        // Calculate margins based on size selection
        val margins = calculateMargins()
        android.util.Log.d("SurveyTest", "Calculated margins: $margins")
        
        // Initialize DigiModule with user-provided URL
        DigiModule.init(
            url = apiUrl,
            context = this,
        )
        
        // Add 1-second delay between init and show to let survey fully activate
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            updateStatus("Launching survey...")
            DigiModule.show(
                surveyId = surveyId,
                language = language,
                context = this,
                margins = margins,
                cornerRadius = 16,
                onResult = { result -> 
                    android.util.Log.d("SurveyTest", "Survey result: $result")
                    updateStatus("Survey completed: $result")
                    showToast("Survey completed: $result")
                }
            )
        }, 1000)
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        updateStatus("Error: $message")
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    private fun updateStatus(message: String) {
        findViewById<android.widget.TextView>(R.id.status_text).text = message
    }
    
    private fun calculateMargins(): Margins {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeightPx = displayMetrics.heightPixels
        val screenWidthPx = displayMetrics.widthPixels
        val density = displayMetrics.density
        
        // Convert screen height from pixels to DP
        val screenHeightDp = screenHeightPx / density
        
        android.util.Log.d("SurveyTest", "Screen dimensions: ${screenWidthPx}x${screenHeightPx}px (${screenHeightDp}dp)")
        android.util.Log.d("SurveyTest", "Screen density: $density")
        
        val selectedRadioButtonId = sizeRadioGroup.checkedRadioButtonId
        
        android.util.Log.d("SurveyTest", "Selected radio button ID: $selectedRadioButtonId")
        
        val margins = when (selectedRadioButtonId) {
            R.id.size_full_screen -> {
                // No margins - full screen
                android.util.Log.d("SurveyTest", "Using full screen margins")
                Margins(0, 0, 0, 0)  // top, bottom, start, end
            }
            R.id.size_middle_third -> {
                // Center the survey in the middle third of the screen
                // Equal top and bottom margins to center the survey
                val marginDp = (screenHeightDp * 0.33).toInt()
                android.util.Log.d("SurveyTest", "Middle 1/3 margins: top=${marginDp}dp, bottom=${marginDp}dp")
                Margins(marginDp, marginDp, 0, 0)  // top, bottom, start, end
            }
            R.id.size_bottom_third -> {
                // Position survey in bottom third
                // Large top margin to push survey to bottom, no bottom margin
                val topMarginDp = (screenHeightDp * 0.67).toInt()
                android.util.Log.d("SurveyTest", "Bottom 1/3 margins: top=${topMarginDp}dp")
                Margins(topMarginDp, 0, 0, 0)  // top, bottom, start, end
            }
            else -> {
                // Default to full screen
                android.util.Log.d("SurveyTest", "Default to full screen margins")
                Margins(0, 0, 0, 0)  // top, bottom, start, end
            }
        }
        
        android.util.Log.d("SurveyTest", "Final margins: $margins")
        return margins
    }
}
