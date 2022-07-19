package com.example.terraclone.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignIn : AppCompatActivity() {
   private var myview:ActivitySignInBinding?=null
    private var mauth:FirebaseAuth?=null
    private var mdialogue:Dialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        setSupportActionBar(myview?.mytoolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mdialogue = MyCustomProgressDialogue(this).mydialogue()
        myview?.mytoolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        mauth = DataBase.mauth
        myview?.signinbtn?.setOnClickListener {
            val email = myview?.emailtxt?.text?.toString()
            val password = myview?.passtxt?.text?.toString()
            if(email!!.isNotEmpty()&&password!!.isNotEmpty()){
                mdialogue?.show()
                authProcess(email,password)
            }
            else Toast.makeText(this, "plz fill all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun authProcess(email: String, password: String) {
           mauth?.signInWithEmailAndPassword(email,password)?.addOnCompleteListener {
               task->
               mdialogue?.dismiss()
               if(task.isSuccessful){

                   Intent(baseContext,MainActivity::class.java).also {
                       startActivity(it)
                       finish()
                   }
               }
               else{
                   Toast.makeText(baseContext, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
               }
           }
    }
}