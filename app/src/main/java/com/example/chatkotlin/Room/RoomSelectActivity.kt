package com.example.chatkotlin.Room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_room_select.*
import kotlinx.android.synthetic.main.room_make_row_item.view.*

class RoomSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_select)

        val adapter = GroupAdapter<ViewHolder>()
//        adapter.add(RoomMakeItem())
//        adapter.add(RoomMakeItem())
//        adapter.add(RoomMakeItem())
        select_recycle.adapter = adapter
        readFirebaseRoomMake()
    }
    private fun readFirebaseRoomMake(){
        val ref = FirebaseDatabase.getInstance().getReference("RoomMake")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    val roommake = it.getValue(RoomMake::class.java)
                    if(roommake != null){
                        adapter.add(RoomMakeItem(roommake))
                    }
                }
                select_recycle.adapter = adapter
                readFirebaseRoomMake()
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

}
class RoomMake(
    val room_id: String = "",
    val room_name: String = "",
    val user_id: String = "",
    val user_name: String = "",
)

class RoomMakeItem(val roommake: RoomMake): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.make_item_room_name.text = roommake.room_name
        val name = "作成者"+ roommake.user_name
        viewHolder.itemView.make_item_user_name.text = name
    }
    override fun getLayout(): Int {
        return  R.layout.room_make_row_item
    }
}