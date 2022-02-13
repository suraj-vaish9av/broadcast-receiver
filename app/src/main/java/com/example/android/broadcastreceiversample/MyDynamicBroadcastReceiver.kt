package com.example.android.broadcastreceiversample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

/**
 * Dynamic Receiver, because this one is register at runtime.
 */
class MyDynamicBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                val connected =
                    !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
                if (connected) {
                    Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context, "disconnected", Toast.LENGTH_SHORT).show()
                }
            }

            Intent.ACTION_TIME_TICK->{
                Toast.makeText(context, "timer tick incremented", Toast.LENGTH_SHORT).show()
            }
        }
    }
}