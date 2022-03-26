package com.dogukan.yemektariflerikitab

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import java.lang.Exception


class TarifFragment : Fragment() {

    private lateinit var saveButton: Button
    private lateinit var imageView: ImageView
    private lateinit var editTextFoodName: EditText
    private lateinit var editTextFoodDesc: EditText

    private var secilenGorsel : Uri? = null
    private var secilenBitmap : Bitmap?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton = view.findViewById(R.id.btnSave)
        imageView = view.findViewById(R.id.imageView)

        saveButton.setOnClickListener {
            saveFood(it)
        }

        imageView.setOnClickListener {
            gorsel_sec(it)
        }
    }

    fun saveFood(view : View){
        val foodName = editTextFoodName.text.toString()
        val foodDesc = editTextFoodDesc.text.toString()

        try{

            context?.let {
                val db = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE,null)
                db.execSQL("CREATE TABLE IF NOT EXISTS yemekler (id INTEGER PRIMARY KEY, " +
                        "yemek_ismi VARCHAR, yemek_malzemesi VARCHAR )")
               val sqlString = " INSERT INTO yemekler (yemek_ismi, yemek_malzemesi) VALUES (?,?)"
                val statement = db.compileStatement(sqlString.toString())
                statement.bindString(1,foodName)
                statement.bindString(2,foodDesc)
                statement.execute()
            }



        }catch (e:Exception){

        }

        val actions = TarifFragmentDirections.actionTarifFragmentToListeFragment()
        Navigation.findNavController(view).navigate(actions)
    }

    fun gorsel_sec(view:View){

        activity?.let {
            if (ContextCompat.checkSelfPermission
                    (it.applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

            }else{
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && requestCode == Activity.RESULT_OK && data != null){
            secilenGorsel = data.data

            try{

                context?.let {
                    if (secilenGorsel != null) {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source =
                                ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                            println(imageView)
                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)
                            imageView.refreshDrawableState()
                        }
                    }
                }
            }catch(e : Exception){
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}

