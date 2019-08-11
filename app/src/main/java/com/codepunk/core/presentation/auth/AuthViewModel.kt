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

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.codepunk.core.CodepunkApp
import com.codepunk.core.domain.model.OAuthToken
import com.codepunk.core.domain.repository.AuthRepository
import com.codepunk.doofenschmirtz.borrowed.android.example.github.vo.Resource
import javax.inject.Inject

/**
 * A [ViewModel] for managing authorization-related data.
 */
class AuthViewModel @Inject constructor(

    app: CodepunkApp,

    private val authRepository: AuthRepository

) : AndroidViewModel(app) {

    // region Properties

    /**
     * A [LiveData] holding the [OAuthToken] relating to the current authentication attempt.
     */
    val authResource = MediatorLiveData<Resource<OAuthToken>>()

    /**
     * A private [LiveData] holding the current source supplying the value to [authResource].
     */
    private var authSource: LiveData<Resource<OAuthToken>>? = null
        private set(value) {
            if (field != value) {
                field?.also { source -> authResource.removeSource(source) }
                field = value?.apply {
                    authResource.addSource(this) { value ->
                        authResource.value = value
                    }
                }
            }
        }

    // endregion Properties

    // region Methods

    /**
     * Authenticates using username (or email) and password.
     */
    fun authenticate(usernameOrEmail: String, password: String) {
        authSource = authRepository.authenticate(usernameOrEmail, password)
    }

    // endregion Methods

}
