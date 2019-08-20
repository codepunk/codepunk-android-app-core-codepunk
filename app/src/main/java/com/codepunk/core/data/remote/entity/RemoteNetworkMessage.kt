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

package com.codepunk.core.data.remote.entity

import com.squareup.moshi.Json

/**
 * A response returned from the network. This response optionally contains a [text]
 * string, an [errorType], [errorDescription], a [hint] detailing possible steps to mitigate the
 * error, and an optional map of field-specific [errors].
 */
data class RemoteNetworkMessage(

    /**
     * A message string.
     */
    @field:Json(name = "message")
    val text: String?,

    /**
     * The type of the error that was encountered.
     */
    @field:Json(name = "error")
    val errorType: RemoteErrorType?,

    /**
     * A description of any encountered [errorType].
     */
    @field:Json(name = "error_description")
    val errorDescription: String?,

    /**
     * A hint that describes possible remedies for any encountered [errorType].
     */
    val hint: String?,

    /**
     * Any errors (relating to specific keys) that were discovered during the request.
     */
    val errors: Map<String, Array<String>>?

) {

    // region Nested/inner classes

    /**
     * An enumerated class describing the type of error represented in this [RemoteNetworkMessage].
     */
    enum class RemoteErrorType {

        /**
         * An error type that results from attempting to authenticate or log in to a user account
         * that is currently inactive.
         */
        @Json(name = "codepunk::activatinator.inactive_user")
        INACTIVE_USER,

        /**
         * An error type that results from attempting to authenticate or log in to a user account
         * using invalid credentials.
         */
        @Json(name = "invalid_credentials")
        INVALID_CREDENTIALS,

        /**
         * An error type that results from attempting to authenticate or log in to a user account
         * with an invalid request (i.e. not supplying a password, or other required field etc.)
         */
        @Json(name = "invalid_request")
        INVALID_REQUEST

    }

    // endregion Nested/inner classes

}
