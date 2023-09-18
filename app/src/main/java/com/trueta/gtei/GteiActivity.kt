package com.trueta.gtei

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trueta.gtei.ui.theme.GteiTheme

class GteiActivity : ComponentActivity() {
    val viewModel: ScreensViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val selectedScreen by viewModel.selectedScreen.collectAsState()
            val message by viewModel.message.collectAsState()

            GteiTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(5.dp)
                            .weight(.095f),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        MessageDisplay(message)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .weight(.005f),
                        contentAlignment = Alignment.BottomCenter,
                    ) {}
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.9f)
                            .background(Color.White)
                            .padding(
                                start = 5.dp,
                                end = 5.dp,
                                top = 1.dp,
                                bottom = 5.dp
                            ), // padding right and left, top and bottom
                        contentAlignment = Alignment.TopCenter
                    ) {
                        selectedScreen?.let {
                            TryDisplay(it, viewModel)  // Pass viewModel as an argument
                        }
                    }
                }
            }
        }
    }
}