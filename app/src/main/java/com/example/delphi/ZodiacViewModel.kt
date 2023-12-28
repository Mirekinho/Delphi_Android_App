package com.example.delphi
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.example.delphi.ZodiacModel

class ZodiacViewModel : ViewModel(){

    var selectedZodiac : String = ""
    var zodiacPrediction : String = ""
    var resultView : TextView? = null

    public suspend fun getPrediction(){
        try {
            val rslt = GlobalScope.async {
                //Prepare and set string being sent over to the API
                contactAztroAPI("https://newastro.vercel.app/"+ selectedZodiac);
            }.await()
            getPrediction(rslt);  //Edit and return the received prediction
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
            zodiacPrediction ="Today's prediction:\n" +
                    selectedZodiac.uppercase() +"\n"+
                    resultJson.getString("horoscope");
            //setText(this.resultView,zodiacPrediction);
            this.resultView!!.text = selectedZodiac

        } catch (e: Exception) {
            e.printStackTrace();
            this.resultView!!.text = "An error has occurred, please try again!";
        }
    }

    /*private fun setText(text: TextView?, value: String) {
        runOnUiThread { text!!.text = value; }
    }*/
}