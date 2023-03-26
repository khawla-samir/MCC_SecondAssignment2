package com.example.assignment2mcc

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment2mcc.Adabter.NoteTypeRV
import com.example.assignment2mcc.Model.NoteType
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class Notes_C : AppCompatActivity() {
    lateinit var typesRV: RecyclerView
    lateinit var types: ArrayList<NoteType>
    //lateinit var details: ArrayList<NoteDetailsAc>
    private var db = Firebase.firestore
    var progressDialog: ProgressDialog? = null
    private lateinit var analytics : FirebaseAnalytics
    lateinit var imageView: ImageView
    var startTime: Long = 0
    var endTime: Long = 0
    var timeSpent: Long = 0
    var seconds: Long = 0


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_computing_page)

        startTime = System.currentTimeMillis()
        analytics = Firebase.analytics

        screenTrack("Notes_C","Notes")

        typesRV = findViewById(R.id.typeRV)
        typesRV.layoutManager = LinearLayoutManager(this)
        types = ArrayList()
        var typeAdabter = NoteTypeRV(this, types)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Page is loaded..")
        progressDialog!!.show()
        //imageView = findViewById(R.id.noteImg)
        /*FirebaseStorage.getInstance().getReference("images/computing.png")
            .downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(imageView)
            }*/

        db.collection("computingNotes")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val type: NoteType? = data.toObject(NoteType::class.java)
                        types.add(type!!)

                    }
                    typesRV.adapter = typeAdabter
                    }
                progressDialog!!.hide()
                }.addOnFailureListener {
                Log.e("TAG","Fialed")
            }


        onDestroy()

    }


    fun screenTrack(screenClass:String , screenName:String){
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
            param(FirebaseAnalytics.Param.SCREEN_CLASS,screenClass)
            param(FirebaseAnalytics.Param.SCREEN_NAME,screenName)
        }
    }
    fun getDuration(): Long {
        return System.currentTimeMillis() - startTime
    }

    override fun onDestroy() {
        super.onDestroy()
        endTime = System.currentTimeMillis()
        timeSpent = endTime-startTime
        seconds = timeSpent/1000
        val track = hashMapOf(
            "screen" to this.toString(),
            "time" to seconds,
        )
        db.collection("screens")
            .add(track)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }
}