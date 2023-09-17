package com.trueta.gtei

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.trueta.gtei.ui.theme.GteiTheme

class GteiActivity : ComponentActivity() {
    val viewModel: ScreensViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GteiTheme {
                Surface {
                    val selectedScreen by viewModel.selectedScreen.collectAsState()
                    val screens by viewModel.screens.collectAsState(initial = emptyList())

                    ScreenSelector(listScreen = screens) { // Always display the ScreenSelector
                        viewModel.onScreenSelected(it)
                    }

                    if (selectedScreen == null) { // Display this text only if no screen is selected
                        Text("No screen selected.")
                    }
                }
            }
        }
    }
}