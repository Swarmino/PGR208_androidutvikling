package victor.fn.eksamen2022

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class RecipeAdapter(var recipeItems: List<RecipeItem>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    var calorieCount = 0

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)

    }

    fun AdapterList(ctx: Context?, position: Int) {
        val sp: SharedPreferences = ctx!!.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        val calorieCount = sp.getInt("calorieCount", 0)
        val newCalorieCount = calorieCount - recipeItems[position].calorieAmount

        val editor = sp.edit()
        editor.putInt("calorieCount", newCalorieCount)
        editor.apply()
        Toast.makeText(ctx, "Calorie count has been updated", Toast.LENGTH_SHORT).show()
        Log.i("RecipeAdapter", sp.all.toString())
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.itemView.apply {
            val recipeItem = recipeItems[position]
            findViewById<TextView>(R.id.tv_recipe_title).text = recipeItem.title
            findViewById<TextView>(R.id.tv_calorie_count).text = recipeItem.calorieAmount.toString()
            findViewById<ImageView>(R.id.iv_recipe_image).setImageBitmap(recipeItem.image)

            // change color of text if calorieamount is higher than caloriecount
            val sp = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            val calorieCount = sp.getInt("calorieCount", 0)
            if (recipeItem.calorieAmount < calorieCount) {
                findViewById<TextView>(R.id.tv_calorie_count).setTextColor(resources.getColor(R.color.black))
            } else {
                findViewById<TextView>(R.id.tv_calorie_count).setTextColor(resources.getColor(R.color.red))
            }

            findViewById<ImageView>(R.id.iv_recipe_image).setOnClickListener {
                shareWebsite(recipeItem)
            }

            findViewById<TextView>(R.id.tv_recipe_title).setOnClickListener {
                shareWebsite(recipeItem)
            }

            findViewById<Button>(R.id.btn_select).setOnClickListener {
                AdapterList(context, position)
            }

            findViewById<Button>(R.id.btn_favourite).setOnClickListener {
                val file = File(context.filesDir, "favourites.txt")
                file.appendText(recipeItem.uri + "")
                Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun View.shareWebsite(recipeItem: RecipeItem) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, recipeItem.shareLink)
        startActivity(context, Intent.createChooser(intent, "Share via"), null)
    }

    override fun getItemCount(): Int {
        return recipeItems.size
    }
}