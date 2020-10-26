package com.example.chatkotlin.Room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.example.chatkotlin.Default.StartpageActivity
import com.example.chatkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_room_result.*

class RoomResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_result)

        var hand0 = Handler()

        //room_idの設定
        val room_id = intent.getStringExtra("room_id").toString()//room_id: room_1
        val user_id = intent.getStringExtra("user_id").toString() //room_id: room_1

        //順位の表示
        val ref = FirebaseDatabase.getInstance().getReference("Room/$room_id/user").orderByChild("score")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val layout_name = findViewById<LinearLayout>(R.id.line_layout_all)
                var i: Int = p0.childrenCount.toInt() - 1
                p0.children.forEach {
                    val line_ranking: LinearLayout = layout_name.getChildAt(i) as LinearLayout
                    val textView_score: TextView = line_ranking.getChildAt(1) as TextView
                    val textView_name: TextView = line_ranking.getChildAt(2) as TextView

                    textView_score.text = it.child("score").value.toString()
                    textView_name.text = it.child("user_name").value.toString()

                    Log.d("nnnn",it.child("score").value.toString())
                    Log.d("nnnn",it.toString())
                    Log.d("nnnn",it.child("user_name").value.toString())
                    i--
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })

        FirebaseDatabase.getInstance().getReference("Room")
        //ゲーム終了後4秒後にデータをリセット
        hand0.postDelayed(Runnable {
            FirebaseDatabase.getInstance().getReference("Room/$room_id").removeValue()
        },4000)

        button_result_to_start.setOnClickListener {
            val intent = Intent(this,StartpageActivity::class.java)
            startActivity(intent)
        }

    }
}