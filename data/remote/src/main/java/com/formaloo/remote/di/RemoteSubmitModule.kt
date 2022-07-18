package com.formaloo.remote.di


import com.formaloo.remote.submit.FormSubmitService
import com.formaloo.remote.submit.SubmitDatasource
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun createRemoteSubmitModule(
    url: String,
    xapikey: String
) = module {

    single(named(remoteSubmitModulConstant.InterceptorName)) {
        Interceptor { chain ->
            val original = chain.request()

            val request =
                original.newBuilder()
                    .header("x-api-key", xapikey)

                    .method(original.method, original.body)
                    .build()

            chain.proceed(request)
        }
    }
//

    single(named(remoteSubmitModulConstant.ClientName)) {

        OkHttpClient.Builder()
            .addInterceptor(get(named(remoteSubmitModulConstant.InterceptorName)) as Interceptor)
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .build()
    }

    single(named(remoteSubmitModulConstant.RetrofitName)) {

        Retrofit.Builder()
            .client(get(named(remoteSubmitModulConstant.ClientName)))
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    single(named(remoteSubmitModulConstant.ServiceName)) {
        get<Retrofit>(named(remoteSubmitModulConstant.RetrofitName)).create(
            FormSubmitService::class.java
        )
    }

    single(named(remoteSubmitModulConstant.DataSourceName)) {
        SubmitDatasource(
            get(
                named(
                    remoteSubmitModulConstant.ServiceName
                )
            )
        )
    }
}


object remoteSubmitModulConstant {
    const val DataSourceName = "SubmitDatasource"
    const val ServiceName = "SubmitService"
    const val ClientName = "SubmitClient"
    const val RetrofitName = "SubmitRetrofit"
    const val InterceptorName = "SubmitInterceptor"
}


