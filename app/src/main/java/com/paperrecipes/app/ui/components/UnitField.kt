package com.paperrecipes.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.PopupProperties
import com.paperrecipes.app.data.model.IngredientUnit

@Composable
fun UnitField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    /**
     * Variables that remember the current state of the component and the custom
     * option that switches the field into free text entry.
      */
    var expanded by remember { mutableStateOf(false) }
    var customMode by remember { mutableStateOf(value.isNotBlank() && value !in IngredientUnit.PRESETS)}

    Box(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (customMode) onValueChange(it) },
            readOnly = !customMode,
            label = { Text("UNIT", style = MaterialTheme.typography.labelMedium) },
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "Pick unit",
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { expanded = true },
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            ),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = true),
        ) {
            IngredientUnit.PRESETS.forEach { preset ->
                DropdownMenuItem(
                    text = { Text(preset, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        customMode = false
                        onValueChange(preset)
                        expanded =false
                    },
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = {
                    Text(
                        "Custom unit...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                onClick = {
                    customMode = true
                    onValueChange("")
                    expanded = false
                },
            )
        }
    }
}