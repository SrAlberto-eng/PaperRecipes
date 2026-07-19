package com.paperrecipes.app.data.model

data class Recipe(
    val id: String,
    val ownerId: String = "",
    val name: String = "",
    val description: String = "",
    val photoUrl: String? = null,
    val servings: Int = 1,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<Step> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)

data class Ingredient(
    val id: String,
    val name: String = "",
    val quantity: Double? = 0.0,
    val unit: String = "unit"
)

data class Step(
    val id: String,
    val description: String = ""
)

data class IngredientUnit(
    val label: String = "",
) {
    companion object {
        val PRESETS: List<String> = listOf(
            "g", "kg", "mg",
            "ml", "l", "cl",
            "tsp", "tbsp", "cup",
            "oz", "lb",
            "pinch", "clove", "slice",
            "unit", "piece",
        )
    }
}