package com.trueta.gtei

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun ScreenSelector(listScreen: List<Screen>, onScreenSelected: (Screen) -> Unit) {
    var selectedScreen by remember { mutableStateOf<Screen?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Select a screen:")
        listScreen.forEach { screen ->
            Button(onClick = {
                selectedScreen = screen
                onScreenSelected(screen)
            }) {
                Text(text = screen.name)
            }
        }

        Text(text = "Selected screen: ${selectedScreen?.name ?: "None"}")
    }
}

@Composable
fun DisplaySelectedScreen(screen: Screen) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Name: ${screen.name}")
        Text(text = "Focus: ${screen.focus}")
        Text(text = "Message: ${screen.message}")
        // Add more UI elements to display other properties of the Screen object
    }
}