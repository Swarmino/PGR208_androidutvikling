package victor.fn.eksamen2022

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.textfield.TextInputEditText

class settings_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val actionBar = supportActionBar
        actionBar!!.title = "Settings"

        val spinner = findViewById<Spinner>(R.id.sp_dietType)
        val adapter = ArrayAdapter.createFromResource(this, R.array.dietType, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val mealSpinner = findViewById<Spinner>(R.id.sp_mealPri)
        val mealAdapter = ArrayAdapter.createFromResource(this, R.array.mealPriority, android.R.layout.simple_spinner_item)
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mealSpinner.adapter = mealAdapter

        val btnSave : Button = findViewById(R.id.btn_save)

        loadValues()

        btnSave.setOnClickListener{
            saveValues()
            Toast.makeText(this, "Your changes have been saved", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // loads and sets the values from the shared preferences
    private fun loadValues(){

        val sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        val spinner = findViewById<Spinner>(R.id.sp_dietType)
        val mealSpinner = findViewById<Spinner>(R.id.sp_mealPri)

        val calorieCount = sharedPref.getInt("calorieCount", 0)

        val dietTypePos = sharedPref.getInt("dietTypePos", 0)
        val priorityMealPos = sharedPref.getInt("priorityMealPos", 0)

        val priorityMeal = sharedPref.getString("priorityMeal", "None")

        val maxHistoryItems = sharedPref.getInt("maxHistoryItems", 0)
        val maxAmount = sharedPref.getInt("maxAmount", 0)

        spinner.setSelection(dietTypePos)
        mealSpinner.setSelection(priorityMealPos)

        findViewById<TextInputEditText>(R.id.ti_dailyIntake).setText(calorieCount.toString())
        findViewById<TextInputEditText>(R.id.ti_maxSearchAmount).setText(maxHistoryItems.toString())
        findViewById<TextInputEditText>(R.id.ti_dietAmount).setText(maxAmount.toString())
        Log.i("Settings", sharedPref.all.toString())
    }

    //saves values to shared preferences
    private fun saveValues() {
        val sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE)
        val editor = sharedPref.edit()

        val dietSpinner = findViewById<Spinner>(R.id.sp_dietType)
        var dietSpinnerResult = dietSpinner.selectedItem.toString()
        val dietSpinnerPosResult = dietSpinner.selectedItemPosition

        val mealSpinner = findViewById<Spinner>(R.id.sp_mealPri)
        var mealSpinnerResult = mealSpinner.selectedItem.toString()
        val mealSpinnerPosResult = mealSpinner.selectedItemPosition

        val calorieCount = findViewById<TextInputEditText>(R.id.ti_dailyIntake).text.toString().toInt()
        val maxHistoryItems = findViewById<TextInputEditText>(R.id.ti_maxSearchAmount).text.toString().toInt()
        val maxAmount = findViewById<TextInputEditText>(R.id.ti_dietAmount).text.toString().toInt()

        if (dietSpinnerResult == "None"){
            dietSpinnerResult = ""
        }

        if (mealSpinnerResult == "None"){
            mealSpinnerResult = ""
        }

        editor.putString("dietType", dietSpinnerResult)
        editor.putInt("dietTypePos", dietSpinnerPosResult)

        editor.putString("mealPriority", mealSpinnerResult)
        editor.putInt("priorityMealPos", mealSpinnerPosResult)

        editor.putInt("calorieCount", calorieCount)
        editor.putInt("maxHistoryItems", maxHistoryItems)
        editor.putInt("maxAmount", maxAmount)

        editor.apply()
        Log.i("Settings", sharedPref.all.toString())

    }

}
