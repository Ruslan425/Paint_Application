package ru.romazanov.paintapplication

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.RangeSlider
import ru.romazanov.paintapplication.databinding.ActivityMainBinding
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUndo.setOnClickListener { binding.drawView.undo() }

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
            binding.drawView.setStrokeWidth(value.toInt())
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
    }
}