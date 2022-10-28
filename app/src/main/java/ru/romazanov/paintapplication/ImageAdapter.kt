package ru.romazanov.paintapplication

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import java.io.File

class ImageAdapter(context: Context, int: Int, list: ArrayList<String>): ArrayAdapter<String>(context, int, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var imageView: View = View.inflate(context, R.layout.image_item, null)
        val imageUri = Uri.parse(getItem(position))

        if(convertView == null) {
            imageView = LayoutInflater.from(context).inflate(R.layout.image_item, null)
        }

        Glide.with(context)
            .load(imageUri.path?.let { File(it) })
            .centerCrop()
            .into(imageView.findViewById(R.id.imageView))

        return imageView
    }
}