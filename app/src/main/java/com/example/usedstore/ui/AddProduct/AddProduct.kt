package com.example.usedstore.ui.AddProduct

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.usedstore.MyDatabase
import com.example.usedstore.R
import java.io.ByteArrayOutputStream

class AddProduct : Fragment() {
    private lateinit var myDatabase: MyDatabase
    private lateinit var bitmap: Bitmap
    private lateinit var iv: ImageView
    var imageString = ""

    private val CAMERA_PERMISSION_REQUEST = 100
    private val CAMERA_REQUEST = 101
    private val GALLERY_REQUEST = 102

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDatabase = MyDatabase(requireContext())
        myDatabase.openDB()

        val addButton: Button = view.findViewById(R.id.btn_add_product)
        addButton.setOnClickListener {
            // جلب البيانات من حقول الإدخال
            val name = view.findViewById<EditText>(R.id.edtxt_add_name).text.toString()
            val priceText = view.findViewById<EditText>(R.id.edtxt_add_price).text.toString()
            val description = view.findViewById<EditText>(R.id.edtxt_add_description).text.toString()
            val city = view.findViewById<EditText>(R.id.edtxt_add_city).text.toString()
            val contactNumber = view.findViewById<EditText>(R.id.edtxt_add_contactNumber).text.toString()

            if (name.isNotEmpty() && priceText.isNotEmpty() && description.isNotEmpty() && city.isNotEmpty() && contactNumber.isNotEmpty()) {
                // تحويل السعر إلى Float
                val price = priceText.toFloatOrNull() ?: 0.0f

                if (::bitmap.isInitialized && !imageString.isNotEmpty()) {
                    // If an image is selected from the camera
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    myDatabase.insertItem(name, price, description, city, contactNumber, baos.toByteArray(), 1)
                    showInsertSuccessDialog()
                } else if (imageString.isNotEmpty()) {
                    // إذا تم تحديد صورة من المعرض
                    myDatabase.insertItem(name, price, description, city, contactNumber, imageString, 1)
                    showInsertSuccessDialog()
                } else {
                    Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // تحديد الصورة عند النقر عليها
        iv = view.findViewById(R.id.iv_add_img)
        iv.setOnClickListener {
            // عرض مربع حوار لاختيار الكاميرا أو المعرض
            val adb = AlertDialog.Builder(requireContext())
            adb.setItems(arrayOf("Camera", "Gallery"), DialogInterface.OnClickListener { dialogInterface, i ->
                if (i == 0) {
                    // التحقق من إذن الوصول إلى الكاميرا
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // إذا لم يتم منح الإذن، قم بطلبه
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
                    } else {
                        // إذا تم منح الإذن، فتح كاميرا الهاتف
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, CAMERA_REQUEST)
                    }
                } else {
                    // التحقق من إذن الوصول إلى المعرض
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // إذا لم يتم منح الإذن، قم بطلبه
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_REQUEST)
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

    // استجابة لنتيجة التقاط صورة من الكاميرا
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            bitmap = data?.extras?.get("data") as Bitmap
            iv.setImageBitmap(bitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myDatabase.closeDB()
    }

    // عرض رسالة بنجاح إضافة المنتج
    private fun showInsertSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("The addition process was completed successfully.")

        builder.setPositiveButton("OK") { _, _ ->
            view?.findViewById<EditText>(R.id.edtxt_add_name)?.text?.clear()
            view?.findViewById<EditText>(R.id.edtxt_add_price)?.text?.clear()
            view?.findViewById<EditText>(R.id.edtxt_add_description)?.text?.clear()
            view?.findViewById<EditText>(R.id.edtxt_add_city)?.text?.clear()
            view?.findViewById<EditText>(R.id.edtxt_add_contactNumber)?.text?.clear()
            iv.setImageResource(R.drawable.baseline_add_a_photo_24)
        }

        builder.create().show()
    }
}
