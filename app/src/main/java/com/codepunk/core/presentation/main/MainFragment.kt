/*
 * Copyright (C) 2019 Codepunk, LLC
 * Author(s): Scott Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepunk.core.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.OnClickListener
import androidx.databinding.DataBindingUtil
import com.codepunk.core.BuildConfig.ACTION_SETTINGS
import com.codepunk.core.R
import com.codepunk.core.databinding.FragmentMainBinding
import com.codepunk.doofenschmirtz.util.loginator.FormattingLoginator
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

// TODO All the things with connecting to Session, repository etc.

/**
 * The main [Fragment] for the Codepunk app.
 */
class MainFragment:
    Fragment(),
    OnClickListener {

    // region Properties

    /**
     * The application [FormattingLoginator].
     */
    @Suppress("UNUSED")
    @Inject
    lateinit var loginator: FormattingLoginator

    /**
     * The binding for this fragment.
     */
    private lateinit var binding: FragmentMainBinding

    // endregion Properties

    // region Lifecycle methods

    // region Lifecycle methods

    /**
     * Injects dependencies into this fragment.
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        if (activity !is MainActivity)
            throw IllegalStateException(
                "${javaClass.simpleName} must be attached to " +
                    MainActivity::class.java.simpleName
            )
    }

    /**
     * Indicates that this fragment has an options menu.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /**
     * Inflates the view.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )
        return binding.root
    }

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Creates the options menu.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Handles menu selections.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                TODO("You need to make settings screens")
                /*
                startActivity(Intent(ACTION_SETTINGS))
                true
                */
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Updates the view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logInOutBtn.setOnClickListener(this)

        /* TODO
        sessionResolvinator = SessionResolvinator(view)
        sessionManager.sessionLiveResource.observe(
            this,
            Observer { sessionResolvinator.resolve(it) }
        )

        // Try to silently obtain a session now
        if (savedInstanceState == null) {
            sessionManager.getSession(silentMode = true, refresh = true)
        }
        */
    }

    // endregion Inherited methods

    // region Implemented methods

    /**
     * Handles click events.
     */
    override fun onClick(v: View?) {
        when (v) {
            binding.logInOutBtn -> {
                /*
                when (sessionManager.session) {
                    null -> sessionManager.getSession(silentMode = false, refresh = true)
                    else -> sessionManager.closeSession(true)
                }
                */
            }
        }
    }

    // endregion Implemented methods

}
