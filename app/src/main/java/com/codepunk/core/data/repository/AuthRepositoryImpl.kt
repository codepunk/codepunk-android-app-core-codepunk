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
import com.codepunk.core.data.remote.entity.RemoteOAuthToken
import com.codepunk.core.data.remote.entity.RemoteUser
import com.codepunk.core.data.remote.webservice.AuthWebservice
import com.codepunk.core.data.remote.webservice.UserWebservice
import com.codepunk.core.domain.model.Message
import com.codepunk.core.domain.model.OAuthToken
import com.codepunk.core.domain.repository.AuthRepository
import com.codepunk.doofenschmirtz.borrowed.android.example.github.AppExecutors
import com.codepunk.doofenschmirtz.borrowed.android.example.github.util.AbsentLiveData
import com.codepunk.doofenschmirtz.borrowed.modified.example.github.api.ApiErrorResponse
import com.codepunk.doofenschmirtz.borrowed.modified.example.github.api.ApiResponse
import com.codepunk.doofenschmirtz.borrowed.modified.example.github.api.ApiSuccessResponse
import com.codepunk.doofenschmirtz.borrowed.modified.example.github.repository.NetworkBoundResource
import com.codepunk.doofenschmirtz.borrowed.modified.example.github.vo.Resource
import com.codepunk.doofenschmirtz.inator.loginator.FormattingLoginator
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Implementation of [AuthRepository] that authenticates a user and performs other token-
 * related functions.
 */
class AuthRepositoryImpl(

    /**
     * The application [SharedPreferences].
     */
    private val sharedPreferences: SharedPreferences,

    /**
     * An instance of [AppExecutors] for maing auth-related API calls.
     */
    private val appExecutors: AppExecutors,

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
    private val userDao: UserDao,

    /**
     * An instance of [FormattingLoginator] for logging messages.
     */
    private val loginator: FormattingLoginator

) : AuthRepository {

    // region Properties

    // endregion Properties

    // region Implemented methods
    override fun authenticate(
        usernameOrEmail: String,
        password: String
    ): LiveData<Resource<OAuthToken>> = AuthenticateResource(
        appExecutors,
        sharedPreferences,
        authWebservice,
        userWebservice,
        userDao,
        loginator,
        usernameOrEmail,
        password
    ).asLiveData()

    override fun register(
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): LiveData<Resource<Message>> {
        TODO("not implemented")
    }

    override fun sendActivationLink(email: String): LiveData<Resource<Message>> {
        TODO("not implemented")
    }

    override fun sendPasswordResetLink(email: String): LiveData<Resource<Message>> {
        TODO("not implemented")
    }

    // endregion Implemented methods

    // region Nested/inner classes

    private class AuthenticateResource(
        appExecutors: AppExecutors,
        private val sharedPreferences: SharedPreferences,
        private val authWebservice: AuthWebservice,
        private val userWebservice: UserWebservice,
        private val userDao: UserDao,
        private val loginator: FormattingLoginator,
        private val usernameOrEmail: String,
        private val password: String
    ) : NetworkBoundResource<OAuthToken, Pair<RemoteOAuthToken, RemoteUser>>(
        appExecutors
    ) {

        // region Properties

        /**
         * A [MutableLiveData] to hold the "saved" [OAuthToken].
         */
        //private val persistedData = MutableLiveData<OAuthToken>()
        private var savedOAuthToken: RemoteOAuthToken? = null

        // endregion Properties

        // region Inherited methods

        override fun saveCallResult(item: Pair<RemoteOAuthToken, RemoteUser>) {
            // Worker thread
            savedOAuthToken = item.first
            val id = userDao.upsert(item.second.toLocal())
            sharedPreferences.edit()
                .putString(BuildConfig.PREF_KEY_AUTHENTICATED_USERNAME, item.second.username)
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

        override fun createCall(): LiveData<ApiResponse<Pair<RemoteOAuthToken, RemoteUser>>> {
            // Main thread
            val data = MutableLiveData<ApiResponse<Pair<RemoteOAuthToken, RemoteUser>>>()

            authWebservice.authenticate(usernameOrEmail, password).observeForever { authResponse ->
                when (authResponse) {
                    is ApiSuccessResponse -> {
                        val token = authResponse.body
                        userWebservice.getUser(token.authToken).observeForever { userResponse ->
                            when (userResponse) {
                                is ApiSuccessResponse -> {
                                    val pair: Pair<RemoteOAuthToken, RemoteUser> =
                                        Pair(token, userResponse.body)
                                    val apiResponse = ApiResponse.create(Response.success(pair))
                                    data.value = apiResponse
                                }
                                is ApiErrorResponse -> {
                                    data.value = ApiErrorResponse(userResponse)
                                }
                            }
                        }
                    }
                    is ApiErrorResponse -> {
                        data.value = ApiErrorResponse(authResponse)
                    }
                }
            }

            return data
        }

        // endregion Inherited methods

    }

}
