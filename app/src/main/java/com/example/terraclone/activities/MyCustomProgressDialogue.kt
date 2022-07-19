package com.example.terraclone.activities

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.example.terraclone.R

class MyCustomProgressDialogue(private val context: Context) {
    fun mydialogue():Dialog{
        val mdialogue = Dialog(context)
        mdialogue.setContentView(R.layout.progress_dialogue)
        mdialogue.setCancelable(false)
        mdialogue.window?.setBackgroundDrawable(ColorDrawable(0))
        return mdialogue
    }
}