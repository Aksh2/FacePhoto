package com.learn.facepoto.di.module

import android.content.Context
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.learn.facepoto.repository.ImageRepository
import com.learn.facepoto.repository.ImageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFaceDetector(): FaceDetector {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .build()
        return FaceDetection.getClient(options)
    }



    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context,
    ): ImageRepository {
        return ImageRepositoryImpl(context)
    }

}
