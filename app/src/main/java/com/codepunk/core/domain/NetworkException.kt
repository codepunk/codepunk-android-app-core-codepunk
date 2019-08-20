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

package com.codepunk.core.domain

import android.annotation.TargetApi
import android.os.Build
import com.codepunk.core.domain.model.NetworkMessage

/**
 * An exception that is generated from an error (or other) message returned by the network.
 */
@Suppress("UNUSED")
class NetworkException : RuntimeException {

    // region Properties

    /**
     * The [NetworkMessage] returned by the network.
     */
    @Suppress("WEAKER_ACCESS")
    val networkMessage: NetworkMessage?

    // endregion Properties

    // region Constructors

    constructor(networkMessage: NetworkMessage?) : super() {
        this.networkMessage = networkMessage
    }

    constructor(networkMessage: NetworkMessage?, message: String?) : super(message) {
        this.networkMessage = networkMessage
    }

    constructor(
        networkMessage: NetworkMessage?,
        message: String?,
        cause: Throwable?
    ) : super(message, cause) {
        this.networkMessage = networkMessage
    }

    constructor(networkMessage: NetworkMessage?, cause: Throwable?) : super(cause) {
        this.networkMessage = networkMessage
    }

    @TargetApi(Build.VERSION_CODES.N)
    constructor(
        networkMessage: NetworkMessage?,
        message: String?,
        cause: Throwable?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(message, cause, enableSuppression, writableStackTrace) {
        this.networkMessage = networkMessage
    }

    // endregion Constructors

}
