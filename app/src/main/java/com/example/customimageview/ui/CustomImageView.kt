package com.example.customimageview.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.example.customimageview.R
import com.example.customimageview.extensions.dpToPx

class CustomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_SIZE = 40
        const val DEFAULT_BORDER_WIDTH = 2
        const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    @Px
    var borderWith: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)
    @ColorInt
    private var borderColor: Int = Color.WHITE
    private var initials: String = "??"

    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()

    private lateinit var resultBm: Bitmap
    private lateinit var maskBm: Bitmap
    private lateinit var srcBm: Bitmap

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView)
            borderWith = ta.getDimension(
                R.styleable.CustomImageView_aiv_borderWidth,
                context.dpToPx(DEFAULT_BORDER_WIDTH)
            )
            borderColor =
                ta.getColor(R.styleable.CustomImageView_aiv_borderColor, DEFAULT_BORDER_COLOR)
            initials = ta.getString(R.styleable.CustomImageView_aiv_initials) ?: "??"
        }
        scaleType = ScaleType.CENTER_CROP
        setup()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d("DEBUG", "onMeasure")
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.dpToPx(DEFAULT_SIZE).toInt()
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d("DEBUG", "onSizeChanged")

        if (w == 0) return
        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }

        prepareBitmaps(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        Log.d("DEBUG", "onDraw")
        canvas.drawBitmap(resultBm, viewRect, viewRect, null)

        val half = (borderWith / 2).toInt()
        viewRect.inset(half, half)
        canvas.drawOval(viewRect.toRectF(), borderPaint)
    }

    private fun setup() {
        with(maskPaint) {
            color = Color.RED
            style = Paint.Style.FILL
        }

        with(borderPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWith
            color = borderColor
        }
    }

    private fun prepareBitmaps(w: Int, h: Int) {
        Log.d("DEBUG", "prepare bitmaps")
        maskBm = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)

        val maskCanvas = Canvas(maskBm)
        maskCanvas.drawOval(viewRect.toRectF(), maskPaint)
        resultBm = maskBm.copy(Bitmap.Config.ARGB_8888, true)

        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)

        val resultCanvas = Canvas(resultBm)
        resultCanvas.drawBitmap(maskBm, viewRect, viewRect, null)
        resultCanvas.drawBitmap(srcBm, viewRect, viewRect, maskPaint)
    }
}
