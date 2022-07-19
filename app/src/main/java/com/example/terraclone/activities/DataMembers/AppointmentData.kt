package com.example.terraclone.activities.DataMembers

class AppointmentData {

    var patientid:String?=null
    var isAccepted:Boolean?=null
    var doctorId:String?=null
    var problemdiscription:String?=null
    var slot:String?=null
    var timestamp:Long?=null
    var id:String?=null

    constructor(){}

    constructor(patientid:String,doctorid:String,isAccepted:Boolean,disc:String,slot:String){
        this.patientid = patientid
        this.isAccepted = isAccepted
        this.doctorId = doctorid
        this.problemdiscription = disc
        this.slot = slot
    }
}