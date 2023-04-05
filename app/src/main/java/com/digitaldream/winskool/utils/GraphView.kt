package com.digitaldream.winskool.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.digitaldream.winskool.R

enum class GraphType {
    START_AT_LEFT, EXACT, TOUCH_END
}

class GraphView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private lateinit var path: Path
    private lateinit var pathPaint: Paint
    private lateinit var gradientPaint: Paint
    private lateinit var graduationPathPaint: Paint
    private lateinit var gridPaint: Paint

    private lateinit var colorsArray: IntArray
    private lateinit var circlePaint: Paint

    private var extraPadding = convertDpToPx(10f) // TODO : Take input from the user

    private lateinit var coordinates: ArrayList<Pair<Float, Float>>
    private var maxXValue: Float = 0f
    private var minXValue: Float = 0f
    private var maxYValue: Float = 0f
    private var minYValue: Float = 0f

    private var eachPixelAllocationX = 0f
    private var eachPixelAllocationY = 0f

    private var actualWidth = 0
    private var actualHeight = 0

    private var circleRadius = 3f

    private var pathOnTop: Boolean = false
    private var drawGrids: Boolean = false
    private var drawGraduations: Boolean = false

    private lateinit var graphType: GraphType

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.GraphView
        )

        try {
            colorsArray = intArrayOf(
                typedArray.getColor(
                    R.styleable.GraphView_gradientStartColor,
                    ContextCompat.getColor(context, R.color.startColor)
                ),
                typedArray.getColor(
                    R.styleable.GraphView_gradientEndColor,
                    ContextCompat.getColor(context, R.color.endColor)
                )
            )

            path = Path()

            pathPaint = Paint().apply {
                color = typedArray.getColor(
                    R.styleable.GraphView_lineColor,
                    ContextCompat.getColor(context, R.color.pathColor)
                )
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = convertDpToPx(
                    typedArray.getInteger(R.styleable.GraphView_pathWidth, 2).toFloat()
                )
                isDither = true
            }
            gradientPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }
            gridPaint = Paint().apply {
                style = Paint.Style.STROKE
                isAntiAlias = true
                color = typedArray.getColor(
                    R.styleable.GraphView_gridColor,
                    ContextCompat.getColor(context, R.color.gridColor)
                )
            }
            graduationPathPaint = Paint().apply {
                style = Paint.Style.STROKE
                isAntiAlias = true
                color = typedArray.getColor(
                    R.styleable.GraphView_graduationColor,
                    ContextCompat.getColor(context, R.color.graduationColor)
                )
                textSize = convertDpToPx(10f)// TODO : Take the input from the user
            }
            circlePaint = Paint().apply {
                color = typedArray.getColor(
                    R.styleable.GraphView_circleColor,
                    ContextCompat.getColor(context, R.color.pathColor)
                )
                isAntiAlias = true
            }
            circleRadius = convertDpToPx(
                typedArray.getInteger(R.styleable.GraphView_circleRadius, 3).toFloat()
            )
            pathOnTop = typedArray.getBoolean(R.styleable.GraphView_pathOnTop, true)
            drawGrids = typedArray.getBoolean(R.styleable.GraphView_drawGrids, true)
            drawGraduations = typedArray.getBoolean(R.styleable.GraphView_drawGraduations, true)
            graphType = GraphType.values()[typedArray.getInt(R.styleable.GraphView_graphType, 1)]

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }
    }


    fun setCoordinatePoints(coordinates: ArrayList<Pair<Float, Float>>) {
        this.coordinates = coordinates
        getMaxCoordinateValues()
        sortXCoordinates()
        invalidate()
    }

    private fun getMaxCoordinateValues() {
        var firstData = true
        for (i in coordinates) {
            if (firstData) {
                maxXValue = i.first
                minXValue = i.first
                maxYValue = i.second
                minYValue = i.second
                firstData = false
            }
            if (i.first > maxXValue) maxXValue = i.first
            if (i.first < minXValue) minXValue = i.first
            if (i.second > maxYValue) maxYValue = i.second
            if (i.second < minYValue) minYValue = i.second
        }
    }

    private fun sortXCoordinates() {

    }

    private fun convertPxToDp(px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    private fun convertDpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun translateToCanvasY(y: Float): Float {
        return actualHeight.toFloat() - y
    }

    private fun translateToCanvasX(canvas: Canvas, x: Float): Float {
        return x
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)

        eachPixelAllocationX = when (graphType) {
            GraphType.EXACT -> (measuredWidth - extraPadding * 2) / (maxXValue)
            GraphType.START_AT_LEFT -> (measuredWidth - extraPadding * 2) / (maxXValue)
            else -> (measuredWidth - extraPadding * 2) / (maxXValue  - minXValue)
        }
        eachPixelAllocationY = (measuredHeight - extraPadding * 2) / (maxYValue)
        setMeasuredDimension(
            measuredWidth, measuredHeight
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        actualWidth = w - extraPadding.toInt()
        actualHeight = h
        val positions = floatArrayOf(0f, 1f)
        gradientPaint.shader = LinearGradient(
            0f, 0f, 0f, actualHeight.toFloat(), colorsArray, positions, Shader.TileMode.CLAMP
        )
        super.onSizeChanged(w, h, oldw, oldh)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (drawGrids) {
            drawGridLines(canvas)
        }
        drawGradients(canvas)
        drawCoordinates(canvas)
        if (drawGraduations) {
            drawGraduations(canvas)
        }
    }

    private fun drawGradients(canvas: Canvas) {
        path.reset()
        var firstPathDraw = true
        var initCoordinateX = 0f
        var initCoordinateY = 0f
        for (i in coordinates) {
            when (graphType) {
                GraphType.EXACT -> {
                    if (firstPathDraw) {
                        initCoordinateX = i.first
                        initCoordinateY = i.second
                        path.moveTo(
                            reCalculateExactCoordinateWithPadding(
                                (i.first) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY((initCoordinateY - i.second) * eachPixelAllocationY),
                                false))
                        firstPathDraw = false

                        path.lineTo(
                            reCalculateExactCoordinateWithPadding(
                                (i.first) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY(i.second * eachPixelAllocationY), false))
                    }
                    else {
                        path.lineTo(
                            reCalculateExactCoordinateWithPadding(
                                (i.first) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY(i.second * eachPixelAllocationY), false))
                    }
                }
                else -> {
                    if (firstPathDraw) {
                        initCoordinateX = i.first
                        initCoordinateY = i.second
                        path.moveTo(
                            reCalculateExactCoordinateWithPadding(
                                (initCoordinateX - i.first) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY((initCoordinateY - i.second) * eachPixelAllocationY),
                                false))
                        firstPathDraw = false

                        path.lineTo(
                            reCalculateExactCoordinateWithPadding(
                                (initCoordinateX - i.first) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY(i.second * eachPixelAllocationY), false))
                    }
                    else {
                        path.lineTo(
                            reCalculateExactCoordinateWithPadding(
                                (i.first - initCoordinateX) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY(i.second * eachPixelAllocationY), false))
                    }
                }
            }
        }
        when(graphType){
            GraphType.EXACT -> {
                path.lineTo(
                    reCalculateExactCoordinateWithPadding(
                        (maxXValue) * eachPixelAllocationX, true
                    ),
                    reCalculateExactCoordinateWithPadding(canvas.height.toFloat(), false)
                )
            }
            else ->{
                path.lineTo(
                    reCalculateExactCoordinateWithPadding(
                        (maxXValue - initCoordinateX) * eachPixelAllocationX, true
                    ),
                    reCalculateExactCoordinateWithPadding(canvas.height.toFloat(), false)
                )
            }
        }


        canvas.drawPath(path, gradientPaint)
    }

    private fun drawCoordinates(canvas: Canvas) {
        path.reset()
        var firstPathDraw = true
        var initCoordinateX = 0f
        for (i in coordinates) {
            when(graphType){
                GraphType.EXACT -> {
                    if (firstPathDraw) {
                        path.moveTo(
                            reCalculateExactCoordinateWithPadding(
                                (i.first) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY((i.second) * eachPixelAllocationY),
                                false))
                        firstPathDraw = false
                    }
                    path.lineTo(
                        reCalculateExactCoordinateWithPadding(
                            (i.first) * eachPixelAllocationX, true),
                        reCalculateExactCoordinateWithPadding(
                            translateToCanvasY(i.second * eachPixelAllocationY), false))
                    drawCircle(
                        canvas,
                        reCalculateExactCoordinateWithPadding(
                            (i.first) * eachPixelAllocationX, true),
                        reCalculateExactCoordinateWithPadding(
                            translateToCanvasY(i.second * eachPixelAllocationY), false))
                }
                else -> {
                    if (firstPathDraw) {
                        initCoordinateX = i.first
                        path.moveTo(
                            reCalculateExactCoordinateWithPadding(
                                (initCoordinateX - i.first) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY((i.second) * eachPixelAllocationY), false))
                        firstPathDraw = false

                        path.lineTo(
                            reCalculateExactCoordinateWithPadding(
                                (initCoordinateX - i.first) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY(i.second * eachPixelAllocationY), false))
                        drawCircle(
                            canvas,
                            reCalculateExactCoordinateWithPadding(
                                (initCoordinateX - i.first) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY(i.second * eachPixelAllocationY), false))
                    }
                    else {
                        path.lineTo(
                            reCalculateExactCoordinateWithPadding(
                                (i.first - initCoordinateX) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY(i.second * eachPixelAllocationY), false))
                        drawCircle(
                            canvas,
                            reCalculateExactCoordinateWithPadding(
                                (i.first - initCoordinateX) * eachPixelAllocationX, true),
                            reCalculateExactCoordinateWithPadding(
                                translateToCanvasY(i.second * eachPixelAllocationY), false))
                    }
                }
            }

        }


        canvas.drawPath(path, pathPaint)
    }

    private fun drawGridLines(canvas: Canvas) {
        for (k in 0..maxYValue.toInt()) {
            for (i in 0..maxXValue.toInt()) {
                canvas.drawLine(
                    reCalculateExactCoordinateWithPadding(
                        (i.toFloat() * eachPixelAllocationX), true
                    ),
                    reCalculateExactCoordinateWithPadding(
                        translateToCanvasY(k.toFloat() * eachPixelAllocationY), false
                    ),
                    (maxXValue * eachPixelAllocationX),
                    reCalculateExactCoordinateWithPadding(
                        translateToCanvasY(k.toFloat() * eachPixelAllocationY), false
                    ),
                    gridPaint
                )
            }
        }
        for (k in 0..maxXValue.toInt()) {
            for (i in 0..maxYValue.toInt()) {
                canvas.drawLine(
                    reCalculateExactCoordinateWithPadding(
                        k.toFloat() * eachPixelAllocationX, true
                    ),
                    reCalculateExactCoordinateWithPadding(
                        translateToCanvasY(i.toFloat() * eachPixelAllocationY), false
                    ),
                    reCalculateExactCoordinateWithPadding(k * eachPixelAllocationX, true),
                    reCalculateExactCoordinateWithPadding(
                        translateToCanvasY(maxYValue * eachPixelAllocationY), false
                    ),
                    gridPaint
                )
            }
        }

    }

    private fun drawGraduations(canvas: Canvas) {
        for (i in 0..maxYValue.toInt()) {
            canvas.drawText(
                i.toString(),
                reCalculateExactCoordinateWithPadding(0f, true),
                reCalculateExactCoordinateWithPadding(
                    translateToCanvasY(i * eachPixelAllocationY), false
                ),
                graduationPathPaint
            )
        }
        for (i in 0..maxXValue.toInt()) {
            canvas.drawText(
                i.toString(),
                reCalculateExactCoordinateWithPadding(i * eachPixelAllocationX, true),
                reCalculateExactCoordinateWithPadding(canvas.height.toFloat(), false),
                graduationPathPaint
            )
        }
    }

    private fun drawCircle(canvas: Canvas, cx: Float, cy: Float) {
        canvas.drawCircle(
            cx, cy, circleRadius, circlePaint
        )// TODO : Take the input from the user
    }


    private fun reCalculateExactCoordinateWithPadding(coord: Float, x: Boolean): Float {
        return when (x) {
            true -> coord + (extraPadding)
            false -> coord - (extraPadding)
        }
    }
}

