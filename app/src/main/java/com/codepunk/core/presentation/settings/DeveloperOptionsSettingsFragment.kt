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

package com.codepunk.core.presentation.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.Preference.OnPreferenceClickListener
import com.codepunk.core.BuildConfig.*
import com.codepunk.core.R
import com.codepunk.doofenschmirtz.app.AlertDialogFragment
import com.codepunk.doofenschmirtz.app.AlertDialogFragment.Companion.RESULT_POSITIVE
import com.codepunk.doofenschmirtz.auth.AuthManager
import com.codepunk.doofenschmirtz.preference.TwoTargetSwitchPreference
import com.codepunk.doofenschmirtz.inator.loginator.FormattingLoginator
import com.codepunk.doofenschmirtz.inator.makeKey
import javax.inject.Inject

// region Constants

private const val REMOTE_URL_REQUEST_CODE = 1

// endregion Constants

/**
 * A preference fragment that displays developer options preferences to the user. By default,
 * developer options are not available to the user until they unlock the developer options
 * preference and openSession themselves as a developer.
 */
class DeveloperOptionsSettingsFragment :
    AbsSettingsFragment(),
    OnPreferenceChangeListener,
    OnPreferenceClickListener,
    AlertDialogFragment.AlertDialogFragmentListener {

    // region Properties

    /**
     * The application [AuthManager].
     */
    @Inject
    lateinit var authManager: AuthManager

    /**
     * The application [FormattingLoginator].
     */
    @Inject
    lateinit var loginator: FormattingLoginator

    /**
     * The API environment preference.
     */
    private val remoteEnvironmentPreference by lazy {
        findPreference(PREF_KEY_DEV_OPTIONS_AUTH_ENVIRONMENT) as ListPreference
    }

    /**
     * The remote URL preference.
     */
    private val overrideRemoteUrlPreference by lazy {
        findPreference(PREF_KEY_DEV_OPTIONS_OVERRIDE_BASE_URL) as TwoTargetSwitchPreference
    }

    /**
     * The current [AlertDialogFragment] instance (if any) showing any authorization-related
     * messages.
     */
    private val baseUrlDialogFragment: DeveloperOptionsBaseUrlDialogFragment?
        get() = requireFragmentManager().findFragmentByTag(REMOTE_URL_DIALOG_FRAGMENT_TAG)
            as DeveloperOptionsBaseUrlDialogFragment?

    // endregion Properties

    // region Lifecycle methods

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Initializes the preference screen.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_dev_options, rootKey)
        requireActivity().title = preferenceScreen.title

        val environments = authManager.environments
        remoteEnvironmentPreference.entries = Array<CharSequence>(environments.size) { i ->
            environments[i].name
        }
        remoteEnvironmentPreference.entryValues = Array<CharSequence>(environments.size) { i ->
            environments[i].id
        }

        overrideRemoteUrlPreference.onPreferenceChangeListener = this
        overrideRemoteUrlPreference.onPreferenceClickListener = this
        onSharedPreferenceChanged(sharedPreferences, PREF_KEY_DEV_OPTIONS_BASE_URL)
    }

    // endregion Inherited methods

    // region Implemented methods

    /**
     * Listens for changes in the [overrideRemoteUrlPreference] value.
     */
    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return when (preference) {
            overrideRemoteUrlPreference -> {
                if (overrideRemoteUrlPreference.isChecked) {
                    sharedPreferences.edit()
                        .remove(PREF_KEY_DEV_OPTIONS_BASE_URL)
                        .apply()
                    true
                } else {
                    baseUrlDialogFragment
                        ?: DeveloperOptionsBaseUrlDialogFragment.showDialogFragmentForResult(
                            this,
                            REMOTE_URL_REQUEST_CODE,
                            REMOTE_URL_DIALOG_FRAGMENT_TAG
                        )
                    false
                }
            }
            else -> true
        }
    }

    /**
     * Listens for the user clicking clicking on preferences.
     */
    override fun onPreferenceClick(preference: Preference?): Boolean = when (preference) {
        overrideRemoteUrlPreference -> {
            baseUrlDialogFragment
                ?: DeveloperOptionsBaseUrlDialogFragment.showDialogFragmentForResult(
                    this,
                    REMOTE_URL_REQUEST_CODE,
                    REMOTE_URL_DIALOG_FRAGMENT_TAG
                )
            true
        }
        else -> false
    }

    /**
     * Sets summary and other values in this fragment's preferences.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREF_KEY_DEV_OPTIONS_AUTH_ENVIRONMENT -> {
                val id = sharedPreferences?.getString(key, null) ?: DEFAULT_AUTH_ENVIRONMENT
                remoteEnvironmentPreference.summary = authManager.getEnvironment(id)?.name ?: ""

                /*
                val environment =
                    sharedPreferences?.getEnvironment(key) ?: DEFAULT_REMOTE_ENVIRONMENT
                remoteEnvironmentPreference.summary = getString(environment.nameResId)
                */
            }
            PREF_KEY_DEV_OPTIONS_BASE_URL -> {
                val remoteUrl = sharedPreferences?.getString(key, null)
                overrideRemoteUrlPreference.isChecked = !remoteUrl.isNullOrBlank()
                overrideRemoteUrlPreference.summary = if (remoteUrl.isNullOrBlank())
                    getString(R.string.settings_dev_options_override_base_url_summary_default)
                else remoteUrl
            }
        }
    }

    /**
     * No op
     */
    override fun onBuildAlertDialog(
        fragment: AlertDialogFragment,
        requestCode: Int,
        builder: AlertDialog.Builder,
        savedInstanceState: Bundle?
    ) {
        // No op
    }

    /**
     * Reacts to the result of the [DeveloperOptionsBaseUrlDialogFragment].
     */
    override fun onDialogResult(
        fragment: AlertDialogFragment,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        when (requestCode) {
            REMOTE_URL_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_POSITIVE -> {
                        val baseUrl = data?.getStringExtra(
                            DeveloperOptionsBaseUrlDialogFragment.KEY_BASE_URL
                        )
                        sharedPreferences.edit()
                            .apply {
                                when {
                                    baseUrl.isNullOrBlank() -> {
                                        putBoolean(PREF_KEY_DEV_OPTIONS_OVERRIDE_BASE_URL, false)
                                        remove(PREF_KEY_DEV_OPTIONS_BASE_URL)
                                    }
                                    else -> {
                                        putBoolean(PREF_KEY_DEV_OPTIONS_OVERRIDE_BASE_URL, true)
                                        putString(PREF_KEY_DEV_OPTIONS_BASE_URL, baseUrl)
                                    }
                                }
                            }
                            .apply()
                    }
                }
            }
        }
    }

    // endregion Implemented methods

    // region Companion object

    companion object {

        // region Properties

        /**
         * The fragment tag to use for the remote URL dialog fragment.
         */
        @JvmStatic
        private val REMOTE_URL_DIALOG_FRAGMENT_TAG =
            DeveloperOptionsSettingsFragment::class.java.makeKey("REMOTE_URL_DIALOG")

        // endregion Properties

    }

    // endregion Companion object

}
