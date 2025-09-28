package com.atarusov.justcounter.navigation

sealed class Screen(val route: String) {
    object CounterScreen : Screen(route = "counter_screen")
}