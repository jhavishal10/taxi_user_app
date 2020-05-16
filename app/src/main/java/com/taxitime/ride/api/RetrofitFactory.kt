package com.taxitime.ride.api

import com.taxitime.ride.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class RetrofitFactory(baseUrl: String, private var defaultTimeOut: Long = 60) {

    private val apiLogLevel =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE

    private var retrofit: Lazy<Retrofit> = lazy {
        createRetrofitInstance(baseUrl)
    }

    private val apiMap: MutableMap<String, Any> = mutableMapOf()

    private fun createRetrofitInstance(baseUrl: String): Retrofit {
        val httpClientBuilder = getOkHttpClientBuilder()

        val retroBuilder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClientBuilder.build())

        getConverterFactory().forEach {
            retroBuilder.addConverterFactory(it)
        }
        return retroBuilder.build()
    }

    /**
     * Provide the OkHttpBuilder with all the basic logging. Override to change the default builder or add any property.
     * @return OkHttpBuilder to be passed in retrofit builder
     */
    open fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.level = apiLogLevel

        return OkHttpClient.Builder()
                .addInterceptor(getAuthenticationInterceptor())
                .addInterceptor(logging)
                .connectTimeout(defaultTimeOut, TimeUnit.SECONDS)
                .readTimeout(defaultTimeOut, TimeUnit.SECONDS)
                .writeTimeout(defaultTimeOut, TimeUnit.SECONDS)
    }

    open fun getAuthenticationInterceptor(): AuthenticationInterceptor {
        return AuthenticationInterceptor(BaseApplication.tokenManager)
    }

    /**
     * Provide converter factory. And override to provide converter factory according to App requirements.
     *
     * @return Converter Factory
     */
    open fun getConverterFactory(): List<Converter.Factory> {

        return listOf(getMoshiConvertorFactory(), GsonConverterFactory.create())
    }

    fun getMoshiConvertorFactory(): Converter.Factory {
        return MoshiMigrationConverter(MoshiConverterFactory.create(MoshiInstance.instance))
    }

    /**
     * Get the API Service instance for given class
     *
     * @param apiClass API interface class
     * @return api object reference to call Retrofit mode APIs
     */
    fun <T> getApiService(apiClass: Class<T>): Any? {
        var api: Any? = apiMap[apiClass.name]
        if (api == null) {
            api = retrofit.value.create(apiClass)
            apiMap[apiClass.name] = api!!
        }
        return api
    }

    /**
     * Add all the common headers which needs to be sent in API requests
     * Eg. - App Version, Source, device id, time zone..etc
     */
    open fun addCommonHeaders(builder: Request.Builder) {
        builder.addHeader("PLATFORM", "Android")
    }
}