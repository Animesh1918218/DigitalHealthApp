package com.example.terraclone.activities.Fragment



import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import com.example.terraclone.R
import com.example.terraclone.activities.DataMembers.Doctor
import com.example.terraclone.activities.DataMembers.Patient
import com.example.terraclone.activities.FireBase.DataBase
import com.example.terraclone.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

import kotlin.collections.HashMap


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var myview:FragmentProfileBinding?=null
    private var mauth:FirebaseAuth?=null
    private var mdata:FirebaseDatabase?=null
    private var mstorage:FirebaseStorage?=null
    private var activityForPermission:ActivityResultLauncher<String>?=null
    private var activityResultLauncherForImages:ActivityResultLauncher<Intent>?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myview = FragmentProfileBinding.bind(view)
        mauth = DataBase.mauth
        mstorage = DataBase.mStorage
        mdata = DataBase.mdatabase
        setUpMyRequestLauncher()
        setUpMyLauncherForImages()
        updateFields()

        val map = HashMap<String,Any>()
        myview?.registerbtn?.setOnClickListener {
            val name = myview?.nametxt?.text.toString()
            val age = myview?.agetxt?.text.toString()
            val adress = myview?.myclinictxt?.text.toString()
            val phoneno = myview?.phoneno?.text.toString()


            updateNewValues(map,name,age,adress,phoneno)
        }

    myview?.floatingActionButton?.setOnClickListener {
             if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                      Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                          activityResultLauncherForImages?.launch(it)
                      }

             }
        else activityForPermission?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }



    }

    private fun setUpMyLauncherForImages() {
        activityResultLauncherForImages = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if(result.resultCode==RESULT_OK && result?.data!=null){
                    myview?.myimage?.setImageURI(result.data?.data)
                    mdata?.reference?.addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val uri = result.data?.data
                            val flag = snapshot.child("Doctors").child(mauth?.uid!!).exists()

                            if(flag){
                                mstorage?.reference?.child("Doctors")?.child(mauth?.uid!!)?.putFile(uri!!)?.addOnSuccessListener {
                                    task->
                                    task.storage.downloadUrl.addOnSuccessListener {
                                        muri->
                                        Log.i("ControlUri","$muri")
                                        snapshot.child("Doctors").child(mauth?.uid!!).child("profilepic").ref.setValue(muri.toString())
                                    }
                                }
                            }
                            else{
                                mstorage?.reference?.child("Patients")?.child(mauth?.uid!!)?.putFile(uri!!)?.addOnSuccessListener {
                                        task->
                                    task.storage.downloadUrl.addOnSuccessListener {
                                            muri->
                                        snapshot.child("Patients").child(mauth?.uid!!).child("profilepic").ref.setValue(muri.toString())
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }
        }
    }

    private fun checkSelfPermission(string: String):Boolean{
        return ContextCompat.checkSelfPermission(requireContext(),string)==PackageManager.PERMISSION_GRANTED
    }
    private fun setUpMyRequestLauncher() {
        activityForPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isgranted->
            if(isgranted) Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNewValues(map: HashMap<String, Any>,name:String,age:String,address:String,phoneno:String) {
        mdata?.reference?.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val flag = snapshot.child("Doctors").child(mauth?.uid!!).exists()
                if(flag){
                    if(name.isNotEmpty()) map["doctorname"] = name
                    if(age.isNotEmpty()) map["age"] = age.toInt()
                    if(address.isNotEmpty()) map["clinicadress"] = address
                    if(phoneno.isNotEmpty()) map["phoneno"] = phoneno.toLong()
                    mdata?.reference?.child("Doctors")?.child(mauth?.uid!!)?.updateChildren(map)?.addOnSuccessListener {
                        Toast.makeText(context, "Values Updated Successfully", Toast.LENGTH_SHORT).show()
                    }

                }
                else{
                    if(name.isNotEmpty()) map["patientname"] = name
                    if(age.isNotEmpty()) map["age"] = age.toInt()
                    if(address.isNotEmpty()) map["address"] = address
                    if(phoneno.isNotEmpty()) map["phoneno"] = phoneno.toLong()
                    mdata?.reference?.child("Patients")?.child(mauth?.uid!!)?.updateChildren(map)?.addOnCompleteListener {
                            task->
                        if(task.isSuccessful){
                            Toast.makeText(context, "Values Updated Successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateFields() {
        mdata?.reference?.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 val flag = snapshot.child("Doctors").child(mauth?.uid!!).exists()
                 if(flag){
                     val doctor = snapshot.child("Doctors").child(mauth?.uid!!).getValue(Doctor::class.java)
                     myview?.agetxt?.setText(doctor?.age?.toString())
                     myview?.myclinictxt?.setText(doctor?.clinicadress)
                     myview?.nametxt?.setText(doctor?.doctorname)
                     myview?.phoneno?.setText(doctor?.phoneno?.toString())
                     myview?.myclinicadtl?.hint = String.format("Clinic Address")
                     Picasso.get().load(doctor?.profilepic).placeholder(R.drawable.ic_baseline_people_24).into(myview?.myimage)

                 }
                else{
                    val patient = snapshot.child("Patients").child(mauth?.uid!!).getValue(Patient::class.java)
                     myview?.agetxt?.setText(patient?.age?.toString())
                     myview?.myclinictxt?.setText(patient?.address)
                     myview?.nametxt?.setText(patient?.patientname)
                     myview?.phoneno?.setText(patient?.phoneno?.toString())
                     myview?.myclinicadtl?.hint = String.format("Home Address")
                     Picasso.get().load(patient?.profilepic).placeholder(R.drawable.ic_baseline_people_24).into(myview?.myimage)
                 }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}