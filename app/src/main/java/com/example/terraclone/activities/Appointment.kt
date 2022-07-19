package com.example.terraclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.terraclone.R
import com.example.terraclone.activities.Adapters.DoctorListAdapter
import com.example.terraclone.activities.DataMembers.Doctor
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.databinding.ActivityAppointmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Appointment : AppCompatActivity() {
    private var myview:ActivityAppointmentBinding?=null
    private var mdatabase:FirebaseDatabase?=null
    private var adapter:DoctorListAdapter?=null
    private var mauth:FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myview = ActivityAppointmentBinding.inflate(layoutInflater)
        setContentView(myview?.root)

        setSupportActionBar(myview?.mytoolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mdatabase = DataBase.mdatabase
        mauth = DataBase.mauth
        myview?.mytoolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        val slots = resources.getStringArray(R.array.slots)
        val adapter = ArrayAdapter(this, R.layout.drop_down_items, slots)
        myview?.autoComplete?.setAdapter(adapter)
        val spz = resources.getStringArray(R.array.Specialization)
        val mspzadapter = ArrayAdapter(this, R.layout.drop_down_items, spz)
        myview?.autoCompleteSpz?.setAdapter(mspzadapter)

        myview?.autoCompleteSpz?.setOnItemClickListener { parent, view, position, id ->
            val slot = myview?.autoComplete?.text?.toString()
            val spec = myview?.autoCompleteSpz?.text?.toString()
            val disc = myview?.pdtxt?.text?.toString()
            if (spec!!.isNotEmpty() && slot!!.isNotEmpty()) {
                setUpAdpater(spec, slot, disc!!)
            }

        }

        myview?.autoComplete?.setOnItemClickListener { parent, view, position, id ->
            val slot = myview?.autoComplete?.text?.toString()
            val spec = myview?.autoCompleteSpz?.text?.toString()
            val disc = myview?.pdtxt?.text?.toString()
            Log.i("Database", "$spec : $slot")
            if (spec!!.isNotEmpty() && slot!!.isNotEmpty()) {
                setUpAdpater(spec, slot, disc!!)
            }
        }

    }



    private fun setUpAdpater(spec: String, slot: String,disc:String) {
          mdatabase?.reference?.addValueEventListener(object :ValueEventListener{
              override fun onDataChange(snapshot: DataSnapshot) {
                  val myarray = ArrayList<Doctor>()
                  val data = snapshot.child("Doctors")

                  for(datasnapshot in data.children){
                      val doctor = datasnapshot.getValue(Doctor::class.java)
                      val map = doctor?.slot
                      val ans = datasnapshot.child("slot").children.first().key
                      val flag = snapshot.child("Appointments").child(mauth?.uid!!+datasnapshot.key).exists()
                      if(doctor?.slot?.containsKey(slot)!!&&doctor.specialization==spec&&!flag&&!map!![ans]!!){
                          doctor.id = datasnapshot.key
                          myarray.add(doctor)
                      }
                  }
                  adapter = DoctorListAdapter(myarray,disc,slot)
                  myview?.myrlview?.adapter = adapter
                  myview?.myrlview?.layoutManager = LinearLayoutManager(baseContext,LinearLayoutManager.VERTICAL,false)

              }

              override fun onCancelled(error: DatabaseError) {
                  TODO("Not yet implemented")
              }

          })
    }
}