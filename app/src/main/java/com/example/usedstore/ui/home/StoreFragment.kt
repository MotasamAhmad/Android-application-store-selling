package com.example.usedstore.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.usedstore.MyDatabase
import com.example.usedstore.R
import com.example.usedstore.Show_item
import com.example.usedstore.databinding.StoreFragmentBinding
import android.widget.CursorAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.core.net.toUri

@Suppress("DEPRECATION")
class StoreFragment : Fragment() {
    private var _binding: StoreFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var myDatabase: MyDatabase
    private lateinit var lv: ListView
    val CAMERA = 111
    val READ = 222
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StoreFragmentBinding.inflate(inflater, container, false)

        // تهيئة قاعدة البيانات
        myDatabase = MyDatabase(requireContext())
        myDatabase.openDB()
        lv = binding.lvStore // تحديد الـ ListView من الملف StoreFragmentBinding
        val root: View = binding.root

        showList()

        return root
    }

    private fun showList() {
        val c: Cursor = myDatabase.selectItem()

        val cursorAdapter = object : CursorAdapter(requireContext(), c, false) {
            override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
                // عرض عنصر جديد في ListView
                return LayoutInflater.from(context).inflate(R.layout.item_store, parent, false)
            }

            @SuppressLint("Range")
            override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
                view?.apply {
                    // تعيين بيانات العناصر في الواجهة
                    val itemNameTextView: TextView = findViewById(R.id.tv_item_name)
                    val itemDesTextView: TextView = findViewById(R.id.tv_item_des)
                    val itemPriceTextView: TextView = findViewById(R.id.tv_item_price)
                    val itemDateTextView: TextView = findViewById(R.id.tv_item_date)
                    val itemImageView: ImageView = findViewById(R.id.iv_item)

                    itemNameTextView.text =cursor?.getString(cursor.getColumnIndex(MyDatabase.K_I_Name))
                    itemDesTextView.text =cursor?.getString(cursor.getColumnIndex(MyDatabase.K_I_Des))
                    itemPriceTextView.text =cursor?.getFloat(cursor.getColumnIndex(MyDatabase.K_I_Price)).toString()
                    itemDateTextView.text =cursor?.getString(cursor.getColumnIndex(MyDatabase.K_I_Date))

                    // عرض الصورة إذا كانت متوفرة
                    val image = cursor?.getBlob(cursor.getColumnIndex(MyDatabase.K_I_Img))
                    val imageG = cursor?.getString(cursor.getColumnIndex(MyDatabase.K_I_ImgG))

                    if (image != null) {
                        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                        itemImageView.setImageBitmap(bitmap)
                    }else if(imageG != null && imageG.isNotEmpty()) {
                        val uri = imageG.toUri()
                        itemImageView.setImageURI(uri)
                    }else{
                        null
                    }

                }
            }
        }

        lv.adapter = cursorAdapter

        lv.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(requireContext(), Show_item::class.java)

            intent.putExtra("id", l.toInt())
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }
}



