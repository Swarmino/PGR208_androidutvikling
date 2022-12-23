package victor.fn.eksamen2022

import android.graphics.Bitmap

data class RecipeItem(
    val title: String,
    var image: Bitmap,
    val calorieAmount: Int,
    val shareLink : String,
    val uri : String
)