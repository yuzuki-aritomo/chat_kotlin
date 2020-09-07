package com.example.chatkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_massage.*
import kotlinx.android.synthetic.main.user_row_new_massage.view.*

class NewMassageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_massage)

        supportActionBar?.title = "Select User"

        fetchUsers()
    }
    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    Log.d("NewMassage", it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }
                }
                recycleview_newmassage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textViewl_new_massage.text = user.username
        Picasso.get().load(user.profileImageUrl).into((viewHolder.itemView.imageView_new_massage))
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_massage
    }
}