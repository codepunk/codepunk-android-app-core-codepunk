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

package com.codepunk.core.domain.session

import androidx.lifecycle.MediatorLiveData
import com.codepunk.core.domain.model.User
import com.codepunk.core.domain.repository.AuthRepository
import com.codepunk.core.domain.session.Session.Companion.UNAUTHENTICATED_SESSION
import com.codepunk.doofenschmirtz.borrowed.android.example.github.vo.Resource
import com.codepunk.doofenschmirtz.borrowed.android.example.github.vo.Status
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton class that manages the currently-authenticated [Session] via a [Resource] wrapped
 * in a [MediatorLiveData].
 */
@Singleton
class SessionManager @Inject constructor(

    /**
     * The [AuthRepository] for the application.
     */
    private val authRepository: AuthRepository

) {

    // region Properties

    /**
     * A [MediatorLiveData] containing the current [Session] (which may be a value of
     * [Session.UNAUTHENTICATED_SESSION].
     */
    val sessionResourceLiveData: MediatorLiveData<Resource<Session>> =
        MediatorLiveData<Resource<Session>>().apply {
            value = Resource.success(UNAUTHENTICATED_SESSION)
        }

    /**
     * Returns whether a currently-authenticated [User] exists.
     */
    val authenticated: Boolean
        get() = sessionResourceLiveData.value?.let {
            when {
                it.status != Status.SUCCESS -> false
                it.data == null -> false
                it.data == UNAUTHENTICATED_SESSION -> false
                else -> true
            }
        } ?: false

    // endregion Properties

    // region Methods

    // TODO Will have all auth methods like authenticate, refreshToken, and others like logout, etc.

    fun login(
        usernameOrEmail: String,
        password: String
    ) {

    }

    // endregion Methods

    // region Nested/inner classes


    // endregion Nested/inner classes

}
