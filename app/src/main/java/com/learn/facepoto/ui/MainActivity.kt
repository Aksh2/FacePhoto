package com.learn.facepoto.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.face.FaceDetector
import com.learn.facepoto.viewmodel.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: ImageViewModel by viewModels()

    @Inject
    lateinit var faceDetector: FaceDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageGridView(
                viewModel.fetchPagedImages().flow
            )

        }

    }


}


/*private fun loadImages(){
    viewModel.humanFaceLiveData.observe(this) { state ->
        when(state) {
            is ImageState.Loading -> {

            }

            is ImageState.Success -> {
                state.faces.forEach{
                    face: Face ->
                    face.boundingBox
                }
            }

            is ImageState.Error ->{

            }
        }


    }
}*/

/*override fun onFacesDetected(faces: List<Face>) {
    for (face in faces) {
        val boundingBox = face.boundingBox

        // Create a Paint object for the bounding box
        val paint = androidx.compose.ui.graphics.Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        // Draw the bounding box on your image view's canvas
        val canvas = yourImageView.holder.lockCanvas()
        canvas?.drawRect(boundingBox, paint)
        yourImageView.holder.unlockCanvasAndPost(canvas)
    }
}*/

/*override fun onFaceDetectionError(e: Exception) {
    TODO("Not yet implemented")
}*/


