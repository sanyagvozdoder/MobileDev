package com.example.mobiledev.presentation.algoritms

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerInputScope
import com.example.mobiledev.presentation.algoritms.util.SplineDot
import com.example.mobiledev.presentation.algoritms.util.SplineMode
import com.example.mobiledev.presentation.algoritms.util.VectorScreenMode

fun PointerInputScope.OnTap(dots:List<SplineDot>, point: Offset, selectionCallback: (s: Int) -> Unit) {
    // nearest point
    var selectedDot: Int = 0
    var dist = 100000f
    val threshold = 1000

    dots.forEachIndexed{ idx, dot ->
        val cDist = (dot.position - point).getDistanceSquared()
        if(cDist < dist)
        {
            selectedDot = idx
            dist = cDist
        }
    }

    if(dist < threshold)
        selectionCallback(selectedDot)
    else
        selectionCallback(-1)
}

val factorials = arrayListOf(1f, 1f, 2f, 6f, 24f)

//https://en.wikipedia.org/wiki/B%C3%A9zier_curve
fun binomialCoefficient(n: Int, i: Int): Float {
    val a1 = factorials[n]
    val a2 = factorials[i]
    val a3 = factorials[n - i]
    return a1 / (a2 * a3)
}

fun bernsteinBasisPolynomials(n: Int, i: Int, t: Float): Float {
    val a1 = Math.pow(t.toDouble(), i.toDouble())
    val a2 = Math.pow((1 - t).toDouble(), (n - i).toDouble())
    return binomialCoefficient(n ,i) * a1.toFloat() * a2.toFloat()
}

fun bezierPoint(t: Float, points: List<Offset>): Offset{
    val n = points.size - 1

    if(t <= 0) return points[0]
    if(t >= 1) return points[n]

    var point = Offset(0f, 0f)

    repeat(points.size) { i ->
        point += points[i] * bernsteinBasisPolynomials(n, i, t)
    }

    return point
}

fun DrawScope.DrawSpline(
    dots:List<SplineDot>,
    screenMode: VectorScreenMode,
    selectedIndex:Int,
    selectionMode:Boolean,
    splineMode: SplineMode,
    mainColor:Color
){
    //spline
    dots.forEachIndexed{index,dot->
        if (dots.size >= 2 && (splineMode == SplineMode.LINE && index != 0 ||
                    splineMode == SplineMode.SHAPE)){
            val step = 0.05f

            var t = 0f;

            var lastPoint = dots[dots.size - 1].position
            var points = arrayListOf(dots[dots.size - 1].position, dots[dots.size - 1].anchor, dots[index].position)


            if(index != 0)
            {
                lastPoint = dots[index - 1].position
                points = arrayListOf(dots[index - 1].position, dots[index - 1].anchor, dots[index].position)
            }
            while (t <= 1f + step) {
                val p = bezierPoint(t, points)
                drawLine(
                    color = mainColor,
                    start = lastPoint,
                    end = p,
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
                lastPoint = p
                t += step
            }
        }
    }

    // dots
    dots.forEachIndexed{index,dot->
        var pointColor = mainColor
        var pointRadius = 10f

        if(index == selectedIndex)
        {
            if(selectionMode)
                pointColor = Color.Yellow
            else
                pointColor = Color.Blue
            pointRadius = 20f
        }

        drawCircle(
            color = pointColor,
            radius = pointRadius,
            center = dot.position
        )
    }


    if(screenMode == VectorScreenMode.EDIT){
        // anchors
        dots.forEachIndexed{index, dot->
            var anchorColor = Color.Red
            var anchorRadius = 5f

            if(index == selectedIndex)
            {
                if(!selectionMode)
                    anchorColor = Color.Yellow
                else
                    anchorColor = Color.Blue
            }

            if(index != dots.size - 1 || splineMode == SplineMode.SHAPE){
                drawCircle(
                    color = anchorColor,
                    radius = anchorRadius,
                    center = dot.anchor
                )

                drawLine(
                    color =  anchorColor,
                    start = dot.position,
                    end = dot.anchor,
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )

                var next = index + 1
                if(index == dots.size - 1 && splineMode == SplineMode.SHAPE)
                    next = 0

                // to next
                drawLine(
                    color = anchorColor,
                    start = dots[next].position,
                    end = dot.anchor,
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
