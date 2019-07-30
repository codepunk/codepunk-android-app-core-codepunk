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
import com.codepunk.core.BuildConfig.DEFAULT_AUTH_ENVIRONMENT
import com.codepunk.doofenschmirtz.di.qualifier.ApplicationContext
import com.codepunk.doofenschmirtz.moshi.adapter.BooleanIntAdapter
import com.codepunk.doofenschmirtz.moshi.adapter.DateJsonAdapter
import com.codepunk.doofenschmirtz.moshi.converter.MoshiEnumConverterFactory
import com.codepunk.doofenschmirtz.net.AuthManager
import com.codepunk.doofenschmirtz.retrofit.interceptor.AuthorizationInterceptor
import com.codepunk.doofenschmirtz.retrofit.interceptor.UrlOverrideInterceptor
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
@Suppress("UNUSED")
@Module
class NetModule {

    // region Methods

    /**
     * Provides an instance of [Cache] for dependency injection.
     */
    @Provides
    @Singleton
    fun providesCache(@ApplicationContext context: Context): Cache =
        Cache(context.cacheDir, CACHE_SIZE)

    /**
     * Provides an instance of [OkHttpClient] for dependency injection.
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
     * Provides an instance of [Moshi] for dependency injection.
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
     * Provides an instance of [MoshiConverterFactory] for dependency injection.
     */
    @Provides
    @Singleton
    fun providesConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    /**
     * Provides an instance of [Retrofit.Builder>. This is used primarily to test base URL
     * overrides entered by the user in Developer Options settings.
     */
    @Provides
    @Singleton
    fun providesRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()

    /**
     * Provides an instance of [Retrofit] for dependency injection.
     */
    @Provides
    @Singleton
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
        moshiEnumConverterFactory: MoshiEnumConverterFactory,
        authManager: AuthManager
    ): Retrofit {
        val baseUrl = authManager.getEnvironment(DEFAULT_AUTH_ENVIRONMENT)?.baseUrl ?: ""
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .addConverterFactory(moshiEnumConverterFactory)
            .build()
    }

    /* TODO
    /**
     * Provides an instance of [AuthWebservice] for making authentication API calls.
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
    */

    // endregion Methods

}
