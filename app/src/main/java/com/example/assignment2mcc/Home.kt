package com.example.assignment2mcc

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment2mcc.Model.NoteLists
import com.example.firstproj.Adabter.NoteListsRV
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Home : AppCompatActivity() {
    lateinit var listsRV: RecyclerView
    lateinit var lists: ArrayList<NoteLists>
    private var db = Firebase.firestore
    var progressDialog: ProgressDialog? = null
    private lateinit var analytics : FirebaseAnalytics
    var startTime: Long = 0
    var endTime: Long = 0
    var timeSpent: Long = 0
    var seconds: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startTime = System.currentTimeMillis()
        analytics = Firebase.analytics
        screenTrack("Home","Home")
        listsRV = findViewById(R.id.listRV)
        listsRV.layoutManager = LinearLayoutManager(this)
        lists = ArrayList()
        var listAdabter = NoteListsRV(this, lists)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Page is loaded, please wait")
        progressDialog!!.show()

        db.collection("noteLists")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val list: NoteLists? = data.toObject(NoteLists::class.java)
                        lists.add(list!!)
                    }
                    listsRV.adapter = listAdabter
                }
                progressDialog!!.hide()
            }.addOnFailureListener {
                Log.e("TAG","Fialed")
            }

        listAdabter.setOnItemClickListener(object : NoteListsRV.onItemClickListener{

            override fun onItemClick(position: Int) {
                onDestroy()


                if(lists[position].list.toString().equals("Computing Notes")){
                    val i = Intent(this@Home,Notes_C::class.java)
                    startActivity(i)
                }else if(lists[position].list.toString().equals("Games Notes")){
                    val i = Intent(this@Home,Notes_G::class.java)
                   startActivity(i)
                }
                }


                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    TODO("Not yet implemented")
                }

            })
    }
    fun selectContent(name:String , contentType:String){
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {//or select_item
            param(FirebaseAnalytics.Param.ITEM_NAME, name);
            param(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        }

    }
    fun screenTrack(screenClass:String , screenName:String){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
            param(FirebaseAnalytics.Param.SCREEN_CLASS,screenClass)
            param(FirebaseAnalytics.Param.SCREEN_NAME,screenName)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        endTime = System.currentTimeMillis()
        timeSpent = endTime-startTime
        seconds = timeSpent/1000
        val track = hashMapOf(
            "screen" to this@Home.toString(),
            "time" to seconds,
        )
        db.collection("screens")
            .add(track)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}