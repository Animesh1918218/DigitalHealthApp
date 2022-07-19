package com.example.terraclone.activities.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.terraclone.R
import com.example.terraclone.activities.Adapters.AppointmentAdapter
import com.example.terraclone.activities.DataMembers.AppointmentData
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var myview:FragmentHomeBinding?=null
    private var mauth:FirebaseAuth?=null
    private var mdatabase:FirebaseDatabase?=null
    private var madapter:AppointmentAdapter?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myview = FragmentHomeBinding.bind(view)
        mauth = DataBase.mauth
        mdatabase = DataBase.mdatabase

        getAppointmentsFromDatabase()

        myview?.myswipelayout?.setOnRefreshListener {
            getAppointmentsFromDatabase()
        }
    }

    private fun getAppointmentsFromDatabase() {
        mdatabase?.reference?.child("Appointments")?.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val myitems:ArrayList<AppointmentData> = ArrayList()
                for (datasnapshot in snapshot.children){

                    val key = datasnapshot.key
                    if(key!!.contains(mauth?.uid!!)){
                        for(data in datasnapshot.children){
                            val appointments = data.getValue(AppointmentData::class.java)
                            appointments?.id = data.key
                            Log.i("Appointment","${appointments?.id}")
                            myitems.add(appointments!!)
                        }
                    }
                }
                madapter = AppointmentAdapter(myitems)
                Log.i("mydata","$myitems")
                myview?.myrlview?.adapter = madapter
                myview?.myrlview?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
                myview?.myswipelayout?.isRefreshing = false

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    override fun onResume() {

        getAppointmentsFromDatabase()
        super.onResume()
    }
}