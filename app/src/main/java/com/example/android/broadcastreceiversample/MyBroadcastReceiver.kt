package com.example.android.broadcastreceiversample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        when(intent?.action){
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                Toast.makeText(context, "connectivity changed", Toast.LENGTH_SHORT).show()
            }

            Intent.ACTION_BOOT_COMPLETED->{
                Toast.makeText(context, "boot completed", Toast.LENGTH_SHORT).show()
            }
        }

    }
}