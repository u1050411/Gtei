package com.trueta.gtei

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScreensViewModel : ViewModel() {
    // Initialize your Variables and Screens here
    val variablesGtei = Variables()
    val screensGtei = Screens()

    val listInitial = screensGtei.start.listScreens

    private val _screens = MutableStateFlow(value = listInitial)
    val screens = _screens.asStateFlow()

    private val _selectedScreen = MutableStateFlow<Screen?>(null)
    val selectedScreen = _selectedScreen.asStateFlow()

    init {
        // Fetch your list of screens from your data source here
    }

    fun onScreenSelected(screen: Screen) {
        _selectedScreen.value = screen
    }
}