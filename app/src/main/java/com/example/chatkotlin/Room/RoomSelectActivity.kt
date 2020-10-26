package com.example.chatkotlin.Room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatkotlin.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_room_select.*

class RoomSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_select)

        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(RoomMakeItem())
        adapter.add(RoomMakeItem())
        adapter.add(RoomMakeItem())
        select_recycle.adapter = adapter
    }
}
class RoomMakeItem: Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    }
    override fun getLayout(): Int {
        return  R.layout.room_make_row_item
    }
}