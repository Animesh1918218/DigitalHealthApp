package com.example.terraclone.activities.FireBase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object DataBase {
     var mauth:FirebaseAuth?=null
     var mdatabase: FirebaseDatabase?=null
    var mStorage :FirebaseStorage?=null
   init {
       mauth = FirebaseAuth.getInstance()
       mdatabase = FirebaseDatabase.getInstance("https://terrafire-e9eb0-default-rtdb.asia-southeast1.firebasedatabase.app/")
       mStorage = FirebaseStorage.getInstance("gs://terrafire-e9eb0.appspot.com")
   }
}