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

package com.codepunk.core.domain.model

import android.os.Parcel
import android.os.Parcelable

/**
 * A response returned from the network. This response optionally contains a [text]
 * string, an [errorType], [errorDescription], a [hint] detailing possible steps to mitigate the
 * error, and an optional map of field-specific [errors].
 */
data class NetworkMessage(

    /**
     * A message string.
     */
    val text: String?,

    /**
     * The type of the error that was encountered.
     */
    val errorType: ErrorType?,

    /**
     * A description of any encountered [errorType].
     */
    val errorDescription: String?,

    /**
     * A hint that describes possible remedies for any encountered [errorType].
     */
    val hint: String?,

    /**
     * Any errors (relating to specific keys) that were discovered during the request.
     */
    val errors: Map<String, Array<String>>?

) : Parcelable {

    // region Constructors

    /**
     * Constructor that takes a [Parcel].
     */
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readSerializable() as ErrorType?,
        parcel.readString(),
        parcel.readString(),
        readErrors(parcel)
    )

    // endregion Constructors

    // region Implemented methods

    /**
     * Flattens this object in to the supplied [parcel]. Implementation of [Parcelable].
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeSerializable(errorType)
        parcel.writeString(errorDescription)
        parcel.writeString(hint)
        writeErrors(parcel, errors)
    }

    /**
     * Describes the kinds of special objects contained in this [Parcelable] instance's marshaled
     * representation. Implementation of [Parcelable].
     */
    override fun describeContents(): Int = 0

    // endregion Implemented methods

    // region Nested/inner classes

    /**
     * An enumerated class describing the type of error represented in this [NetworkMessage].
     */
    enum class ErrorType {

        /**
         * An error type that results from attempting to authenticate or log in to a user account
         * that is currently inactive.
         */
        INACTIVE_USER,

        /**
         * An error type that results from attempting to authenticate or log in to a user account
         * using invalid credentials.
         */
        INVALID_CREDENTIALS,

        /**
         * An error type that results from attempting to authenticate or log in to a user account
         * with an invalid request (i.e. not supplying a password, or other required field etc.)
         */
        INVALID_REQUEST

    }

    // endregion Nested/inner classes

    companion object {

        // region Properties

        /**
         * A public CREATOR field that generates instances of [NetworkMessage] from a [Parcel].
         */
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<NetworkMessage> =
            object : Parcelable.Creator<NetworkMessage> {

                // region Methods

                /**
                 * Implementation of [Parcelable.Creator]. Create a new instance of
                 * [NetworkMessage], instantiating it from the given [Parcel] whose data had
                 * previously been written by [Parcelable.writeToParcel].
                 */
                override fun createFromParcel(source: Parcel): NetworkMessage = NetworkMessage(source)

                /**
                 * Implementation of [Parcelable.Creator]. Create a new array of [NetworkMessage].
                 */
                override fun newArray(size: Int): Array<NetworkMessage?> = arrayOfNulls(size)

                // endregion methods

            }

        // endregion Properties

        // region Methods

        /**
         * Reads the [errors] map from the supplied [parcel].
         */
        @JvmStatic
        private fun readErrors(parcel: Parcel): Map<String, Array<String>>? {
            val hasErrors = parcel.readByte().toInt() != 0
            return if (hasErrors) {
                val size = parcel.readInt()
                HashMap<String, Array<String>>(size).apply {
                    parcel.readString()?.also { key ->
                        val value = Array(parcel.readInt()) { "" }
                        parcel.readStringArray(value)
                        put(key, value)
                    }
                }
            } else {
                null
            }
        }

        /**
         * Writes the [errors] map to the supplied [parcel].
         */
        @JvmStatic
        private fun writeErrors(parcel: Parcel, errors: Map<String, Array<String>>?) {
            errors?.also {
                parcel.writeByte(1)
                parcel.writeInt(it.size)
                it.entries.forEach { entry ->
                    parcel.writeString(entry.key)
                    parcel.writeInt(entry.value.size)
                    parcel.writeStringArray(entry.value)
                }
            } ?: parcel.writeByte(0)
        }

        // endregion methods

    }

    // endregion Companion object

}
