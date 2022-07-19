package com.example.terraclone.activities

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
         private var myview:ActivitySplashScreenBinding?=null
         private var animation:AnimationDrawable?=null
         private var mauth:FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        mauth = DataBase.mauth
          animation  = myview?.mylayout?.background as AnimationDrawable
        animation?.setEnterFadeDuration(2000)
        animation?.setExitFadeDuration(2000)
        animation?.start()
         Handler().postDelayed({
             if(mauth?.uid!=null){
              Intent(this,MainActivity::class.java).also {
                  startActivity(it)

              }
             }
             else {
                 Intent(this, IntroActivity::class.java).also {
                     val options = ActivityOptions.makeSceneTransitionAnimation(
                         this, Pair(myview?.mytext, "mytxttransition"),
                     )
                     startActivity(it, options.toBundle())

                 }
             }
         },6000

         )
            }

    override fun onStop() {
        super.onStop()
        finish()
    }

}