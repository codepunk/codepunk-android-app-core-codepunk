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

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.codepunk.core.BuildConfig
import com.codepunk.core.R
import com.codepunk.core.databinding.DialogDeveloperOptionsRemoteUrlBinding
import com.codepunk.doofenschmirtz.app.AlertDialogFragment
import com.codepunk.doofenschmirtz.inator.makeKey
import com.google.android.material.textfield.TextInputLayout
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * An [AlertDialogFragment] that allows a user with developer credentials to change the base URL
 * for making RESTful calls.
 */
class DeveloperOptionsBaseUrlDialogFragment :
    AlertDialogFragment(),
    DialogInterface.OnShowListener,
    View.OnClickListener {

    // region Properties

    /**
     * The application [SharedPreferences].
     */
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    /**
     * A ([Lazy]) implementation of [Retrofit.Builder] that tests for a valid URL entered by the
     * user.
     */
    @Inject
    lateinit var urlTester: Lazy<Retrofit.Builder>

    /**
     * The binding for this fragment.
     */
    private lateinit var binding: DialogDeveloperOptionsRemoteUrlBinding

    /**
     * The (optional) [Button] that represents a positive response by the user.
     */
    private val positiveBtn by lazy {
        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
    }

    // endregion Properties

    // region Lifecycle methods

    /**
     * Injects dependencies into this fragment.
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    // endregion Lifecycle methods

    // region Inherited methods

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also { dialog ->
            dialog.setOnShowListener(this)
        }
    }

    override fun newBuilder(savedInstanceState: Bundle?): AlertDialog.Builder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.dialog_developer_options_remote_url,
            null,
            false
        )

        binding.layout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
        binding.edit.setText(
            sharedPreferences.getString(
                BuildConfig.PREF_KEY_DEV_OPTIONS_BASE_URL,
                null
            )
        )

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_dev_options_password_dialog_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok, this)
            .setNegativeButton(android.R.string.cancel, this)
    }

    // endregion Inherited methods

    // region Implemented methods

    /**
     * Implementation of [DialogInterface.OnShowListener]. Sets the positive button's
     * OnClickListener when the dialog is shown so we can perform custom logic (i.e. check the
     * password entered by the user)
     */
    override fun onShow(dialog: DialogInterface?) {
        positiveBtn.setOnClickListener(this)
    }

    /**
     * Sends the entered base URL (if any) back to the calling activity or fragment.
     */
    override fun onClick(v: View?) {
        when (v) {
            positiveBtn -> {
                val baseUrl = binding.edit.text.toString()
                if (baseUrl.isEmpty()) {
                    resultCode = RESULT_POSITIVE
                    data = null
                    dialog?.dismiss()
                } else {
                    // Test against Retrofit to make sure it passes baseUrl
                    try {
                        urlTester.get().baseUrl(baseUrl)
                        resultCode = RESULT_POSITIVE
                        data = Intent().apply {
                            putExtra(KEY_BASE_URL, baseUrl)
                        }
                        dialog?.dismiss()
                    } catch (e: IllegalArgumentException) {
                        binding.layout.error = e.localizedMessage
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
         * A bundle key for passing the new base URL back to the calling activity or fragment.
         */
        @JvmStatic
        val KEY_BASE_URL =
            DeveloperOptionsBaseUrlDialogFragment::class.makeKey("BASE_URL")

        // endregion Properties

        // region Methods

        /**
         * Shows an [DeveloperOptionsBaseUrlDialogFragment] for which you would like a result when
         * it dismisses (for whatever reason). If it implements
         * [AlertDialogFragment.AlertDialogFragmentListener], then [targetFragment] will
         * automatically be set as the result listener.
         */
        @JvmStatic
        fun showDialogFragmentForResult(
            targetFragment: Fragment,
            requestCode: Int,
            tag: String
        ) = DeveloperOptionsBaseUrlDialogFragment().apply {
            setTargetFragment(targetFragment, requestCode)
            listenerIdentity = ListenerIdentity.TARGET_FRAGMENT
            show(targetFragment.requireFragmentManager(), tag)
        }

        /**
         * Shows an [DeveloperOptionsBaseUrlDialogFragment] for which you would like a result when
         * it dismisses (for whatever reason). If it implements
         * [AlertDialogFragment.AlertDialogFragmentListener], then [activity] will automatically be
         * set as the result listener.
         */
        @JvmStatic
        fun showDialogFragmentForResult(
            activity: FragmentActivity,
            requestCode: Int,
            tag: String
        ) = DeveloperOptionsBaseUrlDialogFragment().apply {
            this.requestCode = requestCode
            listenerIdentity = ListenerIdentity.ACTIVITY
            show(activity.supportFragmentManager, tag)
        }

        /**
         * Shows an [DeveloperOptionsBaseUrlDialogFragment] for which you would like a result when
         * it dismisses (for whatever reason). This version of [showDialogFragmentForResult]
         * specifically supplies a [listener] with the caveat that, after configuration change, a
         * handle to this fragment must be obtained again (i.e. via
         * [FragmentManager.findFragmentByTag], etc.) and the listener must manually be re-set.
         */
        @Suppress("UNUSED")
        @JvmStatic
        fun showDialogFragmentForResult(
            fragmentManager: FragmentManager,
            requestCode: Int,
            tag: String,
            listener: AlertDialogFragmentListener? = null
        ) = DeveloperOptionsBaseUrlDialogFragment().apply {
            this.requestCode = requestCode
            this.listener = listener
            show(fragmentManager, tag)
        }

        // endregion Methods

    }

    // endregion Companion object

}
