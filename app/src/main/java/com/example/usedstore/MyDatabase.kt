package com.example.usedstore

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream


class MyDatabase(context: Context) {

    val ctx = context

    // 1 - database  table  key   create

    companion object{


        val dbName = "dbUsedStore"
        val dbVersion = 1

        val TableUser = "tuser"
        val K_U_ID = "_id"


        val CreateTableUser = "create table $TableUser (" +
                "$K_U_ID inteGer primary key autoincrement);"


        val TableItem = "tItem"
        val K_I_ID = "_id"
        val K_I_Name = "Iname"
        val K_I_Price = "Iprice"
        val K_I_Des = "Idescrption"
        val K_I_loc = "Ilocation"
        val K_I_NumPhone = "Inumphone"
        val K_I_Img = "Iimg"
        val K_I_ImgG = "IimgG"
        val K_I_UID = "IUid"
        val K_I_Date = "IDate"



        val CreateTableItem = "create table $TableItem (" +
                "$K_I_ID inteGer primary key autoincrement," +
                "$K_I_Name text," +
                "$K_I_Price float," +
                "$K_I_Des text," +
                "$K_I_loc text," +
                "$K_I_NumPhone text," +
                "$K_I_Img integer," +
                "$K_I_ImgG text," +
                "$K_I_Date text," +
                "$K_I_UID integer);"

    }


    //2 - create   upgrade   (SQLite open helper)
    class MyOpenHelper(
        context: Context?,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
    ) : SQLiteOpenHelper(context, name, factory, version) {
        override fun onCreate(p0: SQLiteDatabase?) {
            p0!!.execSQL(CreateTableUser)
            p0.execSQL(CreateTableItem)

            // إضافة قيمة K_U_ID في بداية تثبيت قاعدة البيانات
            val initialValues = ContentValues()
            initialValues.put(MyDatabase.K_U_ID, 1)
            p0.insert(MyDatabase.TableUser, null, initialValues)
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

        }
    }


    //3 - open  close

    lateinit var db : SQLiteDatabase

    fun openDB(){
        val myOpenHelper = MyOpenHelper(ctx, dbName,null, dbVersion)
        db = myOpenHelper.writableDatabase
    }

    fun closeDB(){
        db.close()
    }



    //4 - insert update delete select
    fun insertItem(name: String, price: Float, des: String, loc: String, numPhone: String, image: ByteArray, iuser: Int) {
        val cv = ContentValues()
        cv.put(K_I_Name, name)
        cv.put(K_I_Price, price)
        cv.put(K_I_Des, des)
        cv.put(K_I_loc, loc)
        cv.put(K_I_NumPhone, numPhone)
        cv.put(K_I_Date, getCurrentDate())
        cv.put(K_I_UID, iuser)
        cv.put(K_I_Img, image)

        val l = db.insert(TableItem, null, cv)
    }
    fun insertItem(name: String, price: Float, des: String, loc: String, numPhone: String, image: String, iuser: Int) {
        val cv = ContentValues()
        cv.put(K_I_Name, name)
        cv.put(K_I_Price, price)
        cv.put(K_I_Des, des)
        cv.put(K_I_loc, loc)
        cv.put(K_I_NumPhone, numPhone)
        cv.put(K_I_Date, getCurrentDate())
        cv.put(K_I_UID, iuser)
        cv.put(K_I_ImgG, image)

        val l = db.insert(TableItem, null, cv)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val date = Date()
        return dateFormat.format(date)
    }


    fun updateItem(id:Int,name:String , price:Float , des:String , loc:String , numPhone:String ,image: ByteArray, iuser:Int ){
        val cv = ContentValues()
        cv.put(K_I_Name,name)
        cv.put(K_I_Price,price)
        cv.put(K_I_Des,des)
        cv.put(K_I_loc,loc)
        cv.put(K_I_NumPhone,numPhone)
        cv.put(K_I_Img,image)
        cv.put(K_I_UID,iuser)
        val l = db.update(TableItem ,  cv ,"$K_I_ID = ? ", arrayOf(id.toString()))
        Toast.makeText(ctx, "$l", Toast.LENGTH_SHORT).show()
    }



    fun updateItem(id:Int,name:String , price:Float , des:String , loc:String , numPhone:String ,image: String, iuser:Int ){
        val cv = ContentValues()
        cv.put(K_I_Name,name)
        cv.put(K_I_Price,price)
        cv.put(K_I_Des,des)
        cv.put(K_I_loc,loc)
        cv.put(K_I_NumPhone,numPhone)
        cv.put(K_I_ImgG,image)
        cv.put(K_I_UID,iuser)
        val l = db.update(TableItem ,  cv ,"$K_I_ID = ? ", arrayOf(id.toString()))
        Toast.makeText(ctx, "$l", Toast.LENGTH_SHORT).show()
    }


    fun selectItem() : Cursor {
        return db.query(TableItem, null,null,null,null,null,"$K_I_Name ASC")
//        db.rawQuery("select * from $TableProduct ",null)

    }

    fun selectItemById(id: Int): Cursor {
        return db.query(TableItem, null, "$K_I_ID = ?", arrayOf(id.toString()), null, null, null)

    }


    fun deleteItem(id : Int){
         db.delete(TableItem,"$K_I_ID = ? " , arrayOf(id.toString()))
    }



    fun insertUser(id:Int ){
        val cv = ContentValues()
        cv.put(K_U_ID,id)

        val l = db.insert(TableUser, null, cv )
        Toast.makeText(ctx, "$l", Toast.LENGTH_SHORT).show()
    }


}