/*
// Set this code once in MainActivity or application startup
SciChartSurface.setRuntimeLicenseKey("KcUCClUV0UyTOuKYvbPY7odrpXCPVDYwUw0dN5LTog42l+fIBhZE3K9BSi7WjQEM9XA9JwRlxkHrFTx+WUSWNHD21Fl9hAVJeHSzDJx53fiW6miBHS9QnDAmsgCE3kqq+8Jw5GGy4GJ7mI+MP2YhPO+rT6KMrhaR2PMuJFecRHWo/zykYy8kXsW6iY0M8TiPuVQiRNvE5L/7AMFnQBbdE13OqYs4llgEcg3q2IsIwPMwoZ7DCAykI2aKM4L8TgEPE8yPpzfYXAm7OtYuEhDYaES5YtkwNYNlmCTx9vVJGOCljnyVgBBWOvGmkmhjNvcsWt2nppEvbk0/fvFR3/G1vMC5c5VXaTvR5SLfiEZFbbY99eDZsswwUmMPhhza+jwZf1FdpmABiLBLeRqD8vBy/ESMarKhkPkF4LI6epTgaSfdKEGLsuutiBzwVMhW+tDWjNgPqA1kIMncjDVAYkfgcn3hH+Uruse+Oec0n2Mk42y6EKruSn2IUR8Ea9sOWtoRw3vwcJ9v3X7fXABPsEjX/G6fNMwpOhi7xC5O6zkMkA==");
*/
