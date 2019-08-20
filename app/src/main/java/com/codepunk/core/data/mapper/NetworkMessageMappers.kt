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

package com.codepunk.core.data.mapper

import com.codepunk.core.data.remote.entity.RemoteNetworkMessage
import com.codepunk.core.data.remote.entity.RemoteNetworkMessage.RemoteErrorType
import com.codepunk.core.domain.model.NetworkMessage
import com.codepunk.core.domain.model.NetworkMessage.ErrorType

/**
 * Converts a [RemoteErrorType] to a domain [ErrorType].
 */
fun RemoteErrorType.toDomain(): ErrorType =
    when (this) {
        RemoteErrorType.INACTIVE_USER -> ErrorType.INACTIVE_USER
        RemoteErrorType.INVALID_CREDENTIALS -> ErrorType.INVALID_CREDENTIALS
        RemoteErrorType.INVALID_REQUEST -> ErrorType.INVALID_REQUEST
    }

/**
 * Converts a [RemoteNetworkMessage] to a domain [NetworkMessage].
 */
fun RemoteNetworkMessage.toDomain(): NetworkMessage =
    NetworkMessage(
        text,
        errorType?.toDomain(),
        errorDescription,
        hint,
        errors?.toMap()
    )
