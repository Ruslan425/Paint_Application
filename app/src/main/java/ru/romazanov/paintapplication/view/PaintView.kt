package ru.romazanov.paintapplication.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Picture
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import ru.romazanov.paintapplication.model.Line
import java.io.File
import kotlin.math.abs

class PaintView constructor(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var mX = 0f
    private var mY = 0f
    private lateinit var mPath: Path
    private val mPaint: Paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        alpha = 0xff
    }

    private val paths: ArrayList<Line> = ArrayList()
    private var currentColor = 0
    private var strokeWidth = 0
    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)

    fun init(height: Int, width: Int) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
        currentColor = Color.GREEN
        strokeWidth = 20
    }

    fun setColor(color: Int) {
        currentColor = color
    }

    fun getColor(): Int {
        return currentColor
    }


    fun setStrokeWidth(width: Int) {
        strokeWidth = width
    }

    fun undo() {
        if (paths.size != 0) {
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    fun save(): Bitmap {
        return mBitmap
    }

//    fun setMyBackground(image: String) {
//        Log.i("SET", image)
//        val uri = Uri.parse(image)
//        Glide.with(context)
//            .asBitmap()
//            .load(uri.path?.let { File(it) })
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//
//                    try {
//                        mCanvas.save()
//                        mCanvas.drawBitmap(resource, 0f,0f, mBitmapPaint)
//                        mCanvas.restore()
//                    }catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {}
//            })
//        Glide.with(context)
//            .asDrawable()
//            .load(uri.path?.let { File(it) })
//            .into(object: CustomTarget<Drawable>() {
//                override fun onResourceReady(
//                    resource: Drawable,
//                    transition: Transition<in Drawable>?
//                ) {
//                    background = resource
//                }
//                override fun onLoadCleared(placeholder: Drawable?) {
//
//                }
//
//            })
//
//    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        val backgroundColor = Color.WHITE
        mCanvas.drawColor(backgroundColor)

        for (fp in paths) {
            mPaint.color = fp.color
            mPaint.strokeWidth = fp.lineWight.toFloat()
            mCanvas.drawPath(fp.path, mPaint)
        }
        canvas.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
        canvas.restore()
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = Line(currentColor, strokeWidth, mPath)
        paths.add(fp)
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH || dy >= TOUCH) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath.lineTo(mX, mY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }

    companion object {
        private const val TOUCH = 4f
    }
}