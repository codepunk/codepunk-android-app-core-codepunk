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

package com.codepunk.core.presentation.auth

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.codepunk.core.BuildConfig
import com.codepunk.core.BuildConfig.CATEGORY_CHOOSE
import com.codepunk.core.BuildConfig.CATEGORY_LOG_IN
import com.codepunk.core.BuildConfig.CATEGORY_SIGN_UP
import com.codepunk.core.R
import com.codepunk.core.databinding.ActivityAuthBinding
import com.codepunk.core.viewmodel.ViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * An Activity that handles all actions relating to creating, selecting, and authenticating
 * an account.
 */
class AuthActivity : AppCompatActivity() {

    // region Properties

    /**
     * The Android account manager.
     */
    @Inject
    lateinit var accountManager: AccountManager

    /**
     * The injected [ViewModelProvider.Factory] that we will use to get an instance of
     * [AuthViewModel].
     */
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AuthViewModel>

    /**
     * The [AuthViewModel] instance backing this fragment.
     */
    private val authViewModel: AuthViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AuthViewModel::class.java)
    }

    /**
     * The binding for this activity.
     */
    private lateinit var binding: ActivityAuthBinding

    /**
     * The [NavController] for the activity.
     */
    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.auth_nav_fragment)
    }

    /**
     * The app bar configuration for the activity.
     */
    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(navController.graph)
    }

    /**
     * The activity's [ContentLoadingProgressBar].
     */
    @Suppress("WEAKER_ACCESS")
    lateinit var loadingProgressBar: ContentLoadingProgressBar
        private set

    // endregion Properties

    // region Lifecycle methods

    /**
     * Creates the content view and pulls values from the passed [Intent].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        loadingProgressBar = binding.loadingProgress
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        /* TODO
        authViewModel.authLiveResource.observe(this, Observer { onAuthResource(it) })
        authViewModel.registerLiveResource.observe(this, Observer { onResource(it) })
        authViewModel.sendActivationLiveResource.observe(this, Observer { onResource(it) })
        authViewModel.sendPasswordResetLiveResource.observe(this, Observer { onResource(it) })
        */

        if (savedInstanceState == null) {
            // If the supplied intent specifies it, navigate to an alternate destination
            // and pop up inclusive to that new destination
            intent.categories?.apply {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.fragment_auth, true)
                    .build()
                when {
                    contains(BuildConfig.CATEGORY_CHOOSE) -> resolveIntent(intent, true)
                    contains(CATEGORY_SIGN_UP) -> navController.navigate(
                        R.id.action_auth_to_sign_up,
                        intent.extras,
                        navOptions
                    )
                    contains(BuildConfig.CATEGORY_LOG_IN) -> navController.navigate(
                        R.id.action_auth_to_log_in,
                        intent.extras,
                        navOptions
                    )
                }
            }
        }
    }

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Reacts to a new intent.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.categories?.apply {
            when {
                contains(CATEGORY_CHOOSE) -> resolveIntent(intent)
                contains(CATEGORY_SIGN_UP) ->
                    navController.navigate(R.id.action_auth_to_sign_up, intent.extras)
                contains(CATEGORY_LOG_IN) ->
                    navController.navigate(R.id.action_auth_to_log_in, intent.extras)
            }
        }
    }

    /**
     * Reacts to the "up" button being pressed.
     */
    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()

    // endregion Inherited methods

    // region Methods

    private fun resolveIntent(intent: Intent, popUp: Boolean = false) {
        intent.categories?.apply {
            var action: Int = -1
            val navOptions = NavOptions.Builder().apply {
                if (popUp) {
                    setPopUpTo(R.id.fragment_auth, true)
                }
            }.build()
            when {
                contains(CATEGORY_CHOOSE) -> {

                    // TODO Clean up
                    // TODO If there's only one account and it's the current one,
                    // then change action to R.id.action_auth_to_register

                    // 2) Get all saved accounts for type AUTHENTICATOR_ACCOUNT_TYPE
                    val type: String = BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE
                    val accounts = accountManager.getAccountsByType(type)

                    // 3) Get the "current" account. The current account is either the account whose name has
                    // been saved in shared preferences, or the sole account for the given type if only
                    // one account has been stored via the account manager
                    if (accounts.isEmpty()) {
                        action = R.id.action_auth_to_sign_up
                    }

                    if (action > 0) {
                        navController.navigate(action, intent.extras, navOptions)
                    }
                }
            }
        }
    }

    // endregion Methods

}
