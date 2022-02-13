package com.example.android.broadcastreceiversample

import android.content.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val myDynamicBroadcastReceiver by lazy { MyDynamicBroadcastReceiver() }

    private val myInnerClass = MyInnerClass()

    lateinit var btnSendBroadcast: Button

    private lateinit var txtMessage:TextView

    companion object{
        const val TAG="LifeOfAct, MainAct"

        const val CUSTOM_BROADCAST = "com.arupakaman.receiverapp.ACTION_CUSTOM_BROADCAST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        txtMessage=findViewById(R.id.txtMessage)

        btnSendBroadcast=findViewById(R.id.btnSendBroadcast)

        // runtime registration that's why dynamic receiver
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        registerReceiver(myDynamicBroadcastReceiver,intentFilter)


        /**
         * A custom broadcast, because custom action is used and custom extra
         */
        btnSendBroadcast.setOnClickListener {
            val intent = Intent(CUSTOM_BROADCAST)
            intent.putExtra("com.arupakaman.EXTRA_DATA","Hi, from Sender App")
            sendBroadcast(intent)
        }

        /**
         * This is an explicit broadcast receiver because we explicitly specified the name of broadcast receiver class
         */
        findViewById<Button>(R.id.btnSendExplicitBroadcast).setOnClickListener {
            val intent = Intent(applicationContext,MyExplicitBroadcastReceiver::class.java)
            sendBroadcast(intent)
        }

        /**
         * sending broadcast to broadcast receiver of other application,
         * package name and full qualified class name is given
         */
        findViewById<Button>(R.id.btnSendExplicitBroadcastToOtherApp).setOnClickListener {
            /*val intent = Intent()
            val componentName = ComponentName("com.arupakaman.receiverapp","com.arupakaman.receiverapp.MyExplicitBroadcastReceiver")
            intent.component = componentName
            sendBroadcast(intent)*/

            val intent = Intent()
            val componentName = ComponentName("com.arupakaman.receiverapp","com.arupakaman.receiverapp.RawandaTestReceiver")
            intent.component = componentName
            intent.putExtra(Intent.EXTRA_TEXT,"sample signature 123")
            sendBroadcast(intent)
        }



        /**
         * calling an implicit broadcast receiver explicitly,
         * the benefit of this is that implicit broadcast are almost died,
         * the won't receiver implicit broadcast except a few,
         * so if we fire this broadcast as implicit and if other app is not running that the other app may not receive it
         * but if we fire it as explicit then the other app will must receive it.
         */
        findViewById<Button>(R.id.btnCallImplicitBroadcastAsExplicit).setOnClickListener {
            val intent = Intent("com.arupakaman.receiverapp.IMPLICIT_BROADCAST_RECEIVER")

            val packageManager = packageManager
            val listOfResolveInfo = packageManager.queryBroadcastReceivers(intent,0)
            listOfResolveInfo.forEach {resolveInfo ->
                val componentName = ComponentName(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name)
                intent.component = componentName
                sendBroadcast(intent)
            }
        }

        /**
         * OrderBroadCast receiver used when we want to broadcast multiple messages(receiver) in a sequence,
         * and that will be called in a sequence means one by one based on the priority,
         * if we call abortBroadcast then no further broadcast receiver will not receive any message (means onReceive will not be call for next broadcast receivers).
         *
         * Here we are initiating ordered broadcast receiver,
         */
        findViewById<Button>(R.id.btnCallOrderBroadcastReceiver).setOnClickListener {
            val intent = Intent("com.arupakaman.receiverapp.ACTION_ORDERED_RECEIVER")
            intent.setPackage("com.arupakaman.receiverapp") // this will make it explicit broadcast receiver
            //sendBroadcast(intent)


            val bundle = Bundle()
            bundle.putString("message_key","Start")
            sendOrderedBroadcast(intent,null,OrderBroadcastResult(),null, RESULT_CANCELED,"Start",bundle)
        }

        Log.d(TAG,"onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart")
        // custom dynamic broadcast receiver registered
        val intentFilter = IntentFilter(CUSTOM_BROADCAST)
        registerReceiver(myInnerClass,intentFilter)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG,"onRestart")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"onStop")
        // custom dynamic broadcast receiver unregistered
        unregisterReceiver(myInnerClass)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy")
        // dynamic receiver unregistered
        unregisterReceiver(myDynamicBroadcastReceiver)
    }

    /**
     * broadcast receiver with custom action
     */
    inner class MyInnerClass : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (CUSTOM_BROADCAST == intent?.action)
            {
                val extraMessage = intent.getStringExtra("com.arupakaman.EXTRA_DATA")
                txtMessage.text = "Inner Broadcast Received: $extraMessage"
            }
        }
    }

}