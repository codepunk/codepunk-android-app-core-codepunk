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

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import com.codepunk.core.BuildConfig.*
import com.codepunk.core.CodepunkApp
import com.codepunk.core.R
import com.codepunk.doofenschmirtz.auth.AuthManager
import com.codepunk.doofenschmirtz.auth.AuthManager.Environment
import com.codepunk.doofenschmirtz.borrowed.android.example.github.AppExecutors
import com.codepunk.doofenschmirtz.di.qualifier.ApplicationContext
import com.codepunk.doofenschmirtz.di.qualifier.MainThreadExecutor
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

/**
 * A dagger [Module] for injecting application-level dependencies.
 */
@Suppress("UNUSED")
@Module
object AppModule {

    // region Methods

    /**
     * Provides the application-level [Context].
     */
    @JvmStatic
    @Provides
    @Singleton
    @ApplicationContext
    fun providesContext(app: CodepunkApp): Context = app

    /**
     * Provides an [AccountManager] instance for providing access to a centralized registry of
     * the user's online accounts.
     */
    @JvmStatic
    @Provides
    @Singleton
    fun providesAccountManager(@ApplicationContext context: Context): AccountManager =
        AccountManager.get(context)

    /**
     * Provides the default [SharedPreferences] for the app.
     */
    @JvmStatic
    @Provides
    @Singleton
    fun providesSharedPreferences(app: CodepunkApp): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(app)

    /**
     * Provides a [AuthManager] for managing RESTful connections.
     */
    @JvmStatic
    @Provides
    @Singleton
    fun providesAuthManager(app: CodepunkApp): AuthManager =
        AuthManager.Builder()
            .environment(
                Environment.Builder(
                    "prod",
                    app.getString(R.string.env_prod),
                    "https://codepunk.com"
                )
                    .clientId(CODEPUNK_PROD_CLIENT_ID)
                    .clientSecret(CODEPUNK_PROD_CLIENT_SECRET)
                    .build()
            )
            .environment(
                Environment.Builder(
                    "dev",
                    app.getString(R.string.env_dev),
                    "https://codepunk.com"
                )
                    .clientId(CODEPUNK_DEV_CLIENT_ID)
                    .clientSecret(CODEPUNK_DEV_CLIENT_SECRET)
                    .build()
            )
            .environment(
                Environment.Builder(
                    "local",
                    app.getString(R.string.env_local),
                    "http://192.168.12.10"
                )
                    .clientId(CODEPUNK_LOCAL_CLIENT_ID)
                    .clientSecret(CODEPUNK_LOCAL_CLIENT_SECRET)
                    .build()
            )
            .build()

    /**
     * Provides a main thread [Executor].
     */
    @JvmStatic
    @Provides
    @Singleton
    @MainThreadExecutor
    fun providesMainThreadExecutor() : Executor = object : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    /**
     * Provides an [AppExecutors] instance for global executor pools for the whole application.
     */
    @JvmStatic
    @Provides
    @Singleton
    fun providesAppExecutors(@MainThreadExecutor mainThreadExecutor: Executor): AppExecutors =
        AppExecutors(
            Executors.newSingleThreadExecutor(),
            Executors.newFixedThreadPool(3),
            mainThreadExecutor
        )

    // endregion Methods

}
