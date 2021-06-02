package com.bangkit.farmacode.onboarding.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bangkit.farmacode.databinding.FragmentOnBoarding3Binding
import com.bangkit.farmacode.scanner.ScannerActivity

class OnBoardingFragment3 : Fragment() {

    private var _binding: FragmentOnBoarding3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOnBoarding3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Finish
        binding.ob3Finish.setOnClickListener {
            // Set sharedPreferences
            onBoardingFinished()

            // Navigate to ScannerActivity
            val intent = Intent(activity, ScannerActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun onBoardingFinished() {
        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

}