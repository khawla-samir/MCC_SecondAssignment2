package com.example.firstproj.Adabter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment2mcc.Model.NoteLists
import com.example.assignment2mcc.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

class NoteListsRV(var activity: Activity, var notesList:ArrayList<NoteLists>) :RecyclerView.Adapter<NoteListsRV.NoteViewHolder>(){

    private lateinit var analytics : FirebaseAnalytics
    private lateinit var mListener : OnItemClickListener
    interface onItemClickListener : OnItemClickListener {
        fun onItemClick(position: Int)

    }
    fun setOnItemClickListener(listener:onItemClickListener){
        mListener = listener
    }

    class NoteViewHolder(itemView: View,listener:onItemClickListener) : RecyclerView.ViewHolder(itemView) {//
        val list:TextView =itemView.findViewById(R.id.noteList)
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {

     var view = LayoutInflater.from(activity).inflate(R.layout.list_item,parent,false)
        return NoteViewHolder(view, mListener as onItemClickListener)//
    }

    override fun getItemCount(): Int {
      return notesList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.list.text=notesList[position].list
      /*  holder.itemView.setOnClickListener {
          //  selectContent(position.toString(),notesList[position].list.toString())
            if(notesList[position].list.toString().equals("Computing Notes")){
                val i = Intent(activity,Computing_Page::class.java)
                activity.startActivity(i)
            }else if(notesList[position].list.toString().equals("Games Notes")){
                val i = Intent(activity,Games_Page::class.java)
                activity.startActivity(i)
            }
        }*/

    }
    fun selectContent(id:String , name:String){
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {//or select_item
            param(FirebaseAnalytics.Param.ITEM_ID, id); //(key,value)
            param(FirebaseAnalytics.Param.ITEM_NAME, name);
        }
    }
}