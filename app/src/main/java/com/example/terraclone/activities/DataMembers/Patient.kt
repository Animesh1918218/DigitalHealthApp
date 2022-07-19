package com.example.terraclone.activities.DataMembers


class Patient {
    var patientname:String?=null
    var email:String?=null
    var password:String?=null
    var id:String?=null
    var age:Int?=null
    var bloodtype:String?=null
    var phoneno:Long?=null
    var profilepic:String?=null
    var address:String?=null
    constructor(){}
    constructor(name:String,email:String,password:String,phoneno:Long){
        this.patientname = name
        this.email = email
        this.phoneno = phoneno
        this.password = password
    }



}