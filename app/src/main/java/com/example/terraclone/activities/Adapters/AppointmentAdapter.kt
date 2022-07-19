package com.example.terraclone.activities.Adapters

import android.content.Context
import android.content.res.Resources
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.terraclone.R
import com.example.terraclone.activities.DataMembers.AppointmentData
import com.example.terraclone.activities.DataMembers.Doctor
import com.example.terraclone.activities.DataMembers.Patient
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.databinding.AppointmentAdapterLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class AppointmentAdapter(private val allitems:ArrayList<AppointmentData>):RecyclerView.Adapter<AppointmentAdapter.MainViewHolder>() {
    inner class MainViewHolder(item:AppointmentAdapterLayoutBinding):RecyclerView.ViewHolder(item.root){
        val pname = item.nametxt
        val patientphoneno = item.phoneno
        val disc  = item.discriptiontxt
        val pimage = item.patientimage
        val dname = item.dnametxt
        val dphoneno = item.dphoneno
        val dimage = item.doctorimage
        val slot = item.slottxt
        val dspz = item.spztxt
        val layout = item.mylayout
        val text = item.textViewdoctor
    }

    private var mdatabase:FirebaseDatabase = DataBase.mdatabase!!
    private var context:Context?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        return MainViewHolder(AppointmentAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MainViewHolder,position:Int) {
        mdatabase.reference.addListenerForSingleValueEvent(object :ValueEventListener{
               override fun onDataChange(snapshot: DataSnapshot) {
                   val pdata = snapshot.child("Patients").child(allitems[holder.adapterPosition].patientid!!)
                   val patient = pdata.getValue(Patient::class.java)
                   Log.i("PatientData","$patient")
                   holder.pname.text = String.format("Name : ${patient?.patientname}")
                   holder.patientphoneno.text = String.format("Phone no : ${patient?.phoneno}")
                   holder.disc.text = String.format("Description : ${allitems[holder.adapterPosition].problemdiscription}")
                   Picasso.get().load(patient?.profilepic).placeholder(R.drawable.ic_baseline_people_24).into(holder.pimage)

                   if(allitems[holder.adapterPosition].isAccepted!!){
                       val ddata = snapshot.child("Doctors").child(allitems[holder.adapterPosition].doctorId!!)
                       holder.dimage.visibility = View.VISIBLE
                       holder.dname.visibility = View.VISIBLE
                       holder.dspz.visibility = View.VISIBLE
                       holder.dphoneno.visibility = View.VISIBLE
                       holder.slot.visibility = View.VISIBLE
                       holder.text.visibility = View.VISIBLE

                       val doctor = ddata.getValue(Doctor::class.java)
                       holder.dname.text =  String.format("Name : ${doctor?.doctorname}")

                       holder.dspz.text = String.format("Specializaion : ${doctor?.specialization}")
                       holder.dphoneno.text = String.format("Phone no : ${doctor?.phoneno}")
                       holder.slot.text = String.format("Appointment: ${allitems[holder.adapterPosition].slot}")
                       Picasso.get().load(doctor?.profilepic).placeholder(R.drawable.ic_baseline_people_24).into(holder.dimage)
                   }

               }

               override fun onCancelled(error: DatabaseError) {
                   TODO("Not yet implemented")
               }

           })



       mdatabase.reference.child("Doctors").addListenerForSingleValueEvent(object :ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.child(DataBase.mauth?.uid!!).exists()&&!allitems[holder.adapterPosition].isAccepted!!){
                   holder.layout.setOnClickListener {
                       setUpDialogue(holder.adapterPosition)
                   }
               }
           }

           override fun onCancelled(error: DatabaseError) {
               TODO("Not yet implemented")
           }

       })

    }

    private fun setUpDialogue(position: Int) {
        val mcustomDialogue = AlertDialog.Builder(context!!)
        mcustomDialogue.setTitle("Appointment Booking")
        mcustomDialogue.setMessage("Do You Want To Accept This Appointment")
        Log.i("Position","${allitems[position].id}")
        mcustomDialogue.setPositiveButton("Yes"){
                dinterface,_->
            mdatabase.reference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.child("Appointments").child(allitems[position].patientid+allitems[position].doctorId)
                        .child(allitems[position].id!!)
                        .child("accepted").ref.setValue(true)




                snapshot.child("Doctors").child(FirebaseAuth.getInstance().uid!!).child("slot").child(allitems[position].slot!!).ref.setValue(true).addOnSuccessListener {
                    for(datasnapshot in snapshot.child("Appointments").children){
                        if(datasnapshot.key!!.contains(DataBase.mauth?.uid!!)&&
                            datasnapshot.key!=(allitems[position].patientid+FirebaseAuth.getInstance().uid)){
                            datasnapshot.ref.setValue(null)
                        }
                    }
                }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })




            dinterface.dismiss()
        }
        mcustomDialogue.setCancelable(false)
        mcustomDialogue.setNegativeButton("No"){
                dinterface,_->
            dinterface.dismiss()
        }
        mcustomDialogue.create().show()
    }

    override fun getItemCount(): Int {
        return allitems.size
    }
}