package ru.romazanov.paintapplication

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.romazanov.paintapplication.databinding.FragmentImageDialogBinding

class ImagesFragment(
    val onClick:(image: String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentImageDialogBinding? = null

    private val binding get() = _binding!!

    private var imageUriList: List<String> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDialogBinding.inflate(inflater, container, false)

        imageUriList = getCameraImages()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.grid.adapter = context?.let {
            ImageAdapter(
                it, R.layout.image_item,
                imageUriList as ArrayList<String>
            )
        }

        binding.grid.setOnItemClickListener { adapterView, _, i, _ ->
            onClick(adapterView.getItemAtPosition(i).toString())
        }
    }

    private fun getCameraImages(): List<String> {
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val listOfAllImages = ArrayList<String>()

        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        val cursor = activity?.contentResolver?.query(uri, projection, null,
            null, null)

        val columnIndexData = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val columnIndexFolderName = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            val absolutePathOfImage = cursor.getString(columnIndexData)
            listOfAllImages.add(absolutePathOfImage)
        }
        return listOfAllImages
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}