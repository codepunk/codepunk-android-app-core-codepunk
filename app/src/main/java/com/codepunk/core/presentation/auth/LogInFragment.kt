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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.codepunk.core.Loginator
import com.codepunk.core.R
import com.codepunk.core.databinding.FragmentLogInBinding
import com.codepunk.core.di.factory.ViewModelFactory
import com.codepunk.core.domain.model.OAuthToken
import com.codepunk.doofenschmirtz.borrowed.android.example.github.vo.Resource
import com.codepunk.doofenschmirtz.borrowed.android.example.github.vo.Status
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * A [Fragment] used to log into an existing account.
 */
class LogInFragment :
    Fragment(),
    OnClickListener {

    // region Properties

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
     * The binding for this fragment.
     */
    private lateinit var binding: FragmentLogInBinding

    // endregion Properties

    // region Lifecycle methods

    /**
     * Injects dependencies into this fragment.
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        authViewModel.authResource.observe(this, Observer { resource ->
            onToken(resource)
        })
    }

    /**
     * Inflates the view.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_log_in,
            container,
            false
        )
        return binding.root
    }

    /**
     * Configures view-based listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logInBtn.setOnClickListener(this)
    }

    // endregion Lifecycle methods

    // region Implemented methods

    /**
     * Responds to click events.
     */
    override fun onClick(v: View?) {
        when (v) {
            binding.logInBtn -> {
                authViewModel.authenticate(
                    binding.usernameOrEmailEdit.text.toString(),
                    binding.passwordEdit.text.toString()
                )
            }
        }
    }

    // endregion Implemented methods

    // region Methods

    private fun onToken(resource: Resource<OAuthToken>) {
        Loginator.d("resource=$resource")
        if (resource.status == Status.ERROR) {

        }
    }

    // endregion Methods

}
