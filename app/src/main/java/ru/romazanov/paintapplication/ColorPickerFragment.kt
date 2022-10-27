package ru.romazanov.paintapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import ru.romazanov.paintapplication.databinding.FragmentColorPickerListDialogBinding


class ColorPickerFragment(
    private val currentColor: Int,
    val onClick: (color: Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentColorPickerListDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColorPickerListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.currentColor.setBackgroundColor(currentColor)

        binding.colorsPicker.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val pixel = binding.colorsPicker.drawToBitmap().getPixel(motionEvent.x.toInt(),
                    motionEvent.y.toInt())
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                binding.currentColor.setBackgroundColor(Color.rgb(r,g,b))
                onClick(Color.rgb(r,g,b))
            }
            true
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}