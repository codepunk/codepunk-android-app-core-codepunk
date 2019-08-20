/*
 * Copyright (C) 2019 Codepunk, LLC
 * Author(s): Scott Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepunk.core.di.module

import android.content.Context
import android.content.SharedPreferences
import com.codepunk.core.BuildConfig.DEFAULT_AUTH_ENVIRONMENT
import com.codepunk.core.data.local.dao.UserDao
import com.codepunk.core.data.remote.webservice.AuthWebservice
import com.codepunk.core.data.remote.webservice.AuthWebserviceWrapper
import com.codepunk.core.data.remote.webservice.UserWebservice
import com.codepunk.core.data.repository.AuthRepositoryImpl
import com.codepunk.core.domain.repository.AuthRepository
import com.codepunk.doofenschmirtz.auth.AuthManager
import com.codepunk.doofenschmirtz.borrowed.android.example.github.AppExecutors
import com.codepunk.doofenschmirtz.borrowed.android.example.github.util.LiveDataCallAdapterFactory
import com.codepunk.doofenschmirtz.data.remote.moshi.adapter.BooleanIntAdapter
import com.codepunk.doofenschmirtz.data.remote.moshi.adapter.DateJsonAdapter
import com.codepunk.doofenschmirtz.data.remote.moshi.converter.MoshiEnumConverterFactory
import com.codepunk.doofenschmirtz.data.remote.retrofit.interceptor.AuthorizationInterceptor
import com.codepunk.doofenschmirtz.data.remote.retrofit.interceptor.UrlOverrideInterceptor
import com.codepunk.doofenschmirtz.di.qualifier.ApplicationContext
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Singleton

// region Constants

/**
 * The size of the [OkHttpClient] cache.
 */
private const val CACHE_SIZE: Long = 10 * 1024 * 1024

// endregion Constants

/**
 * A [Module] that provides network-specific instances for dependency injection.
 */
@Module
class NetModule {

    // region Methods

    /**
     * Provides an instance of [Cache].
     */
    @Provides
    @Singleton
    fun providesCache(@ApplicationContext context: Context): Cache =
        Cache(context.cacheDir, CACHE_SIZE)

    /**
     * Provides an instance of [OkHttpClient].
     */
    @Provides
    @Singleton
    fun providesOkHttpClient(
        cache: Cache,
        urlOverrideInterceptor: UrlOverrideInterceptor,
        authorizationInterceptor: AuthorizationInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(urlOverrideInterceptor)
        .addInterceptor(authorizationInterceptor)
        .build()

    /**
     * Provides an instance of [BooleanIntAdapter].
     */
    @Provides
    @Singleton
    fun providesBooleanIntAdapter() =
        BooleanIntAdapter()

    /**
     * Provides an instance of [DateJsonAdapter].
     */
    @Provides
    @Singleton
    fun providesDateJsonAdapter() =
        DateJsonAdapter()

    /**
     * Provides an instance of [MoshiEnumConverterFactory].
     */
    @Provides
    @Singleton
    fun providesMoshiEnumConverterFactory() =
        MoshiEnumConverterFactory()

    /**
     * Provides an instance of [Moshi].
     */
    @Provides
    @Singleton
    fun providesMoshi(
        booleanIntAdapter: BooleanIntAdapter,
        dateJsonAdapter: DateJsonAdapter
    ): Moshi = Moshi.Builder()
        .add(booleanIntAdapter)
        .add(Date::class.java, dateJsonAdapter)
        .build()

    /**
     * Provides an instance of [LiveDataCallAdapterFactory].
     */
    @Provides
    @Singleton
    fun providesLiveDataCallAdapterFactory(): LiveDataCallAdapterFactory =
        LiveDataCallAdapterFactory()

    /**
     * Provides an instance of [MoshiConverterFactory].
     */
    @Provides
    @Singleton
    fun providesConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    /**
     * Provides an instance of [Retrofit.Builder]. This is used primarily to test base URL
     * overrides entered by the user in Developer Options settings.
     */
    @Provides
    @Singleton
    fun providesRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()

    /**
     * Provides an instance of [AuthorizationInterceptor].
     */
    @Provides
    @Singleton
    fun providesAuthorizationInterceptor() =
        AuthorizationInterceptor()

    /**
     * Provides an instance of [UrlOverrideInterceptor].
     */
    @Provides
    @Singleton
    fun providesUrlOverrideInterceptor() =
        UrlOverrideInterceptor()

    /**
     * Provides an instance of [Retrofit].
     */
    @Provides
    @Singleton
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        liveDataCallAdapterFactory: LiveDataCallAdapterFactory,
        moshiConverterFactory: MoshiConverterFactory,
        moshiEnumConverterFactory: MoshiEnumConverterFactory,
        authManager: AuthManager
    ): Retrofit {
        val baseUrl = authManager.getEnvironment(DEFAULT_AUTH_ENVIRONMENT)?.baseUrl ?: ""
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addCallAdapterFactory(liveDataCallAdapterFactory)
            .addConverterFactory(moshiConverterFactory)
            .addConverterFactory(moshiEnumConverterFactory)
            .build()
    }

    /**
     * Provides an instance of [AuthWebservice] for making token API calls.
     */
    @Provides
    @Singleton
    fun providesAuthWebservice(
        retrofit: Retrofit
    ): AuthWebservice = AuthWebserviceWrapper(retrofit.create(AuthWebservice::class.java))

    /**
     * Provides an instance of [UserWebservice] for making user API calls.
     */
    @Provides
    @Singleton
    fun providesUserWebservice(
        retrofit: Retrofit
    ): UserWebservice = retrofit.create(UserWebservice::class.java)

    /**
     * Provides an instance of [AuthRepository].
     */
    @Provides
    @Singleton
    fun providesAuthRepository(
        appExecutors: AppExecutors,
        sharedPreferences: SharedPreferences,
        retrofit: Retrofit,
        authWebservice: AuthWebservice,
        userWebservice: UserWebservice,
        userDao: UserDao
    ): AuthRepository = AuthRepositoryImpl(
        appExecutors,
        sharedPreferences,
        retrofit,
        authWebservice,
        userWebservice,
        userDao
    )

    // endregion Methods

}
