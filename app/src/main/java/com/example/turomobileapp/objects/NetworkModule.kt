package com.example.turomobileapp.objects

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.turomobileapp.interfaces.AchievementsApiService
import com.example.turomobileapp.interfaces.ActivityApiService
import com.example.turomobileapp.interfaces.AssessmentResultApiService
import com.example.turomobileapp.interfaces.BadgesApiService
import com.example.turomobileapp.interfaces.CalendarApiService
import com.example.turomobileapp.interfaces.CourseApiService
import com.example.turomobileapp.interfaces.EnrollmentApiService
import com.example.turomobileapp.interfaces.LectureApiService
import com.example.turomobileapp.interfaces.MessageApiService
import com.example.turomobileapp.interfaces.ModuleApiService
import com.example.turomobileapp.interfaces.QuizApiService
import com.example.turomobileapp.interfaces.ShopItemApiService
import com.example.turomobileapp.interfaces.StudentProgressApiService
import com.example.turomobileapp.interfaces.TutorialApiService
import com.example.turomobileapp.interfaces.UserApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule{

    @Provides
    @Singleton
    @Named("BaseUrl")
    fun provideBaseUrl(): String =
        "https://turoonline.live/api/v1/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message ->
            Log.d("OKHTTP", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(Base64ByteArrayAdapter())
            .add(LocalDateTimeAdapter)
            .addLast(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("BaseUrl") baseUrl: String,
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()


    @Provides
    @Singleton
    fun provideAchievementsApiService(retrofit: Retrofit): AchievementsApiService =
        retrofit.create(AchievementsApiService::class.java)

    @Provides
    @Singleton
    fun provideActivityApiService(retrofit: Retrofit): ActivityApiService =
        retrofit.create(ActivityApiService::class.java)

    @Provides
    @Singleton
    fun provideAssessmentResultApiService(retrofit: Retrofit): AssessmentResultApiService =
        retrofit.create(AssessmentResultApiService::class.java)

    @Provides
    @Singleton
    fun provideBadgesApiService(retrofit: Retrofit): BadgesApiService =
        retrofit.create(BadgesApiService::class.java)

    @Provides
    @Singleton
    fun provideCalendarEventsApiService(retrofit: Retrofit): CalendarApiService =
        retrofit.create(CalendarApiService::class.java)

    @Provides
    @Singleton
    fun provideCourseApiService(retrofit: Retrofit): CourseApiService =
        retrofit.create(CourseApiService::class.java)

    @Provides
    @Singleton
    fun provideEnrollmentApiService(retrofit: Retrofit): EnrollmentApiService =
        retrofit.create(EnrollmentApiService::class.java)

    @Provides
    @Singleton
    fun provideLectureApiService(retrofit: Retrofit): LectureApiService =
        retrofit.create(LectureApiService::class.java)

    @Provides
    @Singleton
    fun provideMessageApiService(retrofit: Retrofit): MessageApiService =
        retrofit.create(MessageApiService::class.java)

    @Provides
    @Singleton
    fun provideModuleApiService(retrofit: Retrofit): ModuleApiService =
        retrofit.create(ModuleApiService::class.java)

    @Provides
    @Singleton
    fun provideQuizApiService(retrofit: Retrofit): QuizApiService =
        retrofit.create(QuizApiService::class.java)

    @Provides
    @Singleton
    fun provideShopItemApiService(retrofit: Retrofit): ShopItemApiService =
        retrofit.create(ShopItemApiService::class.java)

    @Provides
    @Singleton
    fun provideStudentProgressApiService(retrofit: Retrofit): StudentProgressApiService =
        retrofit.create(StudentProgressApiService::class.java)

    @Provides
    @Singleton
    fun provideTutorialApiService(retrofit: Retrofit): TutorialApiService =
        retrofit.create(TutorialApiService::class.java)

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)
}