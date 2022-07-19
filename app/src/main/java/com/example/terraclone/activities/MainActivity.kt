package com.example.terraclone.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.terraclone.R
import com.example.terraclone.activities.DataMembers.Doctor
import com.example.terraclone.activities.DataMembers.Patient
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.activities.Fragment.HomeFragment
import com.example.terraclone.activities.Fragment.ProfileFragment
import com.example.terraclone.databinding.ActivityMainBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    var myview:ActivityMainBinding?=null
    private var mauth:FirebaseAuth?=null
    private var toggle:ActionBarDrawerToggle?=null
    private var mdata:FirebaseDatabase?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(myview?.root)
        setSupportActionBar(myview?.mytoolbar)
        startFragment(HomeFragment())
        mauth = DataBase.mauth
        mdata = DataBase.mdatabase
        toggle = ActionBarDrawerToggle(this,myview?.mydrawerlayout,myview?.mytoolbar,R.string.Open,R.string.Close)
        myview?.mydrawerlayout?.addDrawerListener(toggle!!)
        toggle?.syncState()

        myview?.mynavview?.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home1->{
                    startFragment(HomeFragment())
                }

                R.id.profile->{
                    startFragment(ProfileFragment())

                }
                R.id.logout->{
                    mauth?.signOut()
                    Intent(this,IntroActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
            }
            myview?.mydrawerlayout?.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true


        }

        myview?.floatingActionButton?.setOnClickListener {
            Intent(this,Appointment::class.java).also {
                startActivity(it)
            }
        }
        updateHeader()

    }

    private fun updateHeader() {
       val header= myview?.mynavview?.getHeaderView(0)
        val name = header?.findViewById(R.id.namedrawertxt)  as TextView
        val email = header.findViewById(R.id.emaildrawertxt) as TextView
        val image = header.findViewById(R.id.shapeableImageView) as ShapeableImageView

        mdata?.reference?.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val flag = snapshot.child("Doctors").child(mauth?.uid!!).exists()
                if(flag){
                    val doctor = snapshot.child("Doctors").child(mauth?.uid!!).getValue(Doctor::class.java)
                    name.text = doctor?.doctorname
                    email.text = doctor?.email
                    Picasso.get().load(doctor?.profilepic).placeholder(R.drawable.ic_baseline_people_24).into(image)
                }
                else{
                    myview?.floatingActionButton?.visibility = View.VISIBLE
                    val patient = snapshot.child("Patients").child(mauth?.uid!!).getValue(Patient::class.java)
                    name.text = patient?.patientname
                    email.text = patient?.email
                    Picasso.get().load(patient?.profilepic).placeholder(R.drawable.ic_baseline_people_24).into(image)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })



    }

    private fun startFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(myview?.myframelayout?.id!!,fragment)
        transaction.commit()
    }


    override fun onBackPressed() {
        if(myview?.mydrawerlayout?.isDrawerOpen(GravityCompat.START)!!){
            myview?.mydrawerlayout?.closeDrawer(GravityCompat.START)
        }
        else super.onBackPressed()
    }
}