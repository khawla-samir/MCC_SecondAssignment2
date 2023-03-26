package com.example.assignment2mcc

import android.app.ProgressDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import com.example.assignment2mcc.Model.NoteDetails
import com.example.assignment2mcc.Model.NoteType
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class NoteDetailsAct : AppCompatActivity() {
    lateinit var name : TextView
    lateinit var desc : TextView
    lateinit var num : TextView
    lateinit var img : ImageView
    var startTime: Long = 0
    var endTime: Long = 0
    var timeSpent: Long = 0
    var seconds: Long = 0

    lateinit var details: ArrayList<NoteDetails>
    private var db = Firebase.firestore
    var progressDialog: ProgressDialog? = null
    private lateinit var analytics : FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        name = findViewById(R.id.name)
        desc = findViewById(R.id.desc)
        num = findViewById(R.id.num)
        img = findViewById(R.id.img)
        details = ArrayList()
        analytics = Firebase.analytics
        screenTrack("NoteDetailsAct", "Details")
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Page is loaded..")
        progressDialog!!.show()
        var type = intent.getStringExtra("type").toString()
        var path :String?= null
        if(type.equals("Computing Exam"))
            path="images/ClassQuiz1.png"
        else if (type.equals("Computing Assignment"))
            path="images/computing.png"
        if(type.equals("Games Exam"))
            path="images/ClassQuiz1.png"
        else if (type.equals("Games Assignment"))
            path="images/gaming.png"

        FirebaseStorage.getInstance().getReference(path.toString())
            .downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(img)
            }.addOnFailureListener {
                Log.e("TAG","Fialed")
            }


        db.collection("notes").whereEqualTo("name", type)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {

                        val detail: NoteDetails? = data.toObject(NoteDetails::class.java)
                        details.add(detail!!)

                    }
                    name.text = details[0].name
                    desc.text = details[0].desc
                    num.text = details[0].num
                }
                progressDialog!!.hide()
            }
       // details.clear()
        onDestroy()
    }
                fun screenTrack(screenClass: String, screenName: String) {
                    analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                        param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
                        param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                    }
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
