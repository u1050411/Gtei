package com.trueta.gtei

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


class GteiActivity : ComponentActivity() {
    val viewModel: ScreensViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val selectedScreen by viewModel.selectedScreen.collectAsState()
            val message by viewModel.message.collectAsState()

            val showScreenTry = remember { mutableStateOf(false) }

            // Cambia el estado después de 3 segundos
            Handler(Looper.getMainLooper()).postDelayed({
                showScreenTry.value = true
            }, 3000)

            ScreensGtei(showScreenTry, selectedScreen, message, viewModel)
        }
    }
}






