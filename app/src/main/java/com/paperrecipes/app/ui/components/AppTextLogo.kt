package com.paperrecipes.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppTextLogo() {
    Text(
        text = "Paper",
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp)
    )

    Text (
        text = "Recipes",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp)
    )
}