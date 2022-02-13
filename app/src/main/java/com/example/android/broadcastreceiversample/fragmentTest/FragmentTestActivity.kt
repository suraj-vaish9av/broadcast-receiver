package com.example.android.broadcastreceiversample.fragmentTest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.android.broadcastreceiversample.MainActivity
import com.example.android.broadcastreceiversample.R

class FragmentTestActivity : AppCompatActivity() {

    var counter = 0
    companion object{
        const val TAG="LifeOfAct, FragmentTest"
    }

    // Activity LifeCycle:
    // Case 1.
    // activity launched: onCreate -> onStart -> onResume

    // Case 2.
    // presses back button: onPause -> onStop -> onDestroy
    // opening app again : onCreate -> onStart -> onResume // because activity got destroyed it will be recreated

    // Case 3.
    // presses home button : onPause -> onStop
    // opening app again : onRestart -> onStart -> onResume

    // Case 4.
    // opening a new activity:
        // firstAct: onPause -->                                     |->    onStop
        //                     |                                     |
        // secondAct:          |-> onCreate -> onStart -> onResume ->|
    // pressing back from secondAct:
        // secondAct: onPause -->                                       |-> onStop-> onDestroy
        //                      |                                       |
        // secondAct:           |-> onRestart -> onStart -> onResume -> |


    // Case 5.
    // Screen rotated
    // onCreate -> onStart-> onResume->  Screen rotated here  onPause-> onStop-> onDestroy-> onCreate -> onStart-> onResume

    // Case 6.
    // Device locked (Same as home button): onPause -> onStop
    // opening app again : onRestart -> onStart -> onResume


    // onRestart method called when user gets back to the activity

    /**
     *  Fragment transactions:
     *  add : adds a fragment in the container (it's like a layer one above another)
     *  replace : replaces container's content with the given fragment, (suppose two fragment were added(F1,F2) and then container replaces with F3 then F1,F2 will be removed and F3 will be set)
     *  remove: removes the given fragment
     *
     *  Fragment LifeCycle:
     *
     *      Case 1. fragment loaded: onAttach -> onCreate -> onCreateView -> onViewCreated -> onActivityCreated -> onStart -> onResume
     *              pressing back : onPause -> onStop -> onDestroyView -> onDestroy -> onDetach
     *
     *      Case 2. Add Transaction:
     *              onAttach -> onCreate -> onCreateView -> onViewCreated -> onActivityCreated -> onStart -> onResume
     *
     *      Case 3. Replace Transaction:
     *              suppose that first and second fragments are already added, and then third fragment replaces them
     *              first :                                                                                            | -> onPause -> onStop -> onDestroyView -> onDestroy -> onDetach ->|
     *              second :                        |-> onPause -> onStop -> onDestroyView -> onDestroy -> onDetach -> |                                                                  |
     *              third :  onAttach -> onCreate ->|                                                                                                                                     |-> onCreateView -> onViewCreated -> onActivityCreated -> onStart -> onResume
     *
     *
     *      Case 4. Remove Transaction:
     *              onPause -> onStop -> onDestroyView -> onDestroy -> onDetach
     *
     *  Ways of handling fragments
     *  1. Without using addToBackStack
     *      We need to manage the transaction manually, like if we add or replace a fragment we need to remove it manually
     *
     *  2. With addToBackStack
     *      addToBackStack doesn't add fragment in backstack instead it add the transaction in the backstack
     *      to manage fragments with backstack we use the addToBackStack method with transaction to save the transaction in the stack,
     *      and to pop the transaction we have several methods:
     *          supportFragmentManager.popBackStack()   // pop the transaction from top
     *          supportFragmentManager.popBackStack(int,flag) // pop the transactions from top to the given index, second parameter tell the first parameter's index is exclusive(0) or inclusive(FragmentManager.POP_BACK_STACK_INCLUSIVE)
     *          supportFragmentManager.popBackStack(string,flag)  // same as above method but instead of index we pass string in first parameter which is actually same as we pass when adding transaction to backstack
     */

