package com.example.usedstore

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class Show_item : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_item)

        val myDatabase = MyDatabase(this)
        myDatabase.openDB()

        val sh_name = findViewById<TextView>(R.id.tv_sh_name)
        val sh_price = findViewById<TextView>(R.id.tv_sh_price)
        val sh_des = findViewById<TextView>(R.id.tv_sh_description)
        val sh_loc = findViewById<TextView>(R.id.tv_sh_location)
        val sh_num = findViewById<TextView>(R.id.tv_sh_connectNumber)
        val btn_call = findViewById<Button>(R.id.btn_call)
        val sh_imgView = findViewById<ImageView>(R.id.imgv_sh_img)

        val id = intent.getIntExtra("id", 0)
        val item = myDatabase.selectItemById(id)

        if (item.moveToFirst()) {
            sh_name.text = item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_Name))
            sh_price.text = item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_Price))
            sh_des.text = item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_Des))
            sh_loc.text = item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_loc))
            sh_num.text = item.getString((item.getColumnIndexOrThrow(MyDatabase.K_I_NumPhone)))

            val image = item.getBlob(item.getColumnIndexOrThrow(MyDatabase.K_I_Img))
            val imageG = item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_ImgG))
            if (image != null) {
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                sh_imgView.setImageBitmap(bitmap)
            }else if(imageG != null && imageG.isNotEmpty()) {
                val uri = imageG.toUri()
                sh_imgView.setImageURI(uri)
            }else {
                    Log.e("Show_item", "فشل في تحويل الصورة من البيانات")
                }
            } else {
                Log.e("Show_item", "بيانات الصورة فارغة أو غير متاحة")
            }


        // إضافة الاستماع للزر للاتصال برقم الهاتف
        btn_call.setOnClickListener {
            // الحصول على رقم الهاتف من sh_num
            val phoneNumber = sh_num.text.toString()

            // إنشاء Intent للاتصال برقم الهاتف
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))

            // بدء النشاط للاتصال
            startActivity(dialIntent)
        }

    }
}
