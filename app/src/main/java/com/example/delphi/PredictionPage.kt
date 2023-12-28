package com.example.delphi

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.prediction_page.button2
import kotlinx.android.synthetic.main.prediction_page.resultView2
//import kotlinx.android.synthetic.main.prediction_page.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class PredictionPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); //Run your code in addition to the existing code in the onCreate() of the parent class
        setContentView(R.layout.prediction_page);   //Run the layout out of prediction_page.xml
        supportActionBar?.hide();   //Hides the action bar (if it exists)
        resultView2.setText(intent.getStringExtra("finalText").toString()); //TextView receives the entire prediction
        var buttonView: Button = findViewById(R.id.button2);    //Allows the button to be interacted with inside the Activity
        button2.setOnClickListener {    //We need to set the onClickListener function here
            GlobalScope.async { //Asynchronous execution of code, awaiting a return value
                finish();   //Terminates current Activity
                }
        }
    }
}