package com.example.chatkotlin.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayOutputStream

class UserDBHelper(context: Context, databaseName:String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, databaseName, factory, version) {
    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("create table if not exists UserTable (id text primary key,name text, user_id text, image BLOB)");
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

    fun insertData(id: String, name: String, user_id: String, bitmap: Bitmap) {
        try {
            val dbHelper = UserDBHelper(context, dbName, null, dbVersion);
            val database = dbHelper.writableDatabase

            val values = ContentValues()
            values.put("id", id)
            values.put("name", name)
            values.put("user_id", user_id)
            val byteArrayOutputStream = ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()
            values.put("image", bytes)

            database.insertOrThrow(tableName, null, values)
        }catch(exception: Exception) {
            Log.e("insertData", exception.toString())
        }

    }
    fun updateData(whereId: String, newName: String, newuser_id: String, newBitmap: Bitmap) {
        try {
            val dbHelper = UserDBHelper(context, dbName, null, dbVersion);
            val database = dbHelper.writableDatabase

            val values = ContentValues()
            values.put("name", newName)
            values.put("user_id", newuser_id)
            val byteArrayOutputStream = ByteArrayOutputStream();
            newBitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()
            values.put("image", bytes)

            val whereClauses = "id = ?"
            val whereArgs = arrayOf(whereId)
            database.update(tableName, values, whereClauses, whereArgs)
        }catch(exception: Exception) {
            Log.e("updateData", exception.toString())
        }
    }

    fun selectData() :String{
        try {
            val dbHelper = UserDBHelper(context, dbName, null, dbVersion)
            val database = dbHelper.readableDatabase

            val sql = "select * from " + tableName + " where id = 1"

            val cursor = database.rawQuery(sql, null)
            cursor.moveToFirst()
            return cursor.getString(cursor.getColumnIndex("name"))
//            if (cursor.count > 0) {
//                cursor.moveToFirst()
//                while (!cursor.isAfterLast) {
//                    arrayListId.add(cursor.getString(0))
//                    arrayListName.add(cursor.getString(1))
//                    arrayListType.add(cursor.getInt(2))
//                    val blob: ByteArray = cursor.getBlob(3)
//                    val bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.size)
//                    arrayListBitmap.add(bitmap)
//                    cursor.moveToNext()
//                }
//            }

        }catch(exception: Exception) {
            Log.e("selectData", exception.toString());
            return "a"
        }
    }
}