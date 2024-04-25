package com.example.usedstore

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.usedstore.ui.myAds.myAdsFragment
import java.io.ByteArrayOutputStream

class Edit_item : AppCompatActivity() {
    private lateinit var bitmap: Bitmap
    private lateinit var iv: ImageView
    var imageString = ""

    private val CAMERA_PERMISSION_REQUEST = 100
    private val CAMERA_REQUEST = 101
    private val GALLERY_REQUEST = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        val db = MyDatabase(this)
        db.openDB()

        val ed_name = findViewById<EditText>(R.id.edtxt_edit_name)
        val ed_price = findViewById<EditText>(R.id.edtxt_edit_price)
        val ed_des = findViewById<EditText>(R.id.edtxt_edit_description)
        val ed_loc = findViewById<EditText>(R.id.edtxt_edit_location)
        val ed_num = findViewById<EditText>(R.id.edtxt_edit_contactNumber)
        val ed_imgView = findViewById<ImageView>(R.id.iv_edit_img)
        val btn_edit = findViewById<Button>(R.id.btn_edit_product)

        val id = intent.getIntExtra("id", 0)
        val item = db.selectItemById(id)

        if (item.moveToFirst()) {
            ed_name.setText(item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_Name)))
            ed_price.setText(item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_Price)))
            ed_des.setText(item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_Des)))
            ed_loc.setText(item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_loc)))
            ed_num.setText(item.getString((item.getColumnIndexOrThrow(MyDatabase.K_I_NumPhone))))

            val image = item.getBlob(item.getColumnIndexOrThrow(MyDatabase.K_I_Img))
            val imageG = item.getString(item.getColumnIndexOrThrow(MyDatabase.K_I_ImgG))


            if (image != null) {
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                ed_imgView.setImageBitmap(bitmap)
            }else if(imageG != null && imageG.isNotEmpty()) {
                val uri = imageG.toUri()
                ed_imgView.setImageURI(uri)
            } else {
                    Toast.makeText(this, "فشل في تحويل الصورة من البيانات", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "بيانات الصورة فارغة أو غير متاحة", Toast.LENGTH_SHORT).show()
            }


        btn_edit.setOnClickListener {
            val ed_nameIn = ed_name.text.toString()
            val ed_priceIn = ed_price.text.toString()
            val ed_desIn = ed_des.text.toString()
            val ed_locIn = ed_loc.text.toString()
            val ed_numIn = ed_num.text.toString()

            if (ed_nameIn.isNotEmpty() && ed_priceIn.isNotEmpty() && ed_desIn.isNotEmpty() && ed_locIn.isNotEmpty() && ed_numIn.isNotEmpty()) {
                if (::bitmap.isInitialized && !imageString.isNotEmpty()) {
                    // If an image is selected from the camera
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    db.updateItem( id, ed_nameIn, ed_priceIn.toFloat(), ed_desIn, ed_locIn, ed_numIn, baos.toByteArray(), 1)
                    showInsertSuccessDialog()
                } else if (imageString.isNotEmpty()) {
                    // إذا تم تحديد صورة من المعرض
                    db.updateItem( id, ed_nameIn, ed_priceIn.toFloat(), ed_desIn, ed_locIn, ed_numIn, imageString, 1)
                    showInsertSuccessDialog()
                } else {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        iv = findViewById(R.id.iv_edit_img)
        iv.setOnClickListener {
            // عرض مربع حوار لاختيار الكاميرا أو المعرض
            val adb = AlertDialog.Builder(this)
            adb.setItems(arrayOf("Camera", "Gallery"), DialogInterface.OnClickListener { dialogInterface, i ->
                if (i == 0) {
                    // التحقق من إذن الوصول إلى الكاميرا
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // إذا لم يتم منح الإذن، قم بطلبه
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
                    } else {
                        // إذا تم منح الإذن، فتح كاميرا الهاتف
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, CAMERA_REQUEST)
                    }
                } else {
                    // التحقق من إذن الوصول إلى المعرض
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // إذا لم يتم منح الإذن، قم بطلبه
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_REQUEST)
                    } else {
                        // إذا تم منح الإذن، فتح المعرض
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                        getImage.launch(intent)
                    }
                }
            })
            adb.create().show()
        }
    }
    // استجابة لنتيجة اختيار الصورة من المعرض
    val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val data = it.data
            val imageUri = data?.data
            iv.setImageURI(imageUri)
            imageString = imageUri.toString()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            bitmap = data?.extras?.get("data") as Bitmap
            iv.setImageBitmap(bitmap)
        }
    }

    private fun showInsertSuccessDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage("The editing process was completed successfully.")

        builder.setPositiveButton("OK") { _, _ ->
                val intent = Intent(this, myAdsFragment::class.java)

                startActivity(intent)

        }

        builder.create().show()
    }
}
