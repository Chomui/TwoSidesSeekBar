package com.example.twosidesseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min

class TwoSideSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var paintBackground: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.GRAY
    }
    private var paintProgress: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }
    private var paintDot: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }
    private var paintDotShadow: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        alpha = 255 / 5
    }

    private var progress: Int = 0

    private var min = -50
    private var max = 50

    private var progressLineHeight = 2F.toDp()
    private var dotRadius = progressLineHeight * 4
    private var dotShadowRadius = progressLineHeight * 10

    private var progressBackgroundTint = Color.GRAY
    private var progressTint = Color.BLACK
    private var colorControlActivated = Color.BLACK

    private var pressedDot = false

    private var onChangeListener: OnChangeListener? = null

    init {
        attrs?.let(::initAttrs)

        setDotsRadius()

        setColors()
    }

    override fun onDraw(canvas: Canvas?) {

        if (canvas == null) return

        // background
        canvas.drawRect(
            0F + dotShadowRadius,
            height / 2F - progressLineHeight / 2F,
            width - dotShadowRadius,
            height / 2F + progressLineHeight / 2F,
            paintBackground
        )

        // progress
        canvas.drawRect(
            width / 2F + (width / 2F - dotShadowRadius) / max * progress,
            height / 2F - progressLineHeight / 2F,
            width / 2F,
            height / 2F + progressLineHeight / 2F,
            paintProgress
        )

        // dot
        if (pressedDot) {
            canvas.drawCircle(
                width / 2F + (width / 2F - dotShadowRadius) / max * progress,
                height / 2F,
                dotShadowRadius,
                paintDotShadow
            )
        }
        canvas.drawCircle(
            width / 2F + (width / 2F - dotShadowRadius) / max * progress,
            height / 2F,
            dotRadius,
            paintDot
        )
    }

    private fun setDotsRadius() {
        dotRadius = progressLineHeight * 4
        dotShadowRadius = progressLineHeight * 10
    }

    private fun setColors() {
        paintBackground.color = progressBackgroundTint
        paintDot.color = colorControlActivated
        paintDotShadow.color = colorControlActivated
        paintDotShadow.alpha = 255 / 5
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        event?.let { calculateProgress(it.x) }

        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                pressedDot = true
                invalidate()
                true
            }
            MotionEvent.ACTION_MOVE -> {
                pressedDot = true
                invalidate()
                true
            }
            MotionEvent.ACTION_UP -> {
                pressedDot = false
                invalidate()
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                pressedDot = false
                invalidate()
                true
            }
            else -> false
        }
    }

    private fun calculateProgress(x: Float) {
        progress = when {
            x < dotShadowRadius -> min

            x > width - dotShadowRadius -> max

            else -> {
                when {
                    x < width / 2 -> {
                        (abs(x - width / 2F) / -(width / 2F - dotShadowRadius) * max).toInt()
                    }
                    x > width / 2 -> {
                        (abs(x - width / 2F) / (width / 2F - dotShadowRadius) * max).toInt()
                    }
                    else -> {
                        (min + max) / 2
                    }
                }
            }
        }

        onChangeListener?.onProgressChanged(progress, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth
        val desiredHeight = ceil(dotShadowRadius * 2).toInt()

        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return if (specMode == MeasureSpec.EXACTLY) {
            specSize
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                min(desiredSize, specSize)
            } else {
                desiredSize
            }
        }
    }

    fun setMax(value: Int) {
        if (value <= 0) {
            throw IllegalArgumentException("Value must be greater than zero!")
        }
        max = value
        min = -value
        invalidate()
    }

    fun setProgress(value: Int) {
        if (value > max || value < min) {
            throw IllegalArgumentException("Value must be in range min..max")
        }
        progress = value
        onChangeListener?.onProgressChanged(progress, false)
        invalidate()
    }

    fun setProgressBackgroundTint(@ColorInt color: Int) {
        progressBackgroundTint = color
        paintBackground.color = progressBackgroundTint
        invalidate()
    }

    fun setProgressTint(@ColorInt color: Int) {
        progressTint = color
        paintProgress.color = progressTint
        invalidate()
    }

    fun setColorControlActivated(@ColorInt color: Int) {
        colorControlActivated = color
        paintDot.color = colorControlActivated
        paintDotShadow.color = colorControlActivated
    }

    fun setProgressLineHeight(value: Float) {
        progressLineHeight = value
        setDotsRadius()
        requestLayout()
        invalidate()
    }

    fun setOnChangeListener(listener: OnChangeListener) {
        this.onChangeListener = listener
    }

    interface OnChangeListener {
        fun onProgressChanged(progress: Int, fromUser: Boolean)
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TwoSideSeekBar)

        progressBackgroundTint = typedArray.getColor(
            R.styleable.TwoSideSeekBar_android_progressBackgroundTint,
            progressBackgroundTint
        )
        progressTint = typedArray.getColor(
            R.styleable.TwoSideSeekBar_android_progressTint,
            progressTint
        )
        colorControlActivated = typedArray.getColor(
            R.styleable.TwoSideSeekBar_android_colorControlActivated,
            colorControlActivated
        )
        progressLineHeight = typedArray.getDimension(
            R.styleable.TwoSideSeekBar_tssb_progress_line_height,
            progressLineHeight
        )

        typedArray.recycle()
    }

    private fun Float.toDp(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            resources.displayMetrics
        )
    }

    private fun Double.toDp(): Double {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        ).toDouble()
    }
}