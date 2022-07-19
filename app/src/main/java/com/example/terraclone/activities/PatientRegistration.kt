package com.example.terraclone.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.terraclone.activities.DataMembers.Patient
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.databinding.ActivityPatientRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PatientRegistration : AppCompatActivity() {
    var myview:ActivityPatientRegistrationBinding?=null
    var mauth:FirebaseAuth?=null
    private var myCustomProgressDialogue:Dialog?=null
    var mdatabase:FirebaseDatabase?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivityPatientRegistrationBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        setSupportActionBar(myview?.mytoolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        myCustomProgressDialogue = MyCustomProgressDialogue(this).mydialogue()
        mauth = DataBase.mauth
        mdatabase = DataBase.mdatabase
        myview?.mytoolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        myview?.regiterbtn?.setOnClickListener {
            val myname = myview?.nametxt?.text?.toString()
            val myemail = myview?.emailtxt?.text?.toString()
            val password = myview?.passtxt?.text?.toString()
            val phoneno = myview?.phoneno?.text?.toString()
            if(myname!!.isNotEmpty()&&myemail!!.isNotEmpty()&&password!!.isNotEmpty()&&phoneno!!.isNotEmpty()){
                myCustomProgressDialogue?.show()
                authProcess(myname,myemail,password,phoneno.toLong())
            }
            else Toast.makeText(this, "Plz Fill All Details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun authProcess(myname:String,myemail:String,password:String,phoneno:Long) {
        mauth?.createUserWithEmailAndPassword(myemail,password)?.addOnCompleteListener {
            task->
            myCustomProgressDialogue?.dismiss()
            if(task.isSuccessful){

                val patient = Patient(myname,myemail,password,phoneno)
                val id = task.result.user!!.uid
                 mdatabase?.reference?.child("Patients")?.child(id)?.setValue(patient)
                Toast.makeText(baseContext, "Patient Registered", Toast.LENGTH_SHORT).show()
                Intent(baseContext,MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
            else Toast.makeText(baseContext, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
        }

    }
}