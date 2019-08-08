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

import com.codepunk.core.presentation.auth.*
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
    @ContributesAndroidInjector(modules = [AuthFragmentModule::class])
    abstract fun contributeAuthFragmentInjector(): AuthFragment

    /**
     * Contributes an AndroidInjector to [ForgotPasswordFragment].
     */
    @FragmentScope
    @ContributesAndroidInjector(modules = [ForgotPasswordFragmentModule::class])
    abstract fun contributeForgotPasswordFragmentInjector(): ForgotPasswordFragment

    /**
     * Contributes an AndroidInjector to [LogInFragment].
     */
    @FragmentScope
    @ContributesAndroidInjector(modules = [LogInFragmentModule::class])
    abstract fun contributeLogInFragmentInjector(): LogInFragment

    /**
     * Contributes an AndroidInjector to [SignUpFragment].
     */
    @FragmentScope
    @ContributesAndroidInjector(modules = [SignUpFragmentModule::class])
    abstract fun contributeSignUpFragmentInjector(): SignUpFragment

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
