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

import com.codepunk.core.domain.model.OAuthToken
import com.codepunk.core.domain.model.User

class Session(

    /**
     * The [OAuthToken] received when authenticating the current [User].
     */
    val token: OAuthToken? = null,

    /**
     * The currently-authenticated [User] (if one exists)
     */
    val user: User? = null

) {

    // region Inherited methods

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Session) return false

        if (token != other.token) return false
        if (user != other.user) return false

        return true
    }

    override fun hashCode(): Int {
        var result = token?.hashCode() ?: 0
        result = 31 * result + (user?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Session(token=$token, user=$user)"
    }

    // endregion Inherited methods

    // region Companion object

    companion object {

        // region Properties

        val UNAUTHENTICATED_SESSION = Session()

        // endregion Properties

    }

    // endregion Companion object

}
