package com.example.terraclone.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.terraclone.R
import com.example.terraclone.activities.DataMembers.Doctor
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.databinding.ActivityDoctorRegisterationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.reflect.Array
import java.util.*


class DoctorRegistration : AppCompatActivity() {
    var myview:ActivityDoctorRegisterationBinding?=null
    var mauth:FirebaseAuth?=null
    var mdatabase:FirebaseDatabase?=null
    private var myCustomProgressDialogue:Dialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivityDoctorRegisterationBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        setSupportActionBar(myview?.mytoolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        myCustomProgressDialogue = MyCustomProgressDialogue(this).mydialogue()
        myview?.mytoolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        mauth = DataBase.mauth
        mdatabase = DataBase.mdatabase
        val slots = resources.getStringArray(R.array.slots)

        val marrayAdapter = ArrayAdapter(this,R.layout.drop_down_items,slots)
        myview?.autoComplete?.setAdapter(marrayAdapter)
        val spz = resources.getStringArray(R.array.Specialization)
        val spzadapter = ArrayAdapter(this,R.layout.drop_down_items,spz)
        myview?.autoCompleteSpz?.setAdapter(spzadapter)

        myview?.registerbtn?.setOnClickListener {
            val name = myview?.nametxt?.text?.toString()
            val email = myview?.emailtxt?.text?.toString()
            val phoneno = myview?.phoneno?.text?.toString()
            val slots = myview?.autoComplete?.text?.toString()
            val spez = myview?.autoCompleteSpz?.text?.toString()
            val password = myview?.passtxt?.text?.toString()
           if(name!!.isNotEmpty()&&email!!.isNotEmpty()&&phoneno!!.isNotEmpty()&&slots!!.isNotEmpty()&&spez!!.isNotEmpty()&&password!!.isNotEmpty()){
               myCustomProgressDialogue?.show()
               authProcess(name,email,password,phoneno.toLong(),slots,spez)
           }
            else Toast.makeText(this, "Plz Fill All Details", Toast.LENGTH_SHORT).show()

        }


    }

    private fun authProcess(name: String, email: String, password: String, phoneno: Long, slots: String, spez: String) {
           mauth?.createUserWithEmailAndPassword(email,password)?.addOnCompleteListener {
               task->
               myCustomProgressDialogue?.dismiss()
               if(task.isSuccessful){

                   val doctor = Doctor(name,email,password,phoneno, mutableMapOf(slots to false),spez)
                   val id = task.result.user!!.uid
                   mdatabase?.reference?.child("Doctors")?.child(id)?.setValue(doctor)
                   Toast.makeText(baseContext, "Registered Succesfully", Toast.LENGTH_SHORT).show()
                   Intent(baseContext,MainActivity::class.java).also {
                       startActivity(it)
                       finish()
                   }
               }
               else Toast.makeText(baseContext, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
           }
    }
}