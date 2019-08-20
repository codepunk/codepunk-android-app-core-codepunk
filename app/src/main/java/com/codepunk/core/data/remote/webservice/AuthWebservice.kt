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
import com.codepunk.core.data.remote.entity.RemoteOAuthToken
import com.codepunk.core.data.remote.entity.RemoteNetworkMessage
import com.codepunk.doofenschmirtz.borrowed.android.example.github.api.ApiResponse
import com.codepunk.doofenschmirtz.data.remote.HEADER_ACCEPT_APPLICATION_JSON
import com.codepunk.doofenschmirtz.domain.model.GrantType
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Webservice that defines token-related calls.
 */
@Suppress("UNUSED")
interface AuthWebservice {

    // region Methods

    /**
     * Gets an token token.
     */
    @POST("oauth/token")
    @FormUrlEncoded
    @Headers(HEADER_ACCEPT_APPLICATION_JSON)
    fun authenticate(
        @Field("grant_type")
        grantType: GrantType,

        @Field("client_id")
        clientId: String,

        @Field("client_secret")
        clientSecret: String,

        @Field("username")
        username: String,

        @Field("password")
        password: String,

        @Field("scope")
        scope: String
    ): LiveData<ApiResponse<RemoteOAuthToken>>

    /**
     * Gets an token token using default values.
     */
    fun authenticate(
        username: String,
        password: String,
        scope: String
    ): LiveData<ApiResponse<RemoteOAuthToken>>

    /**
     * Gets an token token using default values.
     */
    fun authenticate(
        username: String,
        password: String
    ): LiveData<ApiResponse<RemoteOAuthToken>>

    /**
     * Gets an token token from an existing [refreshToken].
     */
    @POST("oauth/token")
    @FormUrlEncoded
    @Headers(HEADER_ACCEPT_APPLICATION_JSON)
    fun refreshToken(
        @Field("grant_type")
        grantType: GrantType,

        @Field("client_id")
        clientId: String,

        @Field("client_secret")
        clientSecret: String,

        @Field("refresh_token")
        refreshToken: String
    ): LiveData<ApiResponse<RemoteOAuthToken>>

    /**
     * Gets an token token from an existing [refreshToken].
     */
    fun refreshToken(refreshToken: String): LiveData<ApiResponse<RemoteOAuthToken>>

    /**
     * Registers a new account.
     */
    @POST("register")
    @FormUrlEncoded
    @Headers(HEADER_ACCEPT_APPLICATION_JSON)
    fun register(
        @Field("username")
        username: String,

        @Field("email")
        email: String,

        @Field("password")
        password: String,

        @Field("password_confirmation")
        passwordConfirmation: String
    ): LiveData<ApiResponse<RemoteNetworkMessage>>

    /**
     * Sends an activation link to the supplied [email].
     */
    @POST("activate/send")
    @FormUrlEncoded
    @Headers(HEADER_ACCEPT_APPLICATION_JSON)
    fun sendActivationLink(
        @Field("email")
        email: String
    ): LiveData<ApiResponse<RemoteNetworkMessage>>

    /**
     * Sends a password reset link to the supplied [email].
     */
    @POST("password/email")
    @FormUrlEncoded
    @Headers(HEADER_ACCEPT_APPLICATION_JSON)
    fun sendPasswordResetLink(
        @Field("email")
        email: String
    ): LiveData<ApiResponse<RemoteNetworkMessage>>

    // endregion Methods

}
