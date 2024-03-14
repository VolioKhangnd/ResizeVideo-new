package com.video.mini.tools.zip.compress.convert.simple.tiny.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.video.mini.tools.zip.compress.convert.simple.tiny.R
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.hypot
import kotlin.math.min

interface OnProgressChangedListener {
    fun onProgress(progress: Float)
}

class ProgressCircle : View {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintDefault = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintTextProcess = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private var stroke: Float = 50f
    private var strokeColor = Color.RED
    private var strokeDefault: Float = 50f
    private var strokeColorDefault = Color.GRAY
    private var textStyle = Typeface.NORMAL
    private var textColor = Color.BLACK
    private var textSize = 24f

    constructor(context: Context) : super(context) {
        initial(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initial(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initial(attrs)
    }

    private var isDown = false
    private var oldAngle = 0f
    private var isClockwise = true
    private var onProgressChange: OnProgressChangedListener? = null
    private var startPoint = PointF(0f, 0f)
    private var endPoint = PointF(0f, 0f)
    private var centerO = PointF(0f, 0f)
    private var format: String? = null
    var totalProgress: Float = 100f
        set(value) {
            field = value
            invalidate()
        }

    var currentProgress: Float = 0.0f
        set(value) {
            field = value
            invalidate()
        }

    private var sweepAngle: Float
        get() = currentProgress / (totalProgress / 360)
        set(value) {
            currentProgress = value * (totalProgress / 360)
        }


    private fun isClickInStroke(x: Float, y: Float): Boolean {
        val distanceToCenter = hypot((x - centerX).toDouble(), (y - centerY).toDouble())
        return distanceToCenter in (radius - strokeDefault / 2..radius + strokeDefault / 2)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isDown = isClickInStroke(event.x, event.y)
                if (isDown) {
                    endPoint.set(event.x, event.y)
                    val newSweepAngle = calculateAngle()
                    oldAngle = sweepAngle
                    sweepAngle = newSweepAngle
                    currentProgress = (totalProgress / 360) * sweepAngle
                    onProgressChange?.onProgress(currentProgress)
                    invalidate()
                    return true
                }
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDown) {
                    endPoint.set(event.x, event.y)
                    val newSweepAngle = calculateAngle()
                    oldAngle = sweepAngle
                    sweepAngle = newSweepAngle
                    currentProgress = (totalProgress / 360) * sweepAngle
                    onProgressChange?.onProgress(currentProgress)
                    invalidate()
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                isDown = false
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCircleDefault(canvas)
        drawCircle(canvas)
        drawTextProgress(canvas)
    }

    private fun drawTextProgress(canvas: Canvas) {
        val text = if (format == null) currentProgress.toString() else String.format(
            format!!,
            currentProgress
        )
        val textBounds = Rect()
        paintTextProcess.getTextBounds(text, 0, text.length, textBounds)
        val textHeight = textBounds.bottom - textBounds.top
        canvas.drawText(
            text, centerO.x, centerO.y + textHeight / 2, paintTextProcess
        )

    }

    private fun calculateAngle(): Float {
        val x1 = (startPoint.x - centerO.x).toDouble()
        val y1 = (startPoint.y - centerO.y).toDouble()
        val magnitudeVectorO1 = hypot(x1, y1)

        val x2 = (endPoint.x - centerO.x).toDouble()
        val y2 = (endPoint.y - centerO.y).toDouble()
        val magnitudeVectorO2 = hypot(x2, y2)

        val dotProduct = x1 * x2 + y1 * y2
        val cos = dotProduct / (magnitudeVectorO1 * magnitudeVectorO2)

        val angleInRadians = acos(cos)
        val angleInDegrees = Math.toDegrees(angleInRadians)
        if (endPoint.x <= centerO.x) {
            return 360 - angleInDegrees.toFloat()
        }
        return angleInDegrees.toFloat()
    }


    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private fun drawCircleDefault(canvas: Canvas) {
        centerX = width / 2f
        centerY = height / 2f
        centerO.set(centerX, centerY)
        canvas.drawCircle(centerX, centerY, radius, paintDefault)
    }

    private fun drawCircle(canvas: Canvas) {
        val path = Path()
        val rectF = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        startPoint.set(centerX, centerY - radius)
        path.addArc(rectF, -90f, sweepAngle)
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawPath(path, paint)
    }

    fun format(format: String) {
        this.format = format
        invalidate()
    }

    fun setOnProgressChanged(onProgressChange: OnProgressChangedListener) {
        this.onProgressChange = onProgressChange
    }

    @SuppressLint("Recycle", "ResourceType")
    private fun initial(attrs: AttributeSet?) {
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.ProgressCircle)
        textSize = attrs.getDimension(R.styleable.ProgressCircle_textSizeProgress, textSize)
        textStyle = attrs.getInt(R.styleable.ProgressCircle_textStyleProgress, textStyle)
        textColor = attrs.getColor(R.styleable.ProgressCircle_textColorProgress, textColor)
        stroke = attrs.getDimension(R.styleable.ProgressCircle_strokeWidth, stroke)
        strokeDefault =
            attrs.getDimension(R.styleable.ProgressCircle_strokeWidthDefault, strokeDefault)
        strokeColor = attrs.getColor(R.styleable.ProgressCircle_progressColor, strokeColor)
        strokeColorDefault =
            attrs.getColor(R.styleable.ProgressCircle_progressColorDefault, strokeColorDefault)
        currentProgress = attrs.getFloat(R.styleable.ProgressCircle_toProgress, 0f)
        totalProgress = attrs.getFloat(R.styleable.ProgressCircle_progress, 100f)

        radius = attrs.getDimension(R.styleable.ProgressCircle_radius, radius)
        attrs.recycle()

        paint.apply {
            strokeWidth = stroke
            color = strokeColor
            style = Paint.Style.STROKE
        }

        paintDefault.apply {
            strokeWidth = strokeDefault
            color = strokeColorDefault
            style = Paint.Style.STROKE
        }

        paintTextProcess.apply {
            color = textColor
            textSize = this@ProgressCircle.textSize
            typeface = Typeface.create(Typeface.DEFAULT, this@ProgressCircle.textStyle)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = abs((min(w, h) / 2f) * 0.8f - strokeDefault / 2)
    }

}