    /**
     * Some other notes on Fragments
     *
     * 1. Fragment should be implemented as modular component of activity, means fragments shouldn't be aware of each other (if multiple fragments are in single activity)
     *      communication between fragments should be done via activity, means f1 will tell activity and activity will start or inform f2
     *      this can be done via interface,
     *      interface FragmentActionListener{
     *          // for event
     *          val ACTION_KEY = "action"
     *          val ACTION_VALUE_COUNTRY = 1
     *
     *          // for value in bundle
     *          val KEY_SELECTED_COUNTRY="KEY_SELECTED_COUNTRY"
     *
     *          fun onActionPerformed(bundle: Bundle)
     *      }
     *      we will implement this interface in activity and create a variable of this interface type in f1, the value of variable will be set by activity
     *      and f1 will call the method whenever needed.
     *
     *  2. Retain State
     *      suppose the current state is, inside the activity there are fragments loaded in this order f1,f2
     *          f1 directly loaded in activity's onCreate, and an specific event occurred then activity loaded f2 on top of f1
     *          // if fragment is not loaded from activity's onCreate or from lifecycle method it won't make this issue
     *
     *          now if config changes, activity will be recreated, so f1 will be loaded again, here is the problem we lost the state we were at f2 but now after rotating the device we are at f1
     *          1. To resolve this we can add property android:configChanges="screenSize|orientation|keyboard" to refrain from the recreation of the activity.
     *              we also can use onConfigChanges method to find out the changes.
     *              Note: onConfigChanges only called when configChanges property is used
     *          2. Second option is let the activity to be recreated and check the bundle, if it is null then activity is created at first time else it means it was already created
     *              and some config changes causes it to recreate.
     *              if(savedInstanceState==null)    // fragment is added in activity's onCreate if want to add it only first rest of the time it will keep the existing fragments (means f1 and f2)
     *                  loadFragments
     *              at fragment level same condition could be used to conclude recreation in onActivityCreated(Bundle)
     *
     *
     *  4. setRetainInstance(true); in onCreate
     *             Control whether a fragment instance is retained across Activity
     *             re-creation (such as from a configuration change). If set, the fragment
     *              lifecycle will be slightly different when an activity is recreated:
     *              {@link #onDestroy()} will not be called (but {@link #onDetach()} still
     *              will be, because the fragment is being detached from its current activity).
     *              {@link #onCreate(Bundle)} will not be called since the fragment
     *              is not being re-created.
     *              {@link #onAttach(Activity)} and {@link #onActivityCreated(Bundle)} <b>will</b>
     *              still be called.
     *
     *  5. Why newInstance method used to create fragment
     *  If Android decides to recreate your Fragment later, it's going to call the no-argument constructor of your fragment. So overloading the constructor is not a solution.
     *  With that being said, the way to pass stuff to your Fragment so that they are available after a Fragment is recreated by Android is to pass a bundle to the setArguments method.
     *
     *  6. Headless Fragment:
     *      As setRetainInstance(true) help us to refrain us from recreation of fragment during configuration changes,
     *      so that our data or variable which resides in fragment not lost,
     *      just because of this feature we use Headless Fragment,
     *      Headless Fragment is a fragment which doesn't have the UI,
     *      now the scenario is we have an activity and it's data but due to config changes activity recreated we lost the data,
     *      just to keep track of those data we create a Headless Fragment and store the data there,
     *      even if config changes and activity recreated our data is stored in the headless fragment.
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fragment_test)

        setListener()



        val txtBackStackCount = findViewById<TextView>(R.id.txtBackStackCount)

        supportFragmentManager.addOnBackStackChangedListener {
            Log.d("FragmentTestActivity",supportFragmentManager.backStackEntryCount.toString())
            txtBackStackCount.text="BackStack Count: ${supportFragmentManager.backStackEntryCount}"

            //supportFragmentManager.popBackStack(0,0)

            val stringBuilder = StringBuilder("\n\n")
            for(index in (supportFragmentManager.backStackEntryCount-1) downTo 0){
                val entry = supportFragmentManager.getBackStackEntryAt(index)
                stringBuilder.append("$index: "+entry.name+"\n")
            }
            Log.d("BackStack entries", stringBuilder.toString())
        }

        Log.d(TAG,"onCreate")
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG,"onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume")
    }

    private fun setListener(){
        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val fragment =when(counter%3){
                0-> FirstFragment()
                1-> SecondFragment()
                else-> ThirdFragment()
            }


            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container,fragment)
            transaction.addToBackStack("Add: ${fragment.javaClass.simpleName}")
            val result = transaction.commit()

            Log.d("result",result.toString())

            counter++
        }

        findViewById<Button>(R.id.btnReplace).setOnClickListener {
            val fragment =when(counter%3){
                0-> FirstFragment()
                1-> SecondFragment()
                else-> ThirdFragment()
            }


            val transaction = supportFragmentManager.beginTransaction()
            transaction.addToBackStack("Replace: ${fragment.javaClass.simpleName}")
            transaction.replace(R.id.container,fragment)
            transaction.commit()

            counter++
        }

        findViewById<Button>(R.id.btnRemove).setOnClickListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.container)
            if (fragment!=null){
                val transaction = supportFragmentManager.beginTransaction()
                transaction.addToBackStack("Remove: ${fragment.javaClass.simpleName}")
                transaction.remove(fragment)
                transaction.commit()
            }
        }

        findViewById<Button>(R.id.btnStartAct).setOnClickListener {
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        /*val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment!=null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
        }
        else
            super.onBackPressed()*/

        if(supportFragmentManager.backStackEntryCount>0){
            supportFragmentManager.popBackStack()   // I think that is not needed
        }
        else{
            super.onBackPressed()
        }
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
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy")
    }


}