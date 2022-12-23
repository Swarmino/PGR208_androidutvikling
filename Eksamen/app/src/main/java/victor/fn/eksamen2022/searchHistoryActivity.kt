package victor.fn.eksamen2022

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class searchHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_history)
        showRecipesFromFile()
        val actionBar = supportActionBar
        actionBar!!.title = "Search history"
    }

    fun showRecipesFromFile() {
        val file = File(filesDir, "searchHistory.txt")
        val fileContent = file.readText()
        val tvRecipes : TextView = findViewById(R.id.tv_list)
        tvRecipes.text = fileContent
    }

}
