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

package com.codepunk.core.data.remote.webservice

import androidx.lifecycle.LiveData
import com.codepunk.core.BuildConfig
import com.codepunk.core.data.remote.entity.RemoteAuthentication
import com.codepunk.core.data.remote.entity.RemoteMessage
import com.codepunk.doofenschmirtz.borrowed.android.example.github.api.ApiResponse
import com.codepunk.doofenschmirtz.domain.model.GrantType
import retrofit2.Call

/**
 * TODO: Replace all CODEPUNK_LOCAL_CLIENT_ID/CODEPUNK_LOCAL_CLIENT_SECRET with current somehow
 */

// region Constants

/**
 * A default scope for use in auth webservice calls.
 */
private const val DEFAULT_SCOPE = "*"

// endregion Constants

/**
 * Implementation of [AuthWebservice] that allows for default arguments by wrapping another
 * instance ([base]) and passing default arguments to its methods where appropriate.
 */
class AuthWebserviceWrapper(private val base: AuthWebservice) :
    AuthWebservice {

    // region Inherited methods

    override fun authenticate(
        grantType: GrantType,
        clientId: String,
        clientSecret: String,
        username: String,
        password: String,
        scope: String
    ): LiveData<ApiResponse<RemoteAuthentication>> = base.authenticate(
        grantType,
        clientId,
        clientSecret,
        username,
        password,
        scope
    )

    override fun authenticate(
        username: String,
        password: String,
        scope: String
    ): LiveData<ApiResponse<RemoteAuthentication>> {
        return base.authenticate(
            GrantType.PASSWORD,
            BuildConfig.CODEPUNK_LOCAL_CLIENT_ID,
            BuildConfig.CODEPUNK_LOCAL_CLIENT_SECRET,
            username,
            password,
            scope
        )
    }

    override fun authenticate(
        username: String,
        password: String
    ): LiveData<ApiResponse<RemoteAuthentication>> {
        return base.authenticate(
            GrantType.PASSWORD,
            BuildConfig.CODEPUNK_LOCAL_CLIENT_ID,
            BuildConfig.CODEPUNK_LOCAL_CLIENT_SECRET,
            username,
            password,
            DEFAULT_SCOPE
        )
    }

    override fun refreshToken(
        grantType: GrantType,
        clientId: String,
        clientSecret: String,
        refreshToken: String
    ): LiveData<ApiResponse<RemoteAuthentication>> =
        base.refreshToken(grantType, clientId, clientSecret, refreshToken)

    override fun refreshToken(refreshToken: String): LiveData<ApiResponse<RemoteAuthentication>> =
        base.refreshToken(
            GrantType.REFRESH_TOKEN,
            BuildConfig.CODEPUNK_LOCAL_CLIENT_ID,
            BuildConfig.CODEPUNK_LOCAL_CLIENT_SECRET,
            refreshToken
        )

    override fun register(
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): LiveData<ApiResponse<RemoteMessage>> =
        base.register(username, email, password, passwordConfirmation)

    override fun sendActivationLink(email: String): LiveData<ApiResponse<RemoteMessage>> =
        base.sendActivationLink(email)

    override fun sendPasswordResetLink(email: String): LiveData<ApiResponse<RemoteMessage>> =
        base.sendPasswordResetLink(email)

    // endregion Inherited methods

}
