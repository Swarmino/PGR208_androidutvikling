package victor.fn.eksamen2022

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okio.IOException
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    //private var apiEndpoint = "https://api.edamam.com/api/recipes/v2?app_key=0dd00f8586f499f414f67248450c0017&app_id=3eaca95e&type=public&q="

    private var apiEndpoint = "https://api.edamam.com/api/recipes/v2?app_key=0dd00f8586f499f414f67248450c0017&app_id=3eaca95e&type=public&q="

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSettings : Button = findViewById(R.id.btn_settings)
        val btnSearch : Button = findViewById(R.id.btn_search)
        val btnHistory : Button = findViewById(R.id.btn_history)
        val btnPreferences : Button = findViewById(R.id.btn_prefrence)
        val btnTimeOfDay : Button = findViewById(R.id.btn_timeOfDay)

        val sp = getSharedPreferences("sharedPref", MODE_PRIVATE)

        val dietType = sp.getString("dietType", "None")
        val calorieCount = sp.getInt("calorieCount", 0)
        val priorityMeal = sp.getString("priorityMeal", "None")
        val maxAmount = sp.getInt("maxAmount", 0)

        if (dietType != "None") {
            apiEndpoint += "&diet=" + dietType
        } else {
            apiEndpoint += ""
        }

        if (calorieCount != 0) {
            apiEndpoint += "&calories=" + calorieCount
        } else {
            apiEndpoint += ""
        }

        if (priorityMeal != "None") {
            apiEndpoint += "&mealType=" + priorityMeal
        } else {
            apiEndpoint += ""
        }

        if (maxAmount != 0) {
            apiEndpoint += "&to=" + maxAmount
        } else {
            apiEndpoint += ""
        }

        apiEndpoint += "${dietType}${calorieCount}${priorityMeal}${maxAmount}"

        Log.i("apiEndpoint", apiEndpoint)

        btnSettings.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, settings_activity::class.java)
            startActivity(intent)
            finish()
        })

        btnHistory.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, searchHistoryActivity::class.java)
            startActivity(intent)
        })

        btnSearch.setOnClickListener(View.OnClickListener {
            val etSearch : EditText = findViewById(R.id.et_search)
            val searchQuery = etSearch.text.toString()
            searchRecipeFromApi(searchQuery)
        })

        btnPreferences.setOnClickListener(View.OnClickListener {
            searchRecipeFromApi(apiEndpoint)
        })

        btnTimeOfDay.setOnClickListener(View.OnClickListener {
            showRecipesByTimeOfDay()
        })

        val actionBar = supportActionBar
        actionBar!!.title = "Recipe Search"

    }

    private fun searchRecipeFromApi( query : String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(apiEndpoint + query)
            .build()

        apiGet(client, request)

        val tvMealType : TextView = findViewById(R.id.tv_mealType)
        tvMealType.text = "Currently showing: $query"


    }

    // Gets the api and populates the view with the data
    private fun apiGet(client: OkHttpClient, request: Request) {
        thread {

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("MainActivity", "Failed to execute request")
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body.string()
                    Log.d("MainActivity", body)

                    val json = JSONObject(body)
                    val hits = json.getJSONArray("hits")
                    val recipeList = ArrayList<RecipeItem>()

                    for (i in 0 until hits.length()) {
                        val recipe = hits.getJSONObject(i).getJSONObject("recipe")
                        val title = recipe.getString("label")
                        val image = getBitmapfromUrl(recipe.getString("image"))
                        val calorieCount = recipe.getInt("calories")
                        val shareLink = recipe.getString("shareAs")
                        val uri = recipe.getString("uri")
                        val recipeItem = image?.let { RecipeItem(title, image, calorieCount, shareLink, uri) }
                        recipeItem?.let { recipeList.add(it) }

                        val file = File(filesDir, "searchHistory.txt")
                        val fileContent = file.readText()
                        file.writeText("$fileContent\n $title \n")

                    }

                    runOnUiThread {
                        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.rv_recipes)
                        recyclerView.adapter = RecipeAdapter(recipeList)
                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    }
                }
            })
        }
    }

    // coverts images to bitmap from url strings
    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // shows recipes by time of day
    private fun showRecipesByTimeOfDay() {

        val tvMealType : TextView = findViewById(R.id.tv_mealType)

        var url = apiEndpoint + "all"

        if (getTimeOfDay() == "morning"){
            url = apiEndpoint + "&mealType=breakfast"
            tvMealType.text = "Currently showing: Breakfast"
        }
        else if (getTimeOfDay() == "afternoon"){
            url = apiEndpoint + "&mealType=lunch"
            tvMealType.text = "Currently showing: Lunch"
        }
        else if (getTimeOfDay() == "evening"){
            url = apiEndpoint + "&mealType=dinner"
            tvMealType.text = "Currently showing: Dinner"
        }
        else if (getTimeOfDay() == "night"){
            url = apiEndpoint + "&mealType=dessert"
            tvMealType.text = "Currently showing: Dessert"
        } else if (getTimeOfDay() == "day"){
            url = apiEndpoint + "&mealType=snack"
            tvMealType.text = "Currently showing: Snack"
        }

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        apiGet(client, request)
    }

    // gets the time of day for the showRecipesByTimeOfDay function
    private fun getTimeOfDay() : String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "morning"
            in 12..15 -> "afternoon"
            in 16..20 -> "evening"
            in 21..23 -> "night"
            else -> "day"
        }
    }

}
