package com.example.chatkotlin.data

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.chatkotlin.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.thread


class UserDBHelper(
    context: Context,
    databaseName: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, databaseName, factory, version) {
    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("create table if not exists UserTable (id text primary key,name text, user_id text,image_url text, image BLOB)")
    }
    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            database?.execSQL("alter table UserTable add column deleteFlag integer default 0")
        }
    }
}
class UserDB(context: Context){
    private val dbName: String = "MainDB"
    private val tableName: String = "UserTable"
    private val dbVersion: Int = 1
    private val context: Context = context

    //true: データが存在 false: データが無い
    fun dataeExist(){
        try {
            val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
            val database = dbHelper.readableDatabase
            val sql = "select * from " + tableName + " where id = 1"
            val cursor = database.rawQuery(sql, null)
            cursor.moveToFirst()
            cursor.getString(cursor.getColumnIndex("name"))
        }catch (exception: Exception) {
            //データがない場合の初期の処理
            val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
            val database = dbHelper.writableDatabase
            val values = ContentValues()

            values.put("id", "1")
            values.put("name", "ゲスト")

            //user_idをuuidで登録
            var user_id = UUID.randomUUID().toString()
            values.put("user_id", user_id)

            //gests画像を保存
            val bitmap :Bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.guestuser)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()
            values.put("image", bytes)

            //imageをfirebaseに保存
            saveImageToFirebaseStrage(bitmap,user_id)
            values.put("image_url","testdata")

            Log.d("database","データを新たに作成しました")
            database.insertOrThrow(tableName, null, values)
        }
    }

    fun saveImageToFirebaseStrage(user_image: Bitmap, user_id: String){
        val storage_ref = FirebaseStorage.getInstance().getReference("/UserImages/$user_id")
        val baos = ByteArrayOutputStream()
        user_image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        storage_ref.putBytes(data)
            .addOnSuccessListener {
                storage_ref.downloadUrl.addOnSuccessListener {
                    updateUserImageUrl(it.toString())
                }
            }
    }

    //---------------GET DATA------------------
    fun getname() :String{
        val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
        val database = dbHelper.readableDatabase
        val sql = "select * from " + tableName + " where id = 1"
        val cursor = database.rawQuery(sql, null)
        cursor.moveToFirst()
        return cursor.getString(cursor.getColumnIndex("name"))
    }
    fun getuser_id() :String{
        val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
        val database = dbHelper.readableDatabase
        val sql = "select * from " + tableName + " where id = 1"
        val cursor = database.rawQuery(sql, null)
        cursor.moveToFirst()
        return cursor.getString(cursor.getColumnIndex("user_id"))
    }
    fun getuser_image_url() :String{
        val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
        val database = dbHelper.readableDatabase
        val sql = "select * from " + tableName + " where id = 1"
        val cursor = database.rawQuery(sql, null)
        cursor.moveToFirst()
        return cursor.getString(cursor.getColumnIndex("image_url"))
    }
    fun getUserImage() :Bitmap{
        val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
        val database = dbHelper.readableDatabase
        val sql = "select * from " + tableName + " where id = 1"
        val cursor = database.rawQuery(sql, null)
        cursor.moveToFirst()
        val blob: ByteArray = cursor.getBlob(cursor.getColumnIndex("image"))
        return BitmapFactory.decodeByteArray(blob, 0, blob.size)
    }

    //--------------UPDATE---------------
    fun updateUserImage(newBitmap: Bitmap){
        val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
        val database = dbHelper.writableDatabase

        val values = ContentValues()
        val byteArrayOutputStream = ByteArrayOutputStream()
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        values.put("image", bytes)

        val whereClauses = "id = ?"
        val whereArgs = arrayOf("1")
        database.update(tableName, values, whereClauses, whereArgs)
    }
    fun updateUserName(newName: String){
        val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
        val database = dbHelper.writableDatabase
        val values = ContentValues()
        values.put("name",newName)
        val whereClauses = "id = ?"
        val whereArgs = arrayOf("1")
        database.update(tableName, values, whereClauses, whereArgs)
    }
    fun updateUserImageUrl(newUrl: String){
        val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
        val database = dbHelper.writableDatabase
        val values = ContentValues()
        values.put("image_url",newUrl)
        val whereClauses = "id = ?"
        val whereArgs = arrayOf("1")
        database.update(tableName, values, whereClauses, whereArgs)
    }

    fun delete(){
        val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
        val database = dbHelper.readableDatabase
        database.delete(tableName,"id = ?", arrayOf("1"))
    }
}