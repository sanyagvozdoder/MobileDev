package com.example.mobiledev.presentation.algoritms

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.DrawSpline(dots:List<Offset>){
    dots.forEachIndexed{index,dot->
        drawCircle(
            color = Color.Blue,
            radius = 20f,
            center = dot
        )

        if (dots.size >= 2 && index != 0){
            drawLine(
                color =  Color.Blue,
                start = dots[index - 1],
                end = dots[index],
                strokeWidth = 10f,
                cap = StrokeCap.Round
            )
        }
    }
}