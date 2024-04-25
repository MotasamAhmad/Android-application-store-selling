package com.example.usedstore.ui.myAds
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.usedstore.Edit_item
import com.example.usedstore.MyDatabase
import com.example.usedstore.R
import com.example.usedstore.Show_item
import com.example.usedstore.databinding.MyAdsFragmentBinding

class myAdsFragment : Fragment() {

    private var _binding: MyAdsFragmentBinding? = null
    private lateinit var myDatabase: MyDatabase
    private lateinit var lv: ListView
    val CAMERA = 111
    val READ = 222
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View {
        _binding = MyAdsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // تهيئة قاعدة البيانات
        myDatabase = MyDatabase(requireContext())
        myDatabase.openDB()
        lv = binding.lvMyAds // تحديد الـ ListView من الملف StoreFragmentBinding

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
            val adb = AlertDialog.Builder(requireContext())
            adb.setItems(
                arrayOf("Show","Edit", "Delete"),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    if (i == 0) {
                        val intent = Intent(requireContext(), Show_item::class.java)
                        // إضافة البيانات التي ترغب في إرسالها إلى النشاط التحرير
                        intent.putExtra("id", l.toInt())

                        startActivity(intent)

                    }else if (i == 1) {
                        // إذا كانت الخيارات تحرير، قم بالانتقال إلى EditMyAdsActivity وإرسال البيانات
                        val intent = Intent(requireContext(), Edit_item::class.java)

                        intent.putExtra("id", l.toInt())
                        startActivity(intent)
                    } else if (i == 2) {
                        val adb2 = AlertDialog.Builder(requireContext())
                        adb2.setTitle("Delete Item ! ")
                        adb2.setMessage("Are you sure ? ")
                        adb2.setPositiveButton(
                            "Yes",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                myDatabase.deleteItem(l.toInt())
                                showList()
                            })
                        adb2.setNegativeButton("No", null)
                        adb2.create().show()
                    }
                }
            )

            adb.create().show()
            return@setOnItemClickListener
        }
    }
        override fun onDestroyView() {
        super.onDestroyView()
            showList()
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
