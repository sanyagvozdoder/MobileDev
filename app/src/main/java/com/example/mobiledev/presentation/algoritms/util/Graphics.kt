package com.example.mobiledev.presentation.algoritms.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

data class Vertex(
    val x: Float,
    val y: Float,
    val z: Float
)

data class Face(
    val a: Int,
    val b: Int,
    val c: Int,
    val color: Color = Color.Blue
)

data class Camera(
    val screen: Size,
    val focalLength: Double = 1.0,
)

//https://en.wikipedia.org/wiki/3D_projection
//т.н. *weak* perspective projection
fun project(camera: Camera, vertex: Vertex, scale: Double = 50.0): Offset {
    val sizeXHalf = camera.screen.width / 2
    val sizeYHalf = camera.screen.height / 2

    val px = sizeXHalf + (camera.focalLength * vertex.x) / vertex.z * scale
    val py = sizeYHalf + (camera.focalLength * vertex.y) / vertex.z * scale

    return Offset(px.toFloat(), py.toFloat())
}

//https://en.wikipedia.org/wiki/Rotation_matrix
fun rotateX(vertex: Vertex, angle: Double): Vertex {
    return Vertex(
        vertex.x,
        (cos(angle) * vertex.y - sin(angle) * vertex.z).toFloat(),
        (sin(angle) * vertex.y + cos(angle) * vertex.z).toFloat()
    )
}

fun rotateY(vertex: Vertex, angle: Double): Vertex {
    return Vertex(
        (cos(angle) * vertex.x - sin(angle) * vertex.z).toFloat(),
        vertex.y,
        (sin(angle) * vertex.x + cos(angle) * vertex.z).toFloat()
    )
}

fun rotateZ(vertex: Vertex, angle: Double): Vertex {
    return Vertex(
        (cos(angle) * vertex.x - sin(angle) * vertex.y).toFloat(),
        (sin(angle) * vertex.x + cos(angle) * vertex.y).toFloat(),
        vertex.z
    )
}

fun rotateXYZ(vertex: Vertex, x: Double, y: Double, z: Double): Vertex {
    return rotateZ(rotateY(rotateX(vertex, x), y), z)
}

fun moveZ(vertex: Vertex, amount: Float): Vertex {
    return Vertex(
        vertex.x,
        vertex.y,
        vertex.z + amount
    )
}