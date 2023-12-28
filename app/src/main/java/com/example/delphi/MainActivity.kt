package com.example.delphi

// Added imports
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import androidx.lifecycle.ViewModelProvider
import com.example.delphi.ZodiacViewModel
import com.example.delphi.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.spinner
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener { //Allows for Item selection
    var resultView: TextView? = null;
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ZodiacViewModel   //Calling up the ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); //Run your code in addition to the existing code in the onCreate() of the parent class
        setTheme(R.style.Theme_Delphi); //App's starting theme is set as "SplashScreenTheme"- it has to change to "Theme_Delphi" now to remove the splash screen
        setContentView(R.layout.activity_main); //Run the layout out of activity_main.xml
        supportActionBar?.hide();   //Hides the action bar (if it exists)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ZodiacViewModel::class.java)

        binding.button.setOnClickListener{
            GlobalScope.async { //Asynchronous execution of code, awaiting a return value
                viewModel.selectedZodiac = spinner.getItemAtPosition(spinner.selectedItemPosition).toString().lowercase()
                viewModel.resultView = resultView
                viewModel.getPrediction()
                val sharedPreference = this@MainActivity?.getPreferences(Context.MODE_PRIVATE); //Setup for persistent memory
                with (sharedPreference.edit()) {
                    putInt("selectedZodiac", spinner.getSelectedItemPosition());    //Item position on the spinner gets stored permanently
                    apply();
                }
                var intent = Intent(this@MainActivity, PredictionPage::class.java); //Setup for creating new Activity
                //intent.putExtra("finalText",zodiac);    //Setup transfer of prediction to the other Activity
                intent.putExtra("finalText",viewModel.zodiacPrediction);
                startActivity(intent);  //Run the second Activity
            }
        }

        val spinner = findViewById<Spinner>(R.id.spinner);  //Allows the spinner to be interacted with inside the Activity
        //Allows the sunsigns array (in strings.xml) to be interacted with
        val adapterSigns = ArrayAdapter.createFromResource(this,R.array.signs,android.R.layout.simple_spinner_item);
        adapterSigns.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapterSigns; //Spinner now contains the signs array
        spinner.onItemSelectedListener = this;  //Sets up the onItemSelectedListener for spinner
        resultView = findViewById(R.id.resultView);
        val sharedPreference = this@MainActivity?.getPreferences(Context.MODE_PRIVATE) ?: return;   //Setup for persistent memory
        /*Sets the spinner position into the position stored in the persistent memory (aka the last chosen position- great for a single user who)
        * will be mostly interested in only one sign*/
        spinner.setSelection(sharedPreference.getInt("selectedZodiac",2));
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //No additional code needed, persistent memory and default value guarantees there will be always something selected
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            val imageView: ImageView = findViewById(R.id.starImageView);    //Allows the ImageView to be interacted with inside the Activity
            when(parent.getItemAtPosition(position).toString()){    //Checks which star image it has to show according to the selected sign
                "Capricorn" -> imageView.setImageResource(R.drawable.ic_star_capricorn);
                "Aquarius" -> imageView.setImageResource(R.drawable.ic_star_aquarius);
                "Pisces" -> imageView.setImageResource(R.drawable.ic_star_pisces);
                "Aries" -> imageView.setImageResource(R.drawable.ic_star_aries);
                "Taurus" -> imageView.setImageResource(R.drawable.ic_star_taurus);
                "Gemini" -> imageView.setImageResource(R.drawable.ic_star_gemini);
                "Cancer" -> imageView.setImageResource(R.drawable.ic_star_cancer);
                "Leo" -> imageView.setImageResource(R.drawable.ic_star_leo);
                "Virgo" -> imageView.setImageResource(R.drawable.ic_star_virgo);
                "Libra" -> imageView.setImageResource(R.drawable.ic_star_libra);
                "Scorpio" -> imageView.setImageResource(R.drawable.ic_star_scorpio);
                "Sagittarius" -> imageView.setImageResource(R.drawable.ic_star_sagittarius);
            }
        }
    }
    //This is the original code of the previous non-MVVM version
    /*var zodiac: String = "";    //Used to store the prediction
    var resultView: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); //Run your code in addition to the existing code in the onCreate() of the parent class
        setTheme(R.style.Theme_Delphi); //App's starting theme is set as "SplashScreenTheme"- it has to change to "Theme_Delphi" now to remove the splash screen
        setContentView(R.layout.activity_main); //Run the layout out of activity_main.xml
        supportActionBar?.hide();   //Hides the action bar (if it exists)

        var buttonView: Button = findViewById(R.id.button); //Allows the button to be interacted with inside the Activity
        button.setOnClickListener { //We need to set the onClickListener function here
            GlobalScope.async { //Asynchronous execution of code, awaiting a return value
                //getPrediction(buttonView);  //This function sets into motion a series of other functions, all leading to receiving the sign prediction
                getPrediction(buttonView);
                val sharedPreference = this@MainActivity?.getPreferences(Context.MODE_PRIVATE); //Setup for persistent memory
                with (sharedPreference.edit()) {
                    putInt("selectedZodiac", spinner.getSelectedItemPosition());    //Item position on the spinner gets stored permanently
                    apply();
                }
                var intent = Intent(this@MainActivity, PredictionPage::class.java); //Setup for creating new Activity
                //intent.putExtra("finalText",zodiac);    //Setup transfer of prediction to the other Activity
                intent.putExtra("finalText",viewModel.selectedZodiacSign.value.toString());
                startActivity(intent);  //Run the second Activity
            }
        }

        val spinner = findViewById<Spinner>(R.id.spinner);  //Allows the spinner to be interacted with inside the Activity
        //Allows the sunsigns array (in strings.xml) to be interacted with
        val adapterSigns = ArrayAdapter.createFromResource(this,R.array.signs,android.R.layout.simple_spinner_item);
        adapterSigns.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapterSigns; //Spinner now contains the signs array
        spinner.onItemSelectedListener = this;  //Sets up the onItemSelectedListener for spinner
        resultView = findViewById(R.id.resultView);
        val sharedPreference = this@MainActivity?.getPreferences(Context.MODE_PRIVATE) ?: return;   //Setup for persistent memory
        /*Sets the spinner position into the position stored in the persistent memory (aka the last chosen position- great for a single user who)
        * will be mostly interested in only one sign*/
        spinner.setSelection(sharedPreference.getInt("selectedZodiac",2));


    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        //No additional code needed, persistent memory and default value guarantees there will be always something selected
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            val imageView: ImageView = findViewById(R.id.starImageView);    //Allows the ImageView to be interacted with inside the Activity
            when(parent.getItemAtPosition(position).toString()){    //Checks which star image it has to show according to the selected sign
                "Capricorn" -> imageView.setImageResource(R.drawable.ic_star_capricorn);
                "Aquarius" -> imageView.setImageResource(R.drawable.ic_star_aquarius);
                "Pisces" -> imageView.setImageResource(R.drawable.ic_star_pisces);
                "Aries" -> imageView.setImageResource(R.drawable.ic_star_aries);
                "Taurus" -> imageView.setImageResource(R.drawable.ic_star_taurus);
                "Gemini" -> imageView.setImageResource(R.drawable.ic_star_gemini);
                "Cancer" -> imageView.setImageResource(R.drawable.ic_star_cancer);
                "Leo" -> imageView.setImageResource(R.drawable.ic_star_leo);
                "Virgo" -> imageView.setImageResource(R.drawable.ic_star_virgo);
                "Libra" -> imageView.setImageResource(R.drawable.ic_star_libra);
                "Scorpio" -> imageView.setImageResource(R.drawable.ic_star_scorpio);
                "Sagittarius" -> imageView.setImageResource(R.drawable.ic_star_sagittarius);
            }
        }
    }
    //What follows are the OG functions

    public suspend fun getPrediction(view: android.view.View) {
        try {
            val rslt = GlobalScope.async {
                //Prepare and set string being sent over to the API
                /*contactAztroAPI("https://sameer-kumar-aztro-v1.p.rapidapi.com/?sign="
                        + spinner.getItemAtPosition(spinner.selectedItemPosition) + "&day=today");*/
                contactAztroAPI("https://newastro.vercel.app/"+ spinner.getItemAtPosition(spinner.selectedItemPosition).toString().lowercase());
            }.await()
            getPrediction(rslt);  //Edit and return the received prediction
            //getPrediction(URL("https://newastro.vercel.app/").readText());  //Edit and return the received prediction
        }
        catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun contactAztroAPI(apiUrl:String ):String?{
        var connection: HttpURLConnection? = null;
        var rslt: String? = "";
        try {
            connection = URL(apiUrl).openConnection() as HttpURLConnection; //Setting up connection with the API
            //Set request headers: host name, rapid-api key and content type
            //connection.setRequestProperty("lang", "eng");
            //connection.setRequestProperty("sign", "pisces");
            //connection.requestMethod = "POST";  //Set request method to POST
            val reader = InputStreamReader(connection.inputStream); //Sets InputStreamReader
            var responseData = reader.read();   //Gets response data from reader
            while (responseData != -1) {    //Reads all data from reader
                rslt += responseData.toChar();
                responseData = reader.read();
            }
            return rslt;
        }
        catch (e: Exception) {
            e.printStackTrace();
        }
        return null;    //Returns null if unable to retrieve data
    }

    private fun getPrediction(result: String?) {
        try {
            val resultJson = JSONObject(result); //String to JSON object conversion
            // Setting up the zodiac string
            /*zodiac ="Today's prediction:\n" +
                    this.spinner.getItemAtPosition(spinner.selectedItemPosition).toString() +"\n"+
                    resultJson.getString("sign")+"\n"+
                    resultJson.getString("horoscope");*/
            zodiac ="Today's prediction:\n" +
                    this.spinner.getItemAtPosition(spinner.selectedItemPosition).toString() +"\n"+
                    resultJson.getString("horoscope");
            setText(this.resultView,zodiac);

        } catch (e: Exception) {
            e.printStackTrace();
            this.resultView!!.text = "An error has occurred, please try again!";
        }
    }

    private fun setText(text: TextView?, value: String) {
        runOnUiThread { text!!.text = value; }
    }*/
}