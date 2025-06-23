package com.example.turomobileapp.objects

import com.example.turomobileapp.viewmodels.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val session: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var req = chain.request()
        session.accessToken.value?.let {
            req = req.newBuilder()
                .addHeader("Authorization", "Bearer $it")
                .build()
        }

        val res = chain.proceed(req)

        if (res.code == 401) {
            session.clearSession()
        }
        return res
    }
}