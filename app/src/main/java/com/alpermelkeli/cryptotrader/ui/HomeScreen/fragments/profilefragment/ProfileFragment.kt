package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.profilefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.databinding.FragmentProfileBinding
import com.alpermelkeli.cryptotrader.ui.HomeScreen.HomeScreen

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.btnProfileSettings.setOnClickListener {

        }

        binding.btnApiSettings.setOnClickListener {
            navigateToApiSettings()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? HomeScreen)?.showBottomNavigationView()
    }



    private fun navigateToApiSettings() {
        findNavController().navigate(R.id.action_profileFragment_to_apiSettingsFragment)
        (activity as? HomeScreen)?.hideBottomNavigationView()
    }
}
