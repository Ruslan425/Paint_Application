package ru.romazanov.paintapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.slider.RangeSlider
import ru.romazanov.paintapplication.databinding.ActivityMainBinding
import java.io.File
import java.io.OutputStream
import java.util.Objects

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermissions(PERMISSIONS, MY_PERMISSIONS_REQUEST)

        binding.btnSave.setOnClickListener {
            val bmp = binding.drawView.save()
            val imageOutStream: OutputStream?
            val cv = ContentValues()

            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.png")

            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png")

            cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
            try {
                imageOutStream = contentResolver.openOutputStream(uri!!)
                bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream)
                imageOutStream!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.btnColor.setOnClickListener {
            ColorPickerFragment(binding.drawView.getColor()) {
                binding.drawView.setColor(it)
            }.show(supportFragmentManager, "dialog")
        }

        binding.btnStroke.setOnClickListener {
            if (binding.bar.visibility == View.VISIBLE) binding.bar.visibility =
                View.GONE else binding.bar.visibility = View.VISIBLE
        }

        binding.bar.valueFrom = 0.0f
        binding.bar.valueTo = 200.0f
        binding.bar.addOnChangeListener(RangeSlider.OnChangeListener { _, value, _ ->
            binding.drawView.setStrokeWidth(value)
        })

        val vto = binding.drawView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.drawView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = binding.drawView.width
                val height =binding.drawView.height
                binding.drawView.init(height, width)
            }
        })

        binding.btnImage.setOnClickListener {
            var bmp: Bitmap?
            var alteredBitmap: Bitmap?
            ImagesFragment() {
                val image = it
                if (image.isNotEmpty()) {
                    val bmpFactoryOptions = BitmapFactory.Options()
                    bmpFactoryOptions.inJustDecodeBounds = true
                    bmp = BitmapFactory
                        .decodeStream(
                            File(image).inputStream(), null, bmpFactoryOptions
                        )
                    bmpFactoryOptions.inJustDecodeBounds = false
                    bmp = BitmapFactory
                        .decodeStream(
                            File(image).inputStream(), null, bmpFactoryOptions
                        )

                     alteredBitmap = Bitmap.createBitmap(
                        bmp!!.width,
                        bmp!!.height, bmp!!.config
                    )
                    binding.drawView.setNewImage(alteredBitmap!!, bmp!!)
                }
            }.show(supportFragmentManager, "image_dialog")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST && grantResults.isNotEmpty()){
            if (isPermissions()){
                (Objects.requireNonNull(this.getSystemService(Context.ACTIVITY_SERVICE)) as ActivityManager).clearApplicationUserData()
                recreate()
            }
        }
    }

    private fun isPermissions():Boolean{
        PERMISSIONS.forEach {
            if (checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED){
                return true
            }
        }
        return false
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST = 1234
        private val PERMISSIONS = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }
}