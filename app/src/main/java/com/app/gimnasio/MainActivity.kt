package com.app.gimnasio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.app.gimnasio.ui.navigation.GimnasioNavGraph
import com.app.gimnasio.ui.theme.GimnasioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GimnasioTheme {
                val navController = rememberNavController()
                GimnasioNavGraph(navController = navController)
            }
        }
    }
}
