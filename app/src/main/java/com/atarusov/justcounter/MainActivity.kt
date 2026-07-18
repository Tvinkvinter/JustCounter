package com.atarusov.justcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.atarusov.justcounter.navigation.SetupNavGraph
import com.atarusov.justcounter.shared_features.analytics.AnalyticsTracker
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            JustCounterTheme {
                navController = rememberNavController()
                SetupNavGraph(navController, analyticsTracker)
            }
        }
    }

}
