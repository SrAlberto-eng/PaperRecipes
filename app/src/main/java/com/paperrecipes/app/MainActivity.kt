package com.paperrecipes.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paperrecipes.app.ui.navigation.AppNavigation
import com.paperrecipes.app.ui.theme.PaperRecipesTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaperRecipesTheme {
                AppNavigation()
            }
        }
    }
}