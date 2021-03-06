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

package com.codepunk.core.data.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codepunk.core.BuildConfig
import com.codepunk.core.data.local.dao.UserDao
import com.codepunk.core.data.mapper.toDomain
import com.codepunk.core.data.mapper.toLocal
import com.codepunk.core.data.remote.entity.RemoteNetworkMessage
import com.codepunk.core.data.remote.entity.RemoteOAuthToken
import com.codepunk.core.data.remote.entity.RemoteSession
import com.codepunk.core.data.remote.webservice.AuthWebservice
import com.codepunk.core.data.remote.webservice.UserWebservice
import com.codepunk.core.domain.NetworkException
import com.codepunk.core.domain.model.NetworkMessage
import com.codepunk.core.domain.model.OAuthToken
import com.codepunk.core.domain.repository.AuthRepository
import com.codepunk.doofenschmirtz.borrowed.android.example.github.AppExecutors
import com.codepunk.doofenschmirtz.borrowed.android.example.github.api.ApiErrorResponse
import com.codepunk.doofenschmirtz.borrowed.android.example.github.api.ApiResponse
import com.codepunk.doofenschmirtz.borrowed.android.example.github.api.ApiSuccessResponse
import com.codepunk.doofenschmirtz.borrowed.android.example.github.repository.NetworkBoundResource
import com.codepunk.doofenschmirtz.borrowed.android.example.github.util.AbsentLiveData
import com.codepunk.doofenschmirtz.borrowed.android.example.github.vo.Resource
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Implementation of [AuthRepository] that authenticates a user and performs other token-
 * related functions.
 */
class AuthRepositoryImpl(

    /**
     * An instance of [AppExecutors] for maing auth-related API calls.
     */
    private val appExecutors: AppExecutors,

    /**
     * The application [SharedPreferences].
     */
    private val sharedPreferences: SharedPreferences,

    /**
     * An instance of [Retrofit] for error deserialization.
     */
    private val retrofit: Retrofit,

    /**
     * An instance of [AuthWebservice] for making auth-related API calls.
     */
    private val authWebservice: AuthWebservice,

    /**
     * An instance of [UserWebservice] for making user-related API calls.
     */
    private val userWebservice: UserWebservice,

    /**
     * An instance of [UserDao] for storing the currently-authenticated user in the local database.
     */
    private val userDao: UserDao

) : AuthRepository {

    // region Properties

    // endregion Properties

    // region Implemented methods

    override fun authenticate(
        usernameOrEmail: String,
        password: String
    ): LiveData<Resource<OAuthToken>> = AuthenticateResource(
        usernameOrEmail,
        password
    ).asLiveData()

    override fun register(
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): LiveData<Resource<NetworkMessage>> {
        TODO("not implemented")
    }

    override fun sendActivationLink(email: String): LiveData<Resource<NetworkMessage>> {
        TODO("not implemented")
    }

    override fun sendPasswordResetLink(email: String): LiveData<Resource<NetworkMessage>> {
        TODO("not implemented")
    }

    // endregion Implemented methods

    // region Nested/inner classes

    private inner class AuthenticateResource(
        private val usernameOrEmail: String,
        private val password: String
    ) : NetworkBoundResource<OAuthToken, RemoteSession>(appExecutors) {

        // region Properties

        /**
         * A [MutableLiveData] to hold the "saved" [OAuthToken].
         */
        //private val persistedData = MutableLiveData<OAuthToken>()
        private var savedOAuthToken: RemoteOAuthToken? = null

        // endregion Properties

        // region Inherited methods

        override fun saveCallResult(item: RemoteSession) {
            // Worker thread
            savedOAuthToken = item.token
            val id = userDao.upsert(item.user.toLocal())
            sharedPreferences.edit()
                .putString(BuildConfig.PREF_KEY_AUTHENTICATED_USERNAME, item.user.username)
                .apply()
        }

        override fun shouldFetch(data: OAuthToken?): Boolean = true

        override fun loadFromDb(): LiveData<OAuthToken> {
            return savedOAuthToken?.let {
                MutableLiveData<OAuthToken>().apply {
                    value = it.toDomain()
                }
            } ?: AbsentLiveData.create()
        }

        override fun createCall(): LiveData<ApiResponse<RemoteSession>> {
            // Main thread
            val data = MutableLiveData<ApiResponse<RemoteSession>>()

            authWebservice.authenticate(usernameOrEmail, password).observeForever { authResponse ->
                when (authResponse) {
                    is ApiSuccessResponse -> {
                        val token = authResponse.body
                        userWebservice.getUser(token.authToken).observeForever { userResponse ->
                            when (userResponse) {
                                is ApiSuccessResponse -> {
                                    val session = RemoteSession(token, userResponse.body)
                                    val apiResponse = ApiResponse.create(Response.success(session))
                                    data.value = apiResponse
                                }
                                is ApiErrorResponse -> {
                                    data.value = ApiErrorResponse(userResponse.response)
                                }
                            }
                        }
                    }
                    is ApiErrorResponse -> {
                        data.value = ApiErrorResponse(authResponse.response)
                    }
                }
            }

            return data
        }

        override fun onFetchFailed(response: ApiErrorResponse<RemoteSession>): Throwable {
            val remoteNetworkMessage: RemoteNetworkMessage? =
                response.response?.errorBody()?.let { errorBody ->
                    retrofit.responseBodyConverter<RemoteNetworkMessage>(
                        RemoteNetworkMessage::class.java,
                        arrayOf()
                    ).convert(errorBody)
                }
            val networkMessage = remoteNetworkMessage?.toDomain()
            return NetworkException(networkMessage, networkMessage?.text, response.error)
        }

        // endregion Inherited methods

    }

}
