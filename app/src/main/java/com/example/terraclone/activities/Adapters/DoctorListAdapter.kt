package com.example.terraclone.activities.Adapters

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.terraclone.R
import com.example.terraclone.activities.Appointment
import com.example.terraclone.activities.DataMembers.AppointmentData
import com.example.terraclone.activities.DataMembers.Doctor
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.activities.MyCustomProgressDialogue
import com.example.terraclone.databinding.DoctorAdapterLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class DoctorListAdapter(private val myitems:ArrayList<Doctor>) :RecyclerView.Adapter<DoctorListAdapter.MainViewHolder>() {

    private var disc:String?=null
    private var slot:String?=null
    private var mcustomProgressDialogue:Dialog?=null
    private var context:Context?=null
    private var mdatabase:FirebaseDatabase = DataBase.mdatabase!!
    private var mauth:FirebaseAuth = FirebaseAuth.getInstance()

    constructor(myitems: ArrayList<Doctor>,disc:String,slot:String) : this(myitems) {
        this.disc = disc
        this.slot = slot
    }
    inner class MainViewHolder(item:DoctorAdapterLayoutBinding) : RecyclerView.ViewHolder(item.root){
          val name = item.nametxt
          val email = item.emailtxt
          val image  = item.shapeableImageView2
         val phoneno = item.phoneno
        val layout = item.mylayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        return MainViewHolder(DoctorAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
         holder.name.text = myitems[position].doctorname
         holder.phoneno.text = myitems[position].phoneno.toString()
         holder.email.text  =myitems[position].email
         Picasso.get().load(myitems[position].profilepic).placeholder(R.drawable.ic_baseline_people_24).into(holder.image)

        holder.layout.setOnClickListener {

            setUpAlertDialogue(position)

        }

    }

    override fun getItemCount(): Int {
        return myitems.size
    }

    private fun setUpAlertDialogue(position: Int){
        val mcustomDialogue = AlertDialog.Builder(context!!)
        mcustomDialogue.setTitle("Appointment Booking")
        mcustomDialogue.setMessage("Do You Want To Book This Appointment")
        mcustomProgressDialogue = MyCustomProgressDialogue(context!!).mydialogue()
        mcustomDialogue.setPositiveButton("Yes"){
            dinterface,_->
            mcustomProgressDialogue?.show()
            storeAppointmentInDatabase(position)
            dinterface.dismiss()
        }
        mcustomDialogue.setCancelable(false)
        mcustomDialogue.setNegativeButton("No"){
            dinterface,_->
            dinterface.dismiss()
        }
        mcustomDialogue.create().show()
    }

    private fun storeAppointmentInDatabase(position: Int) {
        val key = mauth.uid + myitems[position].id
        val appointment  = AppointmentData(mauth.uid!!,myitems[position].id!!,false,disc!!,slot!!)
        mdatabase.reference.child("Appointments").child(key).push().setValue(appointment).addOnCompleteListener {
            task->
            if(task.isSuccessful){
                mcustomProgressDialogue?.dismiss()
                Toast.makeText(context, "Appointment Booked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}