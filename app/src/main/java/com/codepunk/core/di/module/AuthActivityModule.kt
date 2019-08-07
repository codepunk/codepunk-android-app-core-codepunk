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

package com.codepunk.core.di.module

import com.codepunk.core.presentation.auth.AuthActivity
import com.codepunk.core.presentation.auth.AuthFragment
import com.codepunk.doofenschmirtz.di.scope.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * A [Module] for injecting dependencies into [AuthActivity].
 */
@Suppress("UNUSED")
@Module
abstract class AuthActivityModule {

    // region Methods

    /**
     * Contributes an AndroidInjector to [AuthFragment].
     */
    @FragmentScope
    @ContributesAndroidInjector(
        modules = [
            AuthFragmentModule::class,
            ForgotPasswordFragmentModule::class,
            LogInFragmentModule::class,
            SignUpFragmentModule::class
        ]
    )
    abstract fun contributeAuthFragmentInjector(): AuthFragment

    // endregion Methods

    // region Companion object

    @Module
    companion object {

        /*
        @JvmStatic
        @Provides
        @ActivityScope
        fun providesSomething(): String = "Hello"
        */

    }

    // endregion Companion object

}
