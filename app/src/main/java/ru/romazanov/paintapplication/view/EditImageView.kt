package ru.romazanov.paintapplication.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import ru.romazanov.paintapplication.model.Line

class EditImageView : androidx.appcompat.widget.AppCompatImageView, OnTouchListener {

    constructor(context: Context) : super(context) {
        setOnTouchListener(this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setOnTouchListener(this)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setOnTouchListener(this)
    }

    private var downx = 0f
    private var downy = 0f
    private var upx = 0f
    private var upy = 0f

    lateinit var canvas: Canvas
    private var paint: Paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        alpha = 0xff
    }
    var matrixImage: Matrix = Matrix()

    private val paths: ArrayList<Line> = ArrayList()
    private var currentColor = 0
    private var strokeWidth = 0


    fun setNewImage(alteredBitmap: Bitmap, bmp: Bitmap) {
        canvas = Canvas(alteredBitmap)
        canvas.drawBitmap(bmp, matrixImage, paint)
        setImageBitmap(alteredBitmap)
    }

    fun init(height: Int, width: Int) {
        val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mBitmap)
        canvas.drawColor(Color.WHITE)
        currentColor = Color.GREEN
        strokeWidth = 30
        setImageBitmap(mBitmap)
    }

    fun setColor(color: Int) {
        paint.color = color
    }

    fun getColor(): Int {
        return paint.color
    }


    fun setStrokeWidth(width: Float) {
        paint.strokeWidth = width
    }

    fun save(): Bitmap {
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        return bitmap
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downx = getPointerCords(event)[0]
                downy = getPointerCords(event)[1]
            }
            MotionEvent.ACTION_MOVE -> {
                upx = getPointerCords(event)[0]
                upy = getPointerCords(event)[1]
                canvas.drawLine(downx, downy, upx, upy, paint)
                invalidate()
                downx = upx
                downy = upy
            }
            MotionEvent.ACTION_UP -> {
                upx = getPointerCords(event)[0]
                upy = getPointerCords(event)[1]
                canvas.drawLine(downx, downy, upx, upy, paint)
                invalidate()
            }
            else -> {
            }
        }
        return true
    }

    private fun getPointerCords(e: MotionEvent): FloatArray {
        val index = e.actionIndex
        val cords = floatArrayOf(e.getX(index), e.getY(index))
        val matrix = Matrix()
        imageMatrix.invert(matrix)
        matrix.postTranslate(scrollX.toFloat(), scrollY.toFloat())
        matrix.mapPoints(cords)
        return cords
    }
}