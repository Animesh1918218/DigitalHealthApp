package com.example.terraclone.activities

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import com.example.terraclone.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    var myview:ActivityIntroBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        myview?.ptrbtn?.setOnClickListener {
            Intent(this, PatientRegistration::class.java).also {
                val options = ActivityOptions.makeSceneTransitionAnimation(this, Pair(myview?.ptrbtn,"mybtntransition"))
                startActivity(it,options.toBundle())
            }
        }
        myview?.dtrbtn?.setOnClickListener { mview->
            Intent(this,DoctorRegistration::class.java).also {
                val options = ActivityOptions.makeSceneTransitionAnimation(this,mview,"mysignintxttransition")
                startActivity(it,options.toBundle())
            }
        }
      myview?.signintxt?.setOnClickListener {
          Intent(this,SignIn::class.java).also {
              startActivity(it)
          }
      }
    }
}