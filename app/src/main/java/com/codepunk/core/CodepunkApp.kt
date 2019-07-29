/*
 * Copyright (C) 2019 Codepunk, LLC
 * Author(s): Scott Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepunk.core

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.preference.PreferenceManager
import com.codepunk.core.BuildConfig.PREF_KEY_DEV_OPTIONS_AUTH_ENVIRONMENT
import com.codepunk.core.BuildConfig.PREF_KEY_DEV_OPTIONS_BASE_URL
import com.codepunk.core.di.component.DaggerAppComponent
import com.codepunk.doofenschmirtz.util.loginator.FormattingLoginator
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import javax.inject.Inject

/*
 * TODO NEXT: UrlOverrideInterceptor
 */

/**
 * The Codepunk [Application] class.
 */
class CodepunkApp :
    Application(),
    HasActivityInjector,
    HasServiceInjector,
    OnSharedPreferenceChangeListener {

    // region Properties

    /**
     * Performs dependency injection on activities.
     */
    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    /**
     * Performs dependency injection on services.
     */
    @Inject
    lateinit var serviceDispatchingAndroidInjector: DispatchingAndroidInjector<Service>

    /**
     * The application [SharedPreferences]
     */
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    /*
    /**
     * The application [Retrofit] instance. TODO
     */
    @Inject
    lateinit var retrofit: Retrofit

    /**
     * The host selection interceptor for overriding the retrofit base URL. TODO
     */
    @Inject
    lateinit var urlOverrideInterceptor: UrlOverrideInterceptor
    */

    /**
     * A [FormattingLoginator] for logging system events.
     */
    @Inject
    lateinit var loginator: FormattingLoginator

    // endregion Properties

    // region Lifecycle methods

    /**
     * Sets up the application for dependency injection and establishes the environment
     * for API calls.
     */
    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .create(this)
            .inject(this)

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        PreferenceManager.setDefaultValues(this, R.xml.settings_main, false)
        PreferenceManager.setDefaultValues(this, R.xml.settings_developer_options, false)
        onSharedPreferenceChanged(sharedPreferences, PREF_KEY_DEV_OPTIONS_BASE_URL)
    }

    /**
     * Performs cleanup. Not totally necessary since the app is done when the Application object
     * is destroyed, but is it included here for completeness.
     */
    override fun onTerminate() {
        super.onTerminate()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    // endregion Lifecycle methods

    // region Implemented methods

    /**
     * Implementation of [HasActivityInjector]. Returns a [DispatchingAndroidInjector] that injects
     * dependencies into activities.
     */
    override fun activityInjector(): AndroidInjector<Activity> = activityDispatchingAndroidInjector

    /**
     * Implementation of [HasServiceInjector]. Returns a [DispatchingAndroidInjector] that injects
     * dependencies into services.
     */
    override fun serviceInjector(): AndroidInjector<Service> = serviceDispatchingAndroidInjector

    /**
     * Implementation of [OnSharedPreferenceChangeListener]. Updates URL override interceptor
     * if necessary.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREF_KEY_DEV_OPTIONS_AUTH_ENVIRONMENT -> {
                // No op? TODO
            }
            PREF_KEY_DEV_OPTIONS_BASE_URL -> {
                /* TODO
                getString(key, null)?.also { newValue ->
                    val oldValue = retrofit.baseUrl().toString()
                    urlOverrideInterceptor.override(oldValue, newValue)
                } ?: urlOverrideInterceptor.clear()
                */
            }
        }
    }

    // endregion Implemented methods

}
