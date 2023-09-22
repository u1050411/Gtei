package com.trueta.gtei

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.trueta.gtei.ui.theme.GteiTheme

/**
 * GteiActivity serves as the main activity for the application.
 * It initializes the UI and triggers the display of ScreensGtei based on a timed condition.
 */
class GteiActivity : ComponentActivity() {
    // Obtain the ViewModel for this Activity
    val viewModel: ScreensViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Create a mutable state to control the visibility of ScreensGtei
            val showScreenTry = remember { mutableStateOf(false) }

            // Change the showScreenTry state to true after a 3-second delay
            Handler(Looper.getMainLooper()).postDelayed({
                showScreenTry.value = true
            }, 3000)
            ScreensGtei(showScreenTry, viewModel)
        }
    }
}






