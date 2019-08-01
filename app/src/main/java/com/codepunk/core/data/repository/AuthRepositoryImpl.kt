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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codepunk.core.data.mapper.toDomain
import com.codepunk.core.data.remote.entity.RemoteAuthentication
import com.codepunk.core.data.remote.webservice.AuthWebservice
import com.codepunk.core.domain.model.Authentication
import com.codepunk.core.domain.model.Message
import com.codepunk.core.domain.repository.AuthRepository
import com.codepunk.doofenschmirtz.borrowed.android.example.github.AppExecutors
import com.codepunk.doofenschmirtz.borrowed.android.example.github.api.ApiResponse
import com.codepunk.doofenschmirtz.borrowed.android.example.github.repository.NetworkBoundResource
import com.codepunk.doofenschmirtz.borrowed.android.example.github.util.AbsentLiveData
import com.codepunk.doofenschmirtz.borrowed.android.example.github.util.LiveDataCallAdapter
import com.codepunk.doofenschmirtz.borrowed.android.example.github.vo.Resource
import retrofit2.Retrofit

/**
 * Implementation of [AuthRepository] that authenticates a user and performs other authentication-
 * related functions.
 */
class AuthRepositoryImpl(

    /**
     * An instance of [AppExecutors] for maing auth-related API calls.
     */
    private val appExecutors: AppExecutors,

    /**
     * An instance of [AuthWebservice] for making auth-related API calls.
     */
    private val authWebservice: AuthWebservice,

    /**
     * An instance of [Retrofit] for error deserialization.
     */
    private val retrofit: Retrofit

) : AuthRepository {

    // region Properties

    // endregion Properties

    // region Implemented methods
    override fun authenticate(
        usernameOrEmail: String,
        password: String
    ): LiveData<Resource<Authentication>> = AuthenticateResource(
        appExecutors,
        authWebservice,
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
        private val authWebservice: AuthWebservice,
        private val usernameOrEmail: String,
        private val password: String
    ) : NetworkBoundResource<Authentication, RemoteAuthentication>(
        appExecutors
    ) {

        // region Properties

        /**
         * A [MutableLiveData] to hold the "saved" [Authentication].
         */
        private val persistedLiveData: MutableLiveData<Authentication> =
            MutableLiveData<Authentication>().apply {
                value = null
            }

        // endregion Properties

        // region Inherited methods

        override fun saveCallResult(item: RemoteAuthentication) {
            // TODO Need to get username if it's an email
            val username = usernameOrEmail
            /*
            val username: String? =
                when (Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
                    true -> getRemoteUser(
                        userWebservice,
                        remoteAuthentication?.authToken
                    )?.username
                    else -> usernameOrEmail
                }
             */
            persistedLiveData.postValue(item.toDomain(username))
        }

        override fun shouldFetch(data: Authentication?): Boolean = true

        override fun loadFromDb(): LiveData<Authentication> {
            // Cast persistedLiveData as a nullable class because the first time loadFromDb is
            // called, persistedLiveData has not yet been initialized despite being non-nullable
            return (persistedLiveData as MutableLiveData<Authentication>?)
                ?: AbsentLiveData.create()
        }

        override fun createCall(): LiveData<ApiResponse<RemoteAuthentication>> =
            authWebservice.authenticate(usernameOrEmail, password)

        // endregion Inherited methods

    }

    // endregion Nested/inner classes

}
