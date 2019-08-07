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

package com.codepunk.core.presentation.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepunk.core.R
import com.codepunk.core.databinding.FragmentAuthBinding
import com.codepunk.doofenschmirtz.util.CustomDividerItemDecoration

/**
 * A simple [Fragment] subclass.
 */
class AuthFragment :
    Fragment(),
    OnClickListener {

    // region Properties

    /**
     * The binding for this fragment.
     */
    private lateinit var binding: FragmentAuthBinding

    // endregion Properties

    // region Lifecycle methods

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
            R.layout.fragment_auth,
            container,
            false
        )
        return binding.root
    }

    // endregion Lifecycle methods

    // region Inherited methods

    /**
     * Sets up views and listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpBtn.setOnClickListener(this)
        binding.loginBtn.setOnClickListener(this)
        val itemDecoration =
            CustomDividerItemDecoration(requireContext(), RecyclerView.VERTICAL, false).apply {
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.divider_item_decoration,
                    requireContext().theme
                )?.let { drawable ->
                    setDrawable(drawable)
                }
            }
        binding.accountsRecycler.addItemDecoration(itemDecoration)
        binding.accountsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        /* TODO
        adapter = AccountAdapter(binding.accountsRecycler.context, accountManager, this)
        binding.accountsRecycler.adapter = adapter
        */

        view.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (binding.accountsRecycler.maxHeight == Integer.MAX_VALUE) {
                binding.accountsRecycler.maxHeight = binding.accountsRecycler.height
            }
        }
    }

    // endregion Inherited methods

    // region Implemented methods

    /**
     * Handles click events.
     */
    override fun onClick(v: View?) {
        when (v) {
            binding.signUpBtn -> Navigation.findNavController(v).navigate(
                R.id.action_auth_to_sign_up
            )
            binding.loginBtn -> Navigation.findNavController(v).navigate(
                R.id.action_auth_to_log_in
            )
            /* TODO
            else -> AccountViewHolder.of(v)?.run {
                onAccountClick(this.account)
            }
            */
        }
    }

    // endregion Implemented methods

}
