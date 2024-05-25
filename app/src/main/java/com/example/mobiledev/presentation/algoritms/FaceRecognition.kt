package com.example.mobiledev.presentation.algoritms

import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.mobiledev.R
import com.example.mobiledev.presentation.algoritms.util.generateUri
import com.example.mobiledev.presentation.algoritms.util.getTmpDirectory
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream

fun FaceRecognition(image:ByteArray?, resources: Resources): Uri?{
    if(!OpenCVLoader.initLocal()) {
        Log.d("FRECOG", "OpenCV not loaded")

    }

    // https://stackoverflow.com/questions/17390289/convert-bitmap-to-mat-after-capture-image-using-android-camera
    var bitmap = toBitmap(image)
    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true)
    val cvImg = Mat()
    Utils.bitmapToMat(bitmap, cvImg)

    val cascadeFile = getCascadeFile(resources)
    cascadeFile.deleteOnExit()

    val faces = detectFaces(cvImg, cascadeFile.absolutePath)

    if(faces != null)
    {
        for (rect in faces.toArray()) {
            Imgproc.rectangle(cvImg, rect, Scalar(0.0, 255.0, 0.0), 2)
        }

        Log.d("FRECOG", "total faces:" + faces.toArray().size.toString())
    }


    Utils.matToBitmap(cvImg, bitmap)

    return generateUri(bitmap)
}

fun detectFaces(image: Mat, cascadeClassifierPath: String): MatOfRect?{
    val grayImage = Mat()
    Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY)
    Imgproc.equalizeHist(grayImage, grayImage)

    val faceCascade = CascadeClassifier(cascadeClassifierPath)

    if (faceCascade.empty()) {
        Log.d("FRECOG", "Failed to load cascade classifier")
        return null
    }

    val faces = MatOfRect()
    faceCascade.detectMultiScale(grayImage, faces)

    return faces
}

fun getCascadeFile(resources: Resources): File
{
    val file = File.createTempFile("cascade", ".xml", getTmpDirectory())

    resources.openRawResource(R.raw.haarcascade_frontalface_default).bufferedReader().use {
        val ostream = FileOutputStream(file)
        ostream.write(it.readText().toByteArray())
        ostream.close()
    }

    return file
}