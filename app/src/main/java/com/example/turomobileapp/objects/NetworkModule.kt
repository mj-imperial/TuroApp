package com.example.turomobileapp.objects

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
import com.example.turomobileapp.interfaces.ModuleProgressApiService
import com.example.turomobileapp.interfaces.OptionsApiService
import com.example.turomobileapp.interfaces.QuestionApiService
import com.example.turomobileapp.interfaces.QuizApiService
import com.example.turomobileapp.interfaces.ScreeningExamApiService
import com.example.turomobileapp.interfaces.StudentProgressApiService
import com.example.turomobileapp.interfaces.TutorialApiService
import com.example.turomobileapp.interfaces.UserApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule{

    @Provides
    @Singleton
    fun provideBaseUrl(): String = "CHANGE DEPENDING ON DATABASE LOCATION"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideTutorialApiService(retrofit: Retrofit): TutorialApiService =
        retrofit.create(TutorialApiService::class.java)

    @Provides
    @Singleton
    fun provideStudentProgressApiService(retrofit: Retrofit): StudentProgressApiService =
        retrofit.create(StudentProgressApiService::class.java)

    @Provides
    @Singleton
    fun provideScreeningExamApiService(retrofit: Retrofit): ScreeningExamApiService =
        retrofit.create(ScreeningExamApiService::class.java)

    @Provides
    @Singleton
    fun provideQuizApiService(retrofit: Retrofit): QuizApiService =
        retrofit.create(QuizApiService::class.java)

    @Provides
    @Singleton
    fun provideQuestionApiService(retrofit: Retrofit): QuestionApiService =
        retrofit.create(QuestionApiService::class.java)

    @Provides
    @Singleton
    fun provideOptionsApiService(retrofit: Retrofit): OptionsApiService =
        retrofit.create(OptionsApiService::class.java)

    @Provides
    @Singleton
    fun provideModuleProgressApiService(retrofit: Retrofit): ModuleProgressApiService =
        retrofit.create(ModuleProgressApiService::class.java)

    @Provides
    @Singleton
    fun provideModuleApiService(retrofit: Retrofit): ModuleApiService =
        retrofit.create(ModuleApiService::class.java)

    @Provides
    @Singleton
    fun provideLectureApiService(retrofit: Retrofit): LectureApiService =
        retrofit.create(LectureApiService::class.java)

    @Provides
    @Singleton
    fun provideEnrollmentApiService(retrofit: Retrofit): EnrollmentApiService =
        retrofit.create(EnrollmentApiService::class.java)

    @Provides
    @Singleton
    fun provideCourseApiService(retrofit: Retrofit): CourseApiService =
        retrofit.create(CourseApiService::class.java)

    @Provides
    @Singleton
    fun provideBadgesApiService(retrofit: Retrofit): BadgesApiService =
        retrofit.create(BadgesApiService::class.java)

    @Provides
    @Singleton
    fun provideAssessmentResultApiService(retrofit: Retrofit): AssessmentResultApiService =
        retrofit.create(AssessmentResultApiService::class.java)

    @Provides
    @Singleton
    fun provideActivityApiService(retrofit: Retrofit): ActivityApiService =
        retrofit.create(ActivityApiService::class.java)

    @Provides
    @Singleton
    fun provideAchievementsApiService(retrofit: Retrofit): AchievementsApiService =
        retrofit.create(AchievementsApiService::class.java)

    @Provides
    @Singleton
    fun provideCalendarEventsApiService(retrofit: Retrofit): CalendarApiService =
        retrofit.create(CalendarApiService::class.java)

    @Provides
    @Singleton
    fun provideMessageApiService(retrofit: Retrofit): MessageApiService =
        retrofit.create(MessageApiService::class.java)
}