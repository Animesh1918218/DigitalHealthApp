package com.example.terraclone.activities.DataMembers

class Doctor {
    var doctorname:String?=null
    var email:String?=null
    var id:String?=null
    var password:String?=null
    var age:Int?=null
    var profilepic:String?=null
    var clinicadress:String?=null
    var slot:MutableMap<String,Boolean>?=null
    var phoneno:Long?=null
    var specialization:String?=null

    constructor(){}
    constructor(name:String,email:String,password:String,phoneno:Long,slots:MutableMap<String,Boolean>,specialization:String){
        this.doctorname = name
        this.email = email
        this.phoneno = phoneno
        this.slot = slots
        this.password = password
        this.specialization = specialization
    }
}