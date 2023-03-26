package com.example.assignment2mcc.Adabter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment2mcc.Model.NoteType
import com.example.assignment2mcc.R
import android.widget.AdapterView.OnItemClickListener
import com.example.assignment2mcc.NoteDetailsAct
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class NoteTypeRV(var activity: Activity, var notesType:ArrayList<NoteType>) :
    RecyclerView.Adapter<NoteTypeRV.NoteViewHolder>() {


    private lateinit var analytics : FirebaseAnalytics
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val type: TextView = itemView.findViewById(R.id.noteType)
        var img: ImageView = itemView.findViewById(R.id.noteImg)

    }


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {


        var view = LayoutInflater.from(activity).inflate(R.layout.computing_list_item, parent, false)
        return NoteViewHolder(view)
    }


    override fun onBindViewHolder(holder: NoteTypeRV.NoteViewHolder, position: Int) {
        holder.type.text = notesType[position].type
        var path :String?= null
        if(notesType[position].type.equals("Computing Exam"))
            path="images/ClassQuiz1.png"
        else if (notesType[position].type.equals("Computing Assignment"))
            path="images/computing.png"
        if(notesType[position].type.equals("Games Exam"))
            path="images/ClassQuiz1.png"
        else if (notesType[position].type.equals("Games Assignment"))
            path="images/gaming.png"

        FirebaseStorage.getInstance().getReference(path.toString())
            .downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(holder.img)
            }

        holder.itemView.setOnClickListener {
            selectContent(position.toString(),notesType[position].type.toString())
                Log.e("TAG","Failed")
            var i = Intent(activity,NoteDetailsAct::class.java)
            i.putExtra("type",notesType[position].type)
            activity.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return notesType.size
    }
    fun selectContent(id:String , name:String){
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {//or select_item
            param(FirebaseAnalytics.Param.ITEM_ID, id); //(key,value)
            param(FirebaseAnalytics.Param.ITEM_NAME, name);
        }
    }


}



