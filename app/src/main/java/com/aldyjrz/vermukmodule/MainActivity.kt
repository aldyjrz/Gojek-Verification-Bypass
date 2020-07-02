package com.aldyjrz.vermukmodule

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    val PICK_PHOTO_FOR_AVATAR = 1
    fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR)
    }
    fun encodeTobase64(images: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        images.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        val b: ByteArray = baos.toByteArray()
        val imageEncoded =
            Base64.encodeToString(b, Base64.DEFAULT)
        Log.e("LOOK", imageEncoded)

        image.setImageBitmap(images)
        val prefs = getSharedPreferences("TOI", 0);
        prefs.edit().putString("srcImage", imageEncoded).apply()

        return imageEncoded
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return
            }
            val inputStream: InputStream =
                applicationContext.getContentResolver().openInputStream(data.data!!)!!
            val yourSelectedImage = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()

            yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 25, baos)

            encodeTobase64(yourSelectedImage)
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs = getSharedPreferences("TOI", 0)
        val x = prefs.getString("srcImage", "cie")
        if(!x.equals("cie")){
            if (x != null) {
                stringToImage(x)
            }
        }
        btn_go.setOnClickListener {
            val b = et_string.getText().toString()
            if(b.isNotEmpty() && b.contains("/")) {
                prefs.edit().putString("srcImage", b).apply()
                stringToImage(b)
            }else{
                Toast.makeText(this, "Field kosong", Toast.LENGTH_SHORT).show()
            }

        }
        btn_choose.setOnClickListener{
            pickImage()
        }




    }
    fun stringToImage(base64: String){
        val baos = ByteArrayOutputStream()

        val decodedString: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        val decodedByte =
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        decodedByte.compress(Bitmap.CompressFormat.JPEG, 20, baos)

        image.setImageBitmap(decodedByte)

        image.borderColor = Color.GRAY
        image.borderWidth = 10

    }
}
