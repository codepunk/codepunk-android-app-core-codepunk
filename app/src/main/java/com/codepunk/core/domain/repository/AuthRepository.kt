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

package com.codepunk.core.domain.repository

import androidx.lifecycle.LiveData
import com.codepunk.core.domain.model.NetworkMessage
import com.codepunk.core.domain.model.OAuthToken
import com.codepunk.doofenschmirtz.borrowed.android.example.github.vo.Resource

/**
 * A data repository that defines auth-related methods.
 */
interface AuthRepository {

    // region Methods

    /**
     * Authenticates a user account using a [usernameOrEmail] and a [password].
     */
    fun authenticate(
        usernameOrEmail: String,
        password: String
    ): LiveData<Resource<OAuthToken>>

    /**
     * Registers a new user account using a [username], [email], [password] and
     * [passwordConfirmation].
     */
    fun register(
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): LiveData<Resource<NetworkMessage>>

    /**
     * Sends an activation link to the supplied [email].
     */
    fun sendActivationLink(
        email: String
    ): LiveData<Resource<NetworkMessage>>

    /**
     * Sends a password reset link to the supplied [email].
     */
    fun sendPasswordResetLink(
        email: String
    ): LiveData<Resource<NetworkMessage>>

    // endregion Methods

}
