package com.sandsiv.surveytest

import android.app.Application

class SurveyTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // SDK will be initialized when user launches a survey with their configuration
    }
}
