package com.example.chatkotlin.Message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatkotlin.*
import com.example.chatkotlin.User.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatlogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)

        recycleview_chat_log.adapter = adapter

        //メッセージ相手のuser情報
        toUser = intent.getParcelableExtra<User>(NewMassageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

        listenForMassage()

        //送信ボタンのクリック
        button_chat_log.setOnClickListener{
            Log.d("NewMassage", "btn clicced")
            performSendMassage()
        }
    }
    private fun listenForMassage(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatmassage = p0.getValue(ChatMassage::class.java)

                if(chatmassage != null){
                    Log.d("New Massage", chatmassage.text)

                    if(chatmassage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMassageActivity.currentUser ?: return
                        adapter.add(ChatToItem(chatmassage.text, currentUser))
                    }else{
                        adapter.add(ChatFromItem(chatmassage.text,toUser!!))
                    }
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun performSendMassage(){
        //メッセージをfirebaseに新規作成
        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMassageActivity.USER_KEY)
        val toId = user.uid

        if(fromId == null) return
        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMassage = ChatMassage(reference.key!!, text, toId, fromId, System.currentTimeMillis()/1000)
        reference.setValue(chatMassage)
            .addOnSuccessListener {
                Log.d("NewMassage", "save massage")
            }
    }

}

class ChatFromItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_from_massages
        Picasso.get().load(uri).into(targetImageView)

    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
class ChatToItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.image_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
        Log.d("test","$user.uid")
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}