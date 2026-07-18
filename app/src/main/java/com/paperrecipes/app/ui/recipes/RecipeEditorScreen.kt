package com.paperrecipes.app.ui.recipes

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paperrecipes.app.R
import com.paperrecipes.app.data.model.Ingredient
import com.paperrecipes.app.data.model.Recipe
import com.paperrecipes.app.ui.components.AppTextField
import com.paperrecipes.app.ui.components.SecondaryButton
import com.paperrecipes.app.ui.components.UnitField
import com.paperrecipes.app.ui.theme.PaperRecipesTheme
import java.util.UUID

@Preview
@Composable
fun RecipeEditorPreview(){
    PaperRecipesTheme {
        RecipeEditorScreen(recipeId = null, onSaveRecipe = {}, onBack = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeEditorScreen(
    recipeId: String?,
    onSaveRecipe: () -> Unit,
    onBack: () -> Unit
){
    val scrollState: ScrollState = rememberScrollState()

    val existingRecipe = true

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                        Text (
                        text = stringResource(if (existingRecipe) R.string.exists else R.string.not_exists),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
        ) {

            // This is going to be replaced by the URL of view model
            val recipe = Recipe(UUID.randomUUID().toString())
            PhotoPicker(recipe.photoUrl)

            // Hardcoded variable, remove when view model will be implemented
            var recipename by remember { mutableStateOf("") }

            val ingredients = remember { mutableStateListOf(
                Ingredient(UUID.randomUUID().toString()),
                )
            }

            AppTextField(
                value = recipename,
                onValueChange = { recipename = it },
                label = stringResource(R.string.RECIPE_NAME),
                modifier = Modifier.fillMaxWidth()
            )

            // Hardcoded variable, remove when view model will be implemented
            var recipeDescription by remember { mutableStateOf("") }

            AppTextField(
                value = recipeDescription,
                onValueChange = { recipeDescription = it },
                label = stringResource(R.string.DESCRIPTION),
                modifier = Modifier.fillMaxWidth()
            )

            var serving by remember { mutableStateOf("") }

            AppTextField(
                value = serving,
                onValueChange = { serving = it },
                label = stringResource(R.string.SERVINGS),
                modifier = Modifier.fillMaxWidth()
            )


            // Ingredients section
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.ingredients),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            ingredients.forEach { ingredient ->
                key(ingredient.id) {
                    IngredientRow(
                        ingredient = ingredient,
                        onChange = {  updated ->
                            val index = ingredients.indexOf(ingredient)
                            if (index != -1) ingredients[index] = updated
                        },
                        onRemove = if (ingredients.size > 1) {
                            { ingredients.remove(ingredient) }
                        } else null,
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }

            SecondaryButton(
                text = stringResource(R.string.add_ingredient),
                onClick = { ingredients.add(Ingredient(UUID.randomUUID().toString())) },
                modifier = Modifier.fillMaxWidth()
            )



            // Steps section
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.steps),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 10.dp)
            )


        }
    }
}


/**
 * Composable UI element for the recipe editor that shows the preview image and incorporates
 * two buttons to load an image from gallery or camera.
 */
@Composable
fun PhotoPicker(photoUrl: String?) {
    Column {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(16f / 10f)
                .clip(shape = MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if( photoUrl != null ){
                TODO()
            } else  {
                Column ( horizontalAlignment = Alignment.CenterHorizontally ) {
                    Icon (
                        imageVector = Icons.Filled.AddPhotoAlternate,
                        contentDescription = "Add a photo",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer( modifier = Modifier.height(2.dp) )

                    Text (
                        text = stringResource(R.string.add_a_photo),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer( modifier = Modifier.height(10.dp) )

        Row ( horizontalArrangement = Arrangement.spacedBy(10.dp) ) {
            SecondaryButton(
                text = stringResource(R.string.gallery),
                onClick = {},
                leadingIcon = Icons.Filled.PhotoLibrary,
                modifier = Modifier.weight(1f)
            )

            SecondaryButton(
                text = stringResource(R.string.camera),
                onClick = {},
                leadingIcon = Icons.Filled.PhotoCamera,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


/**
 * Composable UI ingredient row for the recipe editor screen, it allows to separate
 * each ingredient with its attributes.
 */
@Composable
fun IngredientRow(
    ingredient: Ingredient,
    onChange: (Ingredient) -> Unit,
    onRemove: (() -> Unit)?,
) {
    OutlinedCard (
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults
            .outlinedCardColors( MaterialTheme.colorScheme.surface )
    ) {
        Column( modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)){
                    AppTextField(
                        value = ingredient.name,
                        onValueChange = { onChange(ingredient.copy(name = it)) },
                        label = stringResource(R.string.INGREDIENT)
                    )
                }

                if(onRemove != null){
                    IconButton({ onRemove() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.remove_ingredient),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Row ( horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                
                Box ( Modifier.weight(1f)) {
                    AppTextField(
                        value = if( ingredient.quantity == 0.0 ) "" else trimNumber(ingredient.quantity!!) ,
                        onValueChange = { raw ->
                            val parse = raw.replace(',', '.').toDoubleOrNull() ?: 0.0
                            onChange(ingredient.copy(quantity = parse))
                        },
                        label = stringResource(R.string.QUANTITY),
                    )
                }

                Box( Modifier.weight(1f)) {
                    UnitField(
                        value = ingredient.unit,
                        onValueChange = { onChange(ingredient.copy(unit = it)) },
                    )
                }
            }
        }
    }
}


private fun trimNumber(value: Double): String {
    if( value == value.toLong().toDouble()) return value.toLong().toString()
    return value.toString()